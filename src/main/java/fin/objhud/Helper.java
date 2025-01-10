package fin.objhud;

import net.minecraft.client.gui.DrawContext;

public class Helper {

    public enum ScreenLocationX {
        LEFT,
        MIDDLE,
        RIGHT
    }

    public enum ScreenLocationY {
        TOP,
        MIDDLE,
        BOTTOM,
    }

    public static int defaultHUDLocationX(ScreenLocationX locationX, DrawContext context, int width) {
        return switch (locationX) {
            case LEFT -> 0;
            case MIDDLE -> (context.getScaledWindowWidth() - width) / 2;
            case RIGHT -> context.getScaledWindowWidth() - width;
        };
    }

    public static int defaultHUDLocationY(ScreenLocationY locationY, DrawContext context, int height) {
        return switch (locationY) {
            case TOP -> 0;
            case MIDDLE -> (context.getScaledWindowHeight() - height) / 2;
            case BOTTOM -> context.getScaledWindowHeight() - height;
        };
    }

}
