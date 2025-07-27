package fin.starhud.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

public class RenderUtils {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

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
