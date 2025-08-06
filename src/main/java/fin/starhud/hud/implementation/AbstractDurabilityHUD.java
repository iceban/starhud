package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.config.BaseHUDSettings;
import fin.starhud.config.hud.DurabilitySettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractDurabilityHUD extends AbstractHUD {

    protected final DurabilitySettings durabilitySettings;

    private static final Identifier BIG_DURABILITY_TEXTURE = Identifier.of("starhud", "hud/big_durability_bar.png");
    private static final int BIG_DURABILITY_TEXTURE_WIDTH = 70;
    private static final int BIG_DURABILITY_TEXTURE_HEIGHT = 14;

    private static final Identifier BIG_DURABILITY_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/big_durability_background.png");
    private static final int BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH = 78;
    private static final int BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT = 22;

    private static final Identifier DURABILITY_TEXTURE = Identifier.of("starhud", "hud/durability_bar.png");
    private static final int DURABILITY_TEXTURE_WIDTH = 40;
    private static final int DURABILITY_TEXTURE_HEIGHT = 7;

    private static final Identifier DURABILITY_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/durability_background.png");
    private static final int DURABILITY_BACKGROUND_TEXTURE_WIDTH = 49;
    private static final int DURABILITY_BACKGROUND_TEXTURE_HEIGHT = 13;

    private static final Identifier ITEM_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/item.png");
    private static final int ITEM_BACKGROUND_TEXTURE_WIDTH = 3 + 16 + 3;
    private static final int ITEM_BACKGROUND_TEXTURE_HEIGHT = 3 + 16 + 3;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private ItemStack stack;

    private int stackDamage = 0;
    private int stackMaxDamage = 0;

    private int width = 0;
    private int height = 0;

    private String str;
    private String str2;

    private int durabilityColor;
    private int iconColor;
    private int step;

    private int remainingTextWidth;

    private DurabilitySettings.DisplayMode displayMode;
    private boolean drawItem;

    public AbstractDurabilityHUD(BaseHUDSettings baseHUDSettings, DurabilitySettings durabilitySettings) {
        super(baseHUDSettings);
        this.durabilitySettings = durabilitySettings;
    }

    public abstract ItemStack getStack();
    public abstract int getIconColor();

    @Override
    public boolean collectHUDInformation() {
        stack = getStack();

        if (stack.isEmpty() || !stack.isDamageable())
            return false;

        stackDamage = stack.getDamage();
        stackMaxDamage = stack.getMaxDamage();

        displayMode = durabilitySettings.displayMode;
        drawItem = durabilitySettings.drawItem;
        iconColor = getIconColor();

        width = processWidth();
        height = drawItem ? BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT : DURABILITY_BACKGROUND_TEXTURE_HEIGHT;

        durabilityColor = getItemBarColor(stackMaxDamage - stackDamage, stackMaxDamage) | 0xFF000000;

        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);
        setBoundingBox(x, y, width, height, iconColor);

        return true;
    }

    public int processWidth() {
        if (drawItem) {
            return processWidthItem();
        } else {
            return processWidthIcon();
        }
    }

    public int processWidthItem() {
        return switch (displayMode) {
            case BAR -> processWidthItemBar();
            case FRACTIONAL -> processWidthItemNumber();
            case VALUE_ONLY -> processWidthItemValue();
            case PERCENTAGE -> processWidthItemPercentage();
            case COMPACT -> processWidthItemCompact();
        };
    }

    public int processWidthItemBar() {
        step = getItemBarStep(stackDamage, stackMaxDamage, 10);
        durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        return ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH;
    }

    public int processWidthItemNumber() {
        int damage = stackDamage;
        int maxDamage = stackMaxDamage;
        int remaining = maxDamage - damage;

        str = remaining + "/" + maxDamage;

        step = getItemBarStep(stackDamage, stackMaxDamage, 10);

        int strWidth = CLIENT.textRenderer.getWidth(str);
        return ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
    }

    public int processWidthItemValue() {
        int remaining = stackMaxDamage - stackDamage;
        str = Integer.toString(remaining);
        int strWidth = CLIENT.textRenderer.getWidth(str) - 1;

        return ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
    }

    public int processWidthItemPercentage() {
        int remaining = stackMaxDamage - stackDamage;
        int percentage = ((remaining * 100) / stackMaxDamage);

        str = percentage + "%";
        int strWidth = CLIENT.textRenderer.getWidth(str) - 1;

        return ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
    }

    public int processWidthItemCompact() {
        return ITEM_BACKGROUND_TEXTURE_WIDTH;
    }

    public int processWidthIcon() {
        return switch (displayMode) {
            case BAR -> processWidthIconBar();
            case FRACTIONAL -> processWidthIconNumber();
            case VALUE_ONLY -> processWidthIconValue();
            case PERCENTAGE -> processWidthIconPercentage();
            case COMPACT -> processWidthIconCompact();
        };
    }

    public int processWidthIconBar() {
        step = getItemBarStep(stackDamage, stackMaxDamage, 10);

        return 13 + 1 + DURABILITY_BACKGROUND_TEXTURE_WIDTH;
    }

    public int processWidthIconNumber() {
        int damage = stackDamage;
        int maxDamage = stackMaxDamage;
        int remaining = maxDamage - damage;

        str = Helper.toSuperscript(Integer.toString(remaining)) + '/';
        str2 = Helper.toSubscript(Integer.toString(maxDamage));
        step = getItemBarStep(stackDamage, stackMaxDamage, 10);

        remainingTextWidth = CLIENT.textRenderer.getWidth(str);

        int maxDamageTextWidth = CLIENT.textRenderer.getWidth(str2);

        return 13 + 1 + 5 + remainingTextWidth + maxDamageTextWidth + 5;
    }

    public int processWidthIconValue() {
        int remaining = stackMaxDamage - stackDamage;

        str = Integer.toString(remaining);

        int strWidth = CLIENT.textRenderer.getWidth(str) - 1;

        return 13 + 1 + 5 + strWidth + 5;
    }

    public int processWidthIconPercentage() {

        int remainingDamage = stackMaxDamage - stackDamage;
        int percentage = ((remainingDamage) * 100 / stackMaxDamage);

        str = percentage + "%";
        int strWidth = CLIENT.textRenderer.getWidth(str) - 1;

        return 13 + 1 + 5 + strWidth + 5;
    }

    public int processWidthIconCompact() {
        step = getItemBarStep(stackDamage, stackMaxDamage, 11);
        return 13;
    }

    // get the durability "steps" or progress.
    public static int getItemBarStep(int stackDamage, int stackMaxDamage, int maxStep) {
        return MathHelper.clamp(Math.round(maxStep - (float) stackDamage * maxStep / (float) stackMaxDamage), 0, maxStep);
    }

    // color transition from pastel (red to green).
    public static int getItemBarColor(int stackStep, int maxStep) {
        return MathHelper.hsvToRgb(0.35F * stackStep / (float) maxStep, 0.45F, 0.95F);
    }

    public void renderDurabilityHUD(DrawContext context, Identifier iconTexture, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        if (drawItem) {
            renderItemDurability(context, x , y);
        } else {
            renderDurability(context, iconTexture, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight);
        }
    }

    public void renderItemDurability(DrawContext context, int x, int y) {
        switch (displayMode) {
            case FRACTIONAL, VALUE_ONLY, PERCENTAGE -> RenderUtils.drawItemHUD(context, str, x, y, getWidth(), getHeight(), stack, durabilityColor);
            case BAR -> renderItemDurabilityBar(context, x, y);
            case COMPACT -> renderItemDurabilityCompact(context, x, y);
        }
    }

    public void renderItemDurabilityBar(DrawContext context, int x, int y) {
        // draw background for the item texture
        RenderUtils.fillRoundedLeftSide(context, x, y, x + ITEM_BACKGROUND_TEXTURE_WIDTH, y + ITEM_BACKGROUND_TEXTURE_HEIGHT, 0x80000000);
        // draw background for the big durability bar
        RenderUtils.drawTextureHUD(context, BIG_DURABILITY_BACKGROUND_TEXTURE, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1, y, 0.0F, 0.0F, BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH, BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT, BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH, BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT);

        // draw item
        context.drawItem(stack, x + 3, y + 3);

        // draw the durability bar
        if (step != 0)
            RenderUtils.drawTextureHUD(context, BIG_DURABILITY_TEXTURE, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5, y + 4, 0.0F, 0.0F, step * 7, BIG_DURABILITY_TEXTURE_HEIGHT, BIG_DURABILITY_TEXTURE_WIDTH, BIG_DURABILITY_TEXTURE_HEIGHT, durabilityColor);
    }

    public void renderItemDurabilityCompact(DrawContext context, int x, int y) {
        RenderUtils.fillRounded(context, x, y, x + getWidth(), y + getHeight(), 0x80000000);

        context.drawItem(stack, x + 3, y + 3);
        context.drawStackOverlay(CLIENT.textRenderer, stack, x + 3, y + 3);
    }

    public void renderItemDurabilityNumber(DrawContext context, int x, int y) {
        // draw background
        RenderUtils.fillRoundedLeftSide(context, x, y, x + ITEM_BACKGROUND_TEXTURE_WIDTH, y + ITEM_BACKGROUND_TEXTURE_HEIGHT, 0x80000000);
        RenderUtils.fillRoundedRightSide(context, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1, y, x + getWidth(), y + getHeight(), 0x80000000);

        context.drawItem(stack, x + 3, y + 3);
        RenderUtils.drawTextHUD(context, str, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5, y + 7, durabilityColor, false);
    }

    public void renderDurability(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        switch (displayMode) {
            case FRACTIONAL -> renderDurabilityNumber(context, ICON, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight);
            case BAR -> renderDurabilityBar(context, ICON, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight);
            case VALUE_ONLY, PERCENTAGE -> RenderUtils.drawSmallHUD(context, str, x, y, getWidth(), getHeight(), ICON, u, v, textureWidth, textureHeight, iconWidth, iconHeight, durabilityColor, iconColor);
            case COMPACT -> renderDurabilityCompact(context, ICON, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight);
        }
    }

    public void renderDurabilityBar(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        // draw the icon
        RenderUtils.fillRoundedLeftSide(context, x, y, x + iconWidth, y + iconHeight, 0x80000000);
        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);

        // draw the durability background and steps
        RenderUtils.drawTextureHUD(context, DURABILITY_BACKGROUND_TEXTURE, x + iconWidth + 1, y, 0.0F, 0.0F, DURABILITY_BACKGROUND_TEXTURE_WIDTH, DURABILITY_BACKGROUND_TEXTURE_HEIGHT, DURABILITY_BACKGROUND_TEXTURE_WIDTH, DURABILITY_BACKGROUND_TEXTURE_HEIGHT);
        if (step != 0) RenderUtils.drawTextureHUD(context, DURABILITY_TEXTURE, x + iconWidth + 1 + 5, y + 3, 0, 0, 4 * step, DURABILITY_TEXTURE_HEIGHT, DURABILITY_TEXTURE_WIDTH, DURABILITY_TEXTURE_HEIGHT, this.durabilityColor);
    }

    // example render: ¹²³⁴/₅₆₇₈
    public void renderDurabilityNumber(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {

        RenderUtils.fillRoundedLeftSide(context, x, y, x + iconWidth, y + getHeight(), 0x80000000);
        RenderUtils.fillRoundedRightSide(context, x + iconWidth + 1,  y, x + getWidth(), y + getHeight(), 0x80000000);
        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);

        RenderUtils.drawTextHUD(context, str, x + iconWidth + 1 + 5, y + 3, durabilityColor, false);
        RenderUtils.drawTextHUD(context, str2, x + iconWidth + 1 + 5 + remainingTextWidth, y + 3 - 1, durabilityColor, false);
    }

    public void renderDurabilityCompact(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        // draw the icon
        RenderUtils.fillRounded(context, x, y, x + iconWidth, y + iconHeight, 0x80000000);
        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);

        int durabilityWidth = 11;
        int firstX = x + ((iconWidth - durabilityWidth) / 2);
        int firstY = y + iconHeight - 1;

        context.fill(firstX, firstY, firstX + durabilityWidth, firstY + 1, 0x80000000);
        context.fill(firstX, firstY, firstX + step, firstY + 1, durabilityColor);
    }
}
