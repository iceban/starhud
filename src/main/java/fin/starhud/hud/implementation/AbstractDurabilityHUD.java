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

    private boolean drawBar;
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

        drawBar = durabilitySettings.drawBar;
        drawItem = durabilitySettings.drawItem;
        iconColor = getIconColor();

        width = processWidth();
        height = drawItem ? BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT : DURABILITY_BACKGROUND_TEXTURE_HEIGHT;

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
        if (drawBar) {
            return processWidthItemBar();
        } else {
            return processWidthItemNumber();
        }
    }

    public int processWidthIcon() {
        if (drawBar) {
            return processWidthIconBar();
        } else {
            return processWidthIconNumber();
        }
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
        durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        int strWidth = CLIENT.textRenderer.getWidth(str);
        return ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
    }

    public int processWidthIconBar() {
        step = getItemBarStep(stackDamage, stackMaxDamage, 10);
        durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        return 13 + 1 + DURABILITY_BACKGROUND_TEXTURE_WIDTH;
    }

    public int processWidthIconNumber() {
        int damage = stackDamage;
        int maxDamage = stackMaxDamage;
        int remaining = maxDamage - damage;

        str = Helper.toSuperscript(Integer.toString(remaining)) + '/';
        str2 = Helper.toSubscript(Integer.toString(maxDamage));
        step = getItemBarStep(stackDamage, stackMaxDamage, 10);
        durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        remainingTextWidth = CLIENT.textRenderer.getWidth(str);

        int maxDamageTextWidth = CLIENT.textRenderer.getWidth(str2);

        return 13 + 1 + 5 + remainingTextWidth + maxDamageTextWidth + 5;
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
        if (drawBar) {
            renderItemDurabilityBar(context, x, y);
        } else {
            renderItemDurabilityNumber(context, x, y);
        }
    }

    public void renderDurability(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        if (drawBar) {
            renderDurabilityBar(context, ICON, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight);
        } else {
            renderDurabilityNumber(context, ICON, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight);
        }
    }

    public void renderItemDurabilityBar(DrawContext context, int x, int y) {
        // draw background for the item texture
        RenderUtils.drawTextureHUD(context, ITEM_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT);
        // draw background for the big durability bar
        RenderUtils.drawTextureHUD(context, BIG_DURABILITY_BACKGROUND_TEXTURE, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1, y, 0.0F, 0.0F, BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH, BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT, BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH, BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT);

        // draw item
        context.drawItem(stack, x + 3, y + 3);

        // draw the durability bar
        if (step != 0)
            RenderUtils.drawTextureHUD(context, BIG_DURABILITY_TEXTURE, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5, y + 4, 0.0F, 0.0F, step * 7, BIG_DURABILITY_TEXTURE_HEIGHT, BIG_DURABILITY_TEXTURE_WIDTH, BIG_DURABILITY_TEXTURE_HEIGHT, durabilityColor);
    }

    public void renderItemDurabilityNumber(DrawContext context, int x, int y) {
        RenderUtils.drawTextureHUD(context, ITEM_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT);
        RenderUtils.fillRoundedRightSide(context, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1, y, x + getWidth(), y + getHeight(), 0x80000000);

        context.drawItem(stack, x + 3, y + 3);
        RenderUtils.drawTextHUD(context, str, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5, y + 7, durabilityColor, false);
    }

    public void renderDurabilityBar(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        // draw the icon
        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);

        // draw the durability background and steps
        RenderUtils.drawTextureHUD(context, DURABILITY_BACKGROUND_TEXTURE, x + iconWidth + 1, y, 0.0F, 0.0F, DURABILITY_BACKGROUND_TEXTURE_WIDTH, DURABILITY_BACKGROUND_TEXTURE_HEIGHT, DURABILITY_BACKGROUND_TEXTURE_WIDTH, DURABILITY_BACKGROUND_TEXTURE_HEIGHT);
        if (step != 0) RenderUtils.drawTextureHUD(context, DURABILITY_TEXTURE, x + iconWidth + 1 + 5, y + 3, 0, 0, 4 * step, DURABILITY_TEXTURE_HEIGHT, DURABILITY_TEXTURE_WIDTH, DURABILITY_TEXTURE_HEIGHT, this.durabilityColor);
    }

    // example render: ¹²³⁴/₅₆₇₈
    public void renderDurabilityNumber(DrawContext context, Identifier ICON, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight) {
        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);
        RenderUtils.fillRoundedRightSide(context, x + 14,  y, x + getWidth(), y + getHeight(), 0x80000000);

        // this is gore of my comfort character, call drawText twice except the second one has a -1 pixel offset.
        // Minecraft default subscript's font is 1 pixel to deep for my liking, so I have to shift them up.
        RenderUtils.drawTextHUD(context, str, x + iconWidth + 1 + 5, y + 3, durabilityColor, false);
        RenderUtils.drawTextHUD(context, str2, x + iconWidth + 1 + 5 + remainingTextWidth, y + 3 - 1, durabilityColor, false);
    }
}
