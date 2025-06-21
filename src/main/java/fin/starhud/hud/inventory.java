package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class inventory {

    private static final Identifier INVENTORY_TEXTURE = Identifier.of("starhud", "hud/inventory.png");
    private static final Identifier INVENTORY_TEXTURE_VERTICAL = Identifier.of("starhud", "hud/inventory_vertical.png");

    private static final int[] SLOT_X_HORIZONTAL = new int[27];
    private static final int[] SLOT_Y_HORIZONTAL = new int[27];

    private static final int[] SLOT_X_VERTICAL = new int[27];
    private static final int[] SLOT_Y_VERTICAL = new int[27];

    private static final Settings.InventorySettings inventorySettings = Main.settings.inventorySettings;

    private static final int TEXTURE_WIDTH = 206;
    private static final int TEXTURE_HEIGHT = 68;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    static {
        preComputeHorizontal();
        preComputeVertical();
    }

    public static void renderInventoryHUD(DrawContext context) {
        if ((inventorySettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (inventorySettings.hideOn.chat && Helper.isChatFocused()))
            return;

        PlayerInventory playerInventory = CLIENT.player.getInventory();

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, inventorySettings.scale);

        if (inventorySettings.drawVertical) {
            int x = Helper.calculatePositionX(inventorySettings.x, inventorySettings.originX, TEXTURE_HEIGHT, inventorySettings.scale);
            int y = Helper.calculatePositionY(inventorySettings.y, inventorySettings.originY, TEXTURE_WIDTH, inventorySettings.scale);
            drawInventoryVertical(playerInventory, CLIENT.textRenderer, context, x, y);
        } else {
            int x = Helper.calculatePositionX(inventorySettings.x, inventorySettings.originX, TEXTURE_WIDTH, inventorySettings.scale);
            int y = Helper.calculatePositionY(inventorySettings.y, inventorySettings.originY, TEXTURE_HEIGHT, inventorySettings.scale);
            drawInventoryHorizontal(playerInventory, CLIENT.textRenderer, context, x, y);
        }

        context.getMatrices().popMatrix();
    }

    private static void drawInventoryVertical(PlayerInventory inventory, TextRenderer textRenderer, DrawContext context, int x, int y) {
        boolean foundItem = false;

        for (int i = 0; i < 27; ++i) {

            ItemStack stack = inventory.getMainStacks().get(i + 9);

            if (!stack.isEmpty()) {

                if (!foundItem) {
                    foundItem = true;
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE_VERTICAL, x, y, 0.0F, 0.0F, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH);
                }

                int x1 = x + SLOT_X_VERTICAL[i];
                int y1 = y + SLOT_Y_VERTICAL[i];

                context.drawItem(stack, x1, y1);
                context.drawStackOverlay(textRenderer, stack, x1, y1);
            }
        }
    }

    private static void drawInventoryHorizontal(PlayerInventory inventory, TextRenderer textRenderer, DrawContext context, int x, int y) {
        boolean foundItem = false;
        for (int itemIndex = 0; itemIndex < 27; ++itemIndex) {

            ItemStack stack = inventory.getMainStacks().get(itemIndex + 9);

            if (!stack.isEmpty()) {

                if (!foundItem) {
                    foundItem = true;
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, x, y, 0.0F, 0.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }

                int x1 = x + SLOT_X_HORIZONTAL[itemIndex];
                int y1 = y + SLOT_Y_HORIZONTAL[itemIndex];

                context.drawItem(stack, x1, y1);
                context.drawStackOverlay(textRenderer, stack, x1, y1);
            }
        }
    }

    private static void preComputeHorizontal() {
        int x1 = 3;
        // 1 offset
        int y1 = -20;

        for (int i = 0; i < 27; ++i) {
            if (i % 9 == 0) {
                y1 += 23;
                x1 = 3;
            }
            SLOT_X_HORIZONTAL[i] = x1;
            SLOT_Y_HORIZONTAL[i] = y1;
            x1 += 23;
        }
    }

    private static void preComputeVertical() {
        // 72 = 23 + 23 + 23 + 3 (2 items to the right + 1 offset)
        int x1 = 72;
        int y1 = 3;

        for (int i = 0; i < 27; ++i) {
            if (i % 9 == 0) {
                y1 = 3;
                x1 -= 23;
            }

            SLOT_X_VERTICAL[i] = x1;
            SLOT_Y_VERTICAL[i] = y1;

            y1 += 23;
        }
    }
}
