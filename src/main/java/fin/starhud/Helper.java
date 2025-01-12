package fin.starhud;

import net.minecraft.client.gui.DrawContext;

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

    public static int defaultHUDAlignmentX(ScreenAlignmentX alignmentX, int scaledWidth, int HUDWidth) {
        return switch (alignmentX) {
            case LEFT -> 0;
            case CENTER -> (scaledWidth - HUDWidth) / 2;
            case RIGHT -> scaledWidth - HUDWidth;
        };
    }

    public static int defaultHUDAlignmentY(ScreenAlignmentY alignmentY, int scaledHeight, int HUDHeight) {
        return switch (alignmentY) {
            case TOP -> 0;
            case MIDDLE -> (scaledHeight - HUDHeight) / 2;
            case BOTTOM -> scaledHeight - HUDHeight;
        };
    }

    public static void fillRoundedRightSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2 - 1, y2, color);
        context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

}
