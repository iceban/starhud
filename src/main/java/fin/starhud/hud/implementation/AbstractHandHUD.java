package fin.starhud.hud.implementation;

import fin.starhud.config.hud.HandSettings;
import fin.starhud.helper.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

// i dont like this one.

public abstract class AbstractHandHUD extends AbstractDurabilityHUD {

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
        super(handSettings.base, handSettings.durabilitySettings);
        this.handSettings = handSettings;
        this.arm = arm;
        this.ICON_TEXTURE = ICON_TEXTURE;
    }

    @Override
    public ItemStack getStack() {
        return CLIENT.player.getStackInArm(arm);
    }

    private ItemStack item;
    private int iconColor;

    private int width;
    private int height;

    private String amountStr;

    private boolean showDurability;
    private boolean showCount;
    private boolean drawItem;

    @Override
    public boolean collectHUDInformation() {
        item = getStack();
        if (item.isEmpty())
            return false;

        showDurability = handSettings.showDurability;
        showCount = handSettings.showCount;
        drawItem = handSettings.durabilitySettings.drawItem;

        if (showDurability && item.isDamageable()) {
            return super.collectHUDInformation();
        }

        if (!showCount)
            return false;

        iconColor = getIconColor();
        amountStr = Integer.toString(getItemCount(CLIENT.player.getInventory(), item));

        int strWidth = CLIENT.textRenderer.getWidth(amountStr) - 1;
        width = (drawItem ? ITEM_TEXTURE_WIDTH : ICON_WIDTH) + 1 + 5 + strWidth + 5;
        height = drawItem ? ITEM_TEXTURE_HEIGHT : ICON_HEIGHT;

        setWidth(width);
        setHeight(height);

        return true;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        return renderHandHUD(context, x, y);
    }

    public boolean renderHandHUD(DrawContext context, int x, int y) {
        // either draw the durability or the amount of item in the inventory.
        if (handSettings.showDurability && item.isDamageable()) {
            renderDurabilityHUD(
                    context,
                    ICON_TEXTURE,
                    x, y,
                    0.0F, 0.0F,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT,
                    ICON_WIDTH, ICON_HEIGHT
            );
        } else if (handSettings.showCount) {
            renderStackCountHUD(context, x, y);
        }

        return true;
    }

    private void renderStackCountHUD(DrawContext context, int x, int y) {
        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);
        setBoundingBox(x, y, width, height, iconColor);

        if (drawItem) {
            renderStackCountItemHUD(context, x, y);
        } else {
            renderStackCountIconHUD(context, x, y);
        }
    }

    private void renderStackCountItemHUD(DrawContext context, int x, int y) {
        RenderUtils.drawTextureHUD(context, ITEM_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, ITEM_TEXTURE_WIDTH, ITEM_TEXTURE_HEIGHT, ITEM_TEXTURE_WIDTH, ITEM_TEXTURE_HEIGHT);
        RenderUtils.fillRoundedRightSide(context, x + ITEM_TEXTURE_WIDTH + 1, y, x + width, y + ITEM_TEXTURE_HEIGHT, 0x80000000);
        context.drawItem(item, x + 3, y + 3);
        RenderUtils.drawTextHUD(context, amountStr, x + ITEM_TEXTURE_WIDTH + 1 + 5, y + 3, iconColor, false);
    }

    private void renderStackCountIconHUD(DrawContext context, int x, int y) {
        RenderUtils.drawSmallHUD(
                context,
                amountStr,
                x, y,
                width, height,
                ICON_TEXTURE,
                0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                iconColor
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
