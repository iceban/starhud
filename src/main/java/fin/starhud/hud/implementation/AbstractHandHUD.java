package fin.starhud.hud.implementation;

import fin.starhud.config.hud.HandSettings;
import fin.starhud.helper.Box;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

// i dont like this one.

public abstract class AbstractHandHUD extends AbstractHUD {

    private static final Identifier ITEM_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/item.png");

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13;

    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private static final int ITEM_TEXTURE_WIDTH = 3 + 16 + 3;
    private static final int ITEM_TEXTURE_HEIGHT = 3 + 16 + 3;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private final HandSettings handSettings;
    private final Arm arm;
    private final Identifier ICON_TEXTURE;

    public AbstractHandHUD(HandSettings handSettings, Arm arm, Identifier ICON_TEXTURE) {
        super(handSettings.base);
        this.handSettings = handSettings;
        this.arm = arm;
        this.ICON_TEXTURE = ICON_TEXTURE;
    }

    @Override
    public boolean shouldRender() {
        return super.shouldRender()
                && !CLIENT.player.getStackInArm(arm).isEmpty()
                && (handSettings.showDurability || handSettings.showCount);

    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        return renderHandHUD(context, arm, x, y);
    }

    public boolean renderHandHUD(DrawContext context, Arm arm, int x, int y) {
        ItemStack item = CLIENT.player.getStackInArm(arm);

        // either draw the durability or the amount of item in the inventory.
        if (handSettings.showDurability && item.isDamageable()) {
            Box box = RenderUtils.renderDurabilityHUD(
                    context,
                    item,
                    ICON_TEXTURE,
                    x, y,
                    0.0F, 0.0F,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT,
                    ICON_WIDTH, ICON_HEIGHT,
                    handSettings.color | 0xFF000000,
                    handSettings.drawBar,
                    handSettings.drawItem,
                    handSettings.base.growthDirectionX,
                    handSettings.base.growthDirectionY
            );
            copyBoundingBox(box);
        } else if (handSettings.showCount) {
            renderStackCountHUD(context, item, x, y, 0.0F, handSettings.color | 0xFF000000);
        }

        return true;
    }

    private void renderStackCountHUD(DrawContext context, ItemStack stack, int x, int y, float v, int color) {
        if (handSettings.drawItem) {
            renderStackCountItemHUD(context, stack, x, y, color);
        } else {
            renderStackCountIconHUD(context, stack, x, y, color);
        }
    }

    private void renderStackCountItemHUD(DrawContext context, ItemStack stack, int x, int y, int color) {
        int stackAmount = getItemCount(CLIENT.player.getInventory(), stack);
        String amountStr = Integer.toString(stackAmount);

        int strWidth = CLIENT.textRenderer.getWidth(amountStr) - 1;

        int width = ITEM_TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
        int height = ITEM_TEXTURE_HEIGHT;

        x -= getSettings().getGrowthDirectionHorizontal(width);
        y -= getSettings().getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height, color);

        RenderUtils.drawTextureHUD(context, ITEM_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, ITEM_TEXTURE_WIDTH, ITEM_TEXTURE_HEIGHT, ITEM_TEXTURE_WIDTH, ITEM_TEXTURE_HEIGHT);
        RenderUtils.fillRoundedRightSide(context, x + ITEM_TEXTURE_WIDTH + 1, y, x + width, y + ITEM_TEXTURE_HEIGHT, 0x80000000);
        context.drawItem(stack, x + 3, y + 3);
        RenderUtils.drawTextHUD(context, amountStr, x + ITEM_TEXTURE_WIDTH + 1 + 5, y + 3, color, false);
    }

    private void renderStackCountIconHUD(DrawContext context, ItemStack stack, int x, int y, int color) {
        int stackAmount = getItemCount(CLIENT.player.getInventory(), stack);

        String amountStr = Integer.toString(stackAmount);
        int strWidth = CLIENT.textRenderer.getWidth(amountStr) - 1;

        int width = ICON_WIDTH + 1 + 5 + strWidth + 5;
        int height = ICON_HEIGHT;

        x -= getSettings().getGrowthDirectionHorizontal(width);
        y -= getSettings().getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height, color);

        RenderUtils.drawSmallHUD(
                context,
                amountStr,
                x, y,
                width, height,
                ICON_TEXTURE,
                0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                color
        );
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
