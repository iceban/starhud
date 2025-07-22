package fin.starhud.helper;

import fin.starhud.Helper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RenderUtils {

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

    private static final Box tempBox = new Box(0,0);

    public static void drawSmallHUD(DrawContext context, String infoStr, int x, int y, int width, int height, Identifier iconTexture, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color) {
        RenderUtils.drawTextureHUD(context, iconTexture, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, color);
        RenderUtils.fillRoundedRightSide(context, x + iconWidth + 1, y, x + width, y + height, 0x80000000);
        RenderUtils.drawTextHUD(context, infoStr, x + iconWidth + 1 + 5, y + 3, color, false);
    }

    public static void drawSmallHUD(DrawContext context, OrderedText infoText, int x, int y, int width, int height, Identifier iconTexture, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color) {
        RenderUtils.drawTextureHUD(context, iconTexture, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, color);
        RenderUtils.fillRoundedRightSide(context, x + iconWidth + 1, y, x + width, y + height, 0x80000000);
        RenderUtils.drawTextHUD(context, infoText, x + iconWidth + 1 + 5, y + 3, color, false);
    }

    public static void fillRoundedRightSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2 - 1, y2, color);
        context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    // get the durability "steps" or progress.
    public static int getItemBarStep(ItemStack stack, int maxStep) {
        return MathHelper.clamp(Math.round(maxStep - (float) stack.getDamage() * maxStep / (float) stack.getMaxDamage()), 0, maxStep);
    }

    // color transition from pastel (red to green).
    public static int getItemBarColor(int stackStep, int maxStep) {
        return MathHelper.hsvToRgb(0.35F * stackStep / (float) maxStep, 0.45F, 0.95F);
    }

    public static Box renderDurabilityHUD(DrawContext context, ItemStack stack, Identifier iconTexture, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, boolean drawBar, boolean drawItem, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        if (drawItem) {
            return renderItemDurability(context, stack, x , y, drawBar, growthDirectionX, growthDirectionY);
        } else {
            return renderDurability(context, iconTexture, stack, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight, color, drawBar, growthDirectionX, growthDirectionY);
        }
    }

    public static Box renderItemDurability(DrawContext context, ItemStack stack, int x, int y, boolean drawBar, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        if (drawBar) {
            return renderItemDurabilityBar(context, stack, x, y, growthDirectionX, growthDirectionY);
        } else {
            return renderItemDurabilityNumber(context, stack, x, y, growthDirectionX, growthDirectionY);
        }
    }

    public static Box renderDurability(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, boolean drawBar, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        if (drawBar) {
            return renderDurabilityBar(context, ICON, stack, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight, color, growthDirectionX, growthDirectionY);
        } else {
            return renderDurabilityNumber(context, ICON, stack, x, y, u, v, textureWidth, textureHeight, iconWidth, iconHeight, color, growthDirectionX, growthDirectionY);
        }
    }

    public static Box renderItemDurabilityBar(DrawContext context, ItemStack stack, int x, int y, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        int step = getItemBarStep(stack, 10);
        int durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        int width = ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5 + BIG_DURABILITY_TEXTURE_WIDTH + 5;
        int height = ITEM_BACKGROUND_TEXTURE_WIDTH;

        x -= growthDirectionX.getGrowthDirection(width);
        y -= growthDirectionY.getGrowthDirection(height);

        tempBox.setBoundingBox(x, y, width, height, durabilityColor);

        // draw background for the item texture
        RenderUtils.drawTextureHUD(context, ITEM_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT);
        // draw background for the big durability bar
        RenderUtils.drawTextureHUD(context, BIG_DURABILITY_BACKGROUND_TEXTURE, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1, y, 0.0F, 0.0F, BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH, BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT, BIG_DURABILITY_BACKGROUND_TEXTURE_WIDTH, BIG_DURABILITY_BACKGROUND_TEXTURE_HEIGHT);

        // draw item
        context.drawItem(stack, x + 3, y + 3);

        // draw the durability bar
        if (step != 0)
            RenderUtils.drawTextureHUD(context, BIG_DURABILITY_TEXTURE, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5, y + 4, 0.0F, 0.0F, step * 7, BIG_DURABILITY_TEXTURE_HEIGHT, BIG_DURABILITY_TEXTURE_WIDTH, BIG_DURABILITY_TEXTURE_HEIGHT, durabilityColor);

        return tempBox;
    }

    public static Box renderItemDurabilityNumber(DrawContext context, ItemStack stack, int x, int y, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - damage;

        String durability = remaining + "/" + maxDamage;

        int textColor = getItemBarColor(remaining, maxDamage) | 0xFF000000;

        int strWidth = CLIENT.textRenderer.getWidth(durability);

        int width = ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
        int height = ITEM_BACKGROUND_TEXTURE_HEIGHT;

        x -= growthDirectionX.getGrowthDirection(width);
        y -= growthDirectionY.getGrowthDirection(height);

        tempBox.setBoundingBox(x, y, width, height, textColor);

        RenderUtils.drawTextureHUD(context, ITEM_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT, ITEM_BACKGROUND_TEXTURE_WIDTH, ITEM_BACKGROUND_TEXTURE_HEIGHT);
        RenderUtils.fillRoundedRightSide(context, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1, y, x + width, y + height, 0x80000000);

        context.drawItem(stack, x + 3, y + 3);
        RenderUtils.drawTextHUD(context, durability, x + ITEM_BACKGROUND_TEXTURE_WIDTH + 1 + 5, y + 7, textColor, false);

        return tempBox;
    }

    public static Box renderDurabilityBar(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        int step = getItemBarStep(stack, 10);
        int durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        int width = iconWidth + 1 + 5 + (DURABILITY_TEXTURE_WIDTH - 1) + 5;
        int height = iconHeight;

        x -= growthDirectionX.getGrowthDirection(width);
        y -= growthDirectionY.getGrowthDirection(height);

        tempBox.setBoundingBox(x, y, width, height, color);

        // draw the icon
        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, color);

        // draw the durability background and steps
        RenderUtils.drawTextureHUD(context, DURABILITY_BACKGROUND_TEXTURE, x + iconWidth + 1, y, 0.0F, 0.0F, DURABILITY_BACKGROUND_TEXTURE_WIDTH, DURABILITY_BACKGROUND_TEXTURE_HEIGHT, DURABILITY_BACKGROUND_TEXTURE_WIDTH, DURABILITY_BACKGROUND_TEXTURE_HEIGHT);
        if (step != 0) RenderUtils.drawTextureHUD(context, DURABILITY_TEXTURE, x + iconWidth + 1 + 5, y + 3, 0, 0, 4 * step, DURABILITY_TEXTURE_HEIGHT, DURABILITY_TEXTURE_WIDTH, DURABILITY_TEXTURE_HEIGHT, durabilityColor);

        return tempBox;
    }

    // example render: ¹²³⁴/₅₆₇₈
    public static Box renderDurabilityNumber(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - damage;

        String remainingStr = Helper.toSuperscript(Integer.toString(remaining)) + '/';
        String maxDamageStr = Helper.toSubscript(Integer.toString(maxDamage));

        int remainingTextWidth = CLIENT.textRenderer.getWidth(remainingStr);
        int maxDamageTextWidth = CLIENT.textRenderer.getWidth(maxDamageStr);

        int textColor = getItemBarColor(remaining, maxDamage) | 0xFF000000;

        int width = iconWidth + 1 + 5 + remainingTextWidth + maxDamageTextWidth + 5;
        int height = iconHeight;

        x -= growthDirectionX.getGrowthDirection(width);
        y -= growthDirectionY.getGrowthDirection(height);

        tempBox.setBoundingBox(x, y, width, height, color);

        RenderUtils.drawTextureHUD(context, ICON, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, color);
        fillRoundedRightSide(context, x + 14,  y, x + width, y + height, 0x80000000);

        // this is gore of my comfort character, call drawText twice except the second one has a -1 pixel offset.
        // Minecraft default subscript's font is 1 pixel to deep for my liking, so I have to shift them up.
        RenderUtils.drawTextHUD(context, remainingStr, x + iconWidth + 1 + 5, y + 3, textColor, false);
        RenderUtils.drawTextHUD(context, maxDamageStr, x + iconWidth + 1 + 5 + remainingTextWidth, y + 3 - 1, textColor, false);
        return tempBox;
    }

    // for easier version porting.

    public static void drawTextureHUD(DrawContext context, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, u, v, width, height, textureWidth, textureHeight, color);
    }

    public static void drawTextureHUD(DrawContext context, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTextHUD(DrawContext context, String str, int x, int y, int color, boolean shadow) {
        OrderedText orderedText = OrderedText.styledForwardsVisitedString(str, Style.EMPTY);
        context.drawText(CLIENT.textRenderer, orderedText, x , y, color, shadow);
    }

    public static void drawTextHUD(DrawContext context, OrderedText text, int x, int y, int color, boolean shadow) {
        context.drawText(CLIENT.textRenderer, text, x, y, color, shadow);
    }
}
