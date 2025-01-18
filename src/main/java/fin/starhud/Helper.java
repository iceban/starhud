package fin.starhud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;


public class Helper {

    public enum ScreenAlignmentX {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum ScreenAlignmentY {
        TOP,
        MIDDLE,
        BOTTOM,
    }

    public enum TextGrowthDirection {
        LEFT,
        CENTER,
        RIGHT,
    }

    public static int defaultHUDAlignmentX(ScreenAlignmentX alignmentX, int scaledWidth) {
        return switch (alignmentX) {
            case LEFT -> 0;
            case CENTER -> scaledWidth / 2;
            case RIGHT -> scaledWidth;
        };
    }

    public static int defaultHUDAlignmentY(ScreenAlignmentY alignmentY, int scaledHeight) {
        return switch (alignmentY) {
            case TOP -> 0;
            case MIDDLE -> scaledHeight / 2;
            case BOTTOM -> scaledHeight;
        };
    }

    public static int calculateTextureOffsetX(ScreenAlignmentX alignmentX, int textureWidth) {
        return switch (alignmentX) {
            case LEFT -> 0;
            case CENTER -> textureWidth / 2;
            case RIGHT -> textureWidth;
        };
    }
    public static int calculateTextureOffsetY(ScreenAlignmentY alignmentY, int textureHeight) {
        return switch (alignmentY) {
            case TOP -> 0;
            case MIDDLE -> textureHeight / 2;
            case BOTTOM -> textureHeight;
        };
    }

    public static int getTextGrowthDirection(TextGrowthDirection textGrowthDirection, int textWidth) {
        return switch (textGrowthDirection) {
            case LEFT -> textWidth;
            case CENTER -> textWidth / 2;
            case RIGHT -> 0;
        };
    }

    public static float scaledX(Window window, int scale) {
        return scale == 0 ? 1 : (float) window.getWidth() / (window.getScaledWidth() * scale);
    }

    public static float scaledY(Window window, int scale) {
        return scale == 0 ? 1 : (float) window.getWidth() / (window.getScaledWidth() * scale);
    }

    public static int calculatePositionX(int x, ScreenAlignmentX alignmentX, Window window, int HUDWidth, int HUDScale) {
        return x + (int) (defaultHUDAlignmentX(alignmentX, window.getScaledWidth()) * scaledX(window, HUDScale)) - calculateTextureOffsetX(alignmentX, HUDWidth);
    }

    public static int calculatePositionY(int y, ScreenAlignmentY alignmentY, Window window, int HUDHeight, int HUDScale) {
        return y + (int) (defaultHUDAlignmentY(alignmentY, window.getScaledHeight()) * scaledY(window, HUDScale)) - calculateTextureOffsetY(alignmentY, HUDHeight);
    }

    public static void setHUDScale(DrawContext context, Window window, int scale) {
        if (scale == 0) return;

        float scaleFloat = (float) window.getScaledWidth() * scale / window.getWidth();
        context.getMatrices().scale(scaleFloat, scaleFloat, scaleFloat);
    }

    public static void fillRoundedRightSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2 - 1, y2, color);
        context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

}
