package fin.objhud;

import net.minecraft.client.gui.DrawContext;

public class Helper {

    public enum ScreenLocationX {
        LEFT,
        MIDDLE,
        RIGHT
    }

    public enum ScreenLocationY {
        UPPER,
        MIDDLE,
        UNDER,
    }

    public static int defaultHUDLocationX(ScreenLocationX locationX, DrawContext context) {
        return switch (locationX) {
            case LEFT -> 0;
            case MIDDLE -> context.getScaledWindowWidth() / 2;
            case RIGHT -> context.getScaledWindowWidth();
        };
    }

    public static int defaultHUDLocationY(ScreenLocationY locationY, DrawContext context) {
        return switch (locationY) {
            case UPPER -> 0;
            case MIDDLE -> context.getScaledWindowHeight() / 2;
            case UNDER -> context.getScaledWindowHeight();
        };
    }

}
