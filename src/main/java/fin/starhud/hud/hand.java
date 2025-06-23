package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class hand {

    private static final Identifier HAND_TEXTURE = Identifier.of("starhud", "hud/hand.png");

    // base HUD Width (Icon width = 13, 1 for the gap between icon and text, 5 for the gap on left side of the attribute, 5 for the gap on the right of the attribute).
    // guys i legit forgot what the "10 for the padding at left / right edge" meant help.
    private static final int TEXTURE_WIDTH = 13 + 1 + 5 + 5;
    // 3 x 10 (10 durability bars) + 9 for each gap.
    // durability adds 39 additional width.
    private static final int DURABILITY_WIDTH = 39;
    // count string is at max 4 digits, each digit may have 5 pixels.
    // 5 + 1 + 5 + 1 + 5 + 1 + 5 = 23.
    // count string adds 23 additional width.
    private static final int COUNT_WIDTH = 23;
    private static final int TEXTURE_HEIGHT = 13;

    private static final Settings.HandSettings.LeftHandSettings leftHandSettings = Main.settings.handSettings.leftHandSettings;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderLeftHandHUD(DrawContext context) {
        if (    (leftHandSettings.hideOn.f3 && Helper.isDebugHUDOpen()) ||
                (leftHandSettings.hideOn.chat && Helper.isChatFocused()) ||
                (leftHandSettings.hideOn.bossbar && Helper.isBossBarShown()))
            return;

        PlayerInventory playerInventory = CLIENT.player.getInventory();

        ItemStack item;
        if (CLIENT.player.getMainArm() == Arm.LEFT)
            item = CLIENT.player.getEquippedStack(EquipmentSlot.MAINHAND);
        else
            item = CLIENT.player.getEquippedStack(EquipmentSlot.OFFHAND);

        if (item.isEmpty()) return;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, leftHandSettings.scale);

        // either draw the durability or the amount of item in the inventory.
        if (leftHandSettings.showDurability && item.isDamageable()) {
            int x = Helper.calculatePositionX(leftHandSettings.x, leftHandSettings.originX, TEXTURE_WIDTH, leftHandSettings.scale)
                    - Helper.getGrowthDirection(leftHandSettings.textureGrowth, DURABILITY_WIDTH);
            int y = Helper.calculatePositionY(leftHandSettings.y, leftHandSettings.originY, TEXTURE_HEIGHT, leftHandSettings.scale);
            // Use width_count + width because we are using the same texture used in Count HUD, We don't have the texture for durability.
            Helper.renderItemDurabilityHUD(context, HAND_TEXTURE, item, x, y, 0, COUNT_WIDTH + TEXTURE_WIDTH, 27, leftHandSettings.color | 0xFF000000);
        } else if (leftHandSettings.showCount) {
            int x = Helper.calculatePositionX(leftHandSettings.x, leftHandSettings.originX, TEXTURE_WIDTH, leftHandSettings.scale)
                    - Helper.getGrowthDirection(leftHandSettings.textureGrowth, COUNT_WIDTH);
            int y = Helper.calculatePositionY(leftHandSettings.y, leftHandSettings.originY, TEXTURE_HEIGHT, leftHandSettings.scale);
            renderItemCountHUD(context, CLIENT.textRenderer, playerInventory, item, x, y, 0, leftHandSettings.color | 0xFF000000);
        }

        context.getMatrices().popMatrix();
    }

    private static final Settings.HandSettings.RightHandSettings rightHandSettings = Main.settings.handSettings.rightHandSettings;

    public static void renderRightHandHUD(DrawContext context) {
        if (    (rightHandSettings.hideOn.f3 && Helper.isDebugHUDOpen()) ||
                (rightHandSettings.hideOn.chat && Helper.isChatFocused()) ||
                (rightHandSettings.hideOn.bossbar && Helper.isBossBarShown()))
            return;

        PlayerInventory playerInventory = CLIENT.player.getInventory();

        ItemStack item;
        if (CLIENT.player.getMainArm() == Arm.LEFT)
            item = CLIENT.player.getEquippedStack(EquipmentSlot.OFFHAND);
        else
            item = CLIENT.player.getEquippedStack(EquipmentSlot.MAINHAND);

        if (item.isEmpty()) return;
        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, rightHandSettings.scale);

        // either draw the durability or the amount of item in the inventory.
        if (rightHandSettings.showDurability && item.isDamageable()) {
            int x = Helper.calculatePositionX(rightHandSettings.x, rightHandSettings.originX, TEXTURE_WIDTH, rightHandSettings.scale)
                    - Helper.getGrowthDirection(rightHandSettings.textureGrowth, DURABILITY_WIDTH);
            int y = Helper.calculatePositionY(rightHandSettings.y, rightHandSettings.originY, TEXTURE_HEIGHT, rightHandSettings.scale);
            Helper.renderItemDurabilityHUD(context, HAND_TEXTURE, item, x, y, 14, COUNT_WIDTH + TEXTURE_WIDTH, 27, rightHandSettings.color | 0xFF000000);
        } else if (rightHandSettings.showCount) {
            int x = Helper.calculatePositionX(rightHandSettings.x, rightHandSettings.originX, TEXTURE_WIDTH, rightHandSettings.scale)
                    - Helper.getGrowthDirection(rightHandSettings.textureGrowth, COUNT_WIDTH);
            int y = Helper.calculatePositionY(rightHandSettings.y, rightHandSettings.originY, TEXTURE_HEIGHT, rightHandSettings.scale);
            renderItemCountHUD(context, CLIENT.textRenderer, playerInventory, item, x, y, 14, rightHandSettings.color | 0xFF000000);
        }

        context.getMatrices().popMatrix();
    }

    private static void renderItemCountHUD(DrawContext context, TextRenderer textRenderer, PlayerInventory playerInventory, ItemStack stack, int x, int y, float v, int color) {
        int stackAmount = getItemCount(playerInventory, stack);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, HAND_TEXTURE, x, y, 0.0F, v, COUNT_WIDTH + TEXTURE_WIDTH, TEXTURE_HEIGHT, COUNT_WIDTH + TEXTURE_WIDTH, 27, color);
        context.drawText(textRenderer, Integer.toString(stackAmount), x + 19, y + 3, color, false);
    }

    private static int getItemCount(PlayerInventory inventory, ItemStack stack) {
        int stackAmount = 0;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack item = inventory.getStack(i);
            if (!item.isEmpty() && ItemStack.areItemsAndComponentsEqual(item, stack))
                stackAmount += item.getCount();
        }

        return stackAmount;
    }
}