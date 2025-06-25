package fin.starhud.helper;

public enum ScreenAlignmentY {
    TOP,
    MIDDLE,
    BOTTOM;

    // if MIDDLE, place HUD in the middle of the screen
    public int getAlignmentYPos(int scaledHeight) {
        return switch (this) {
            case TOP -> 0;
            case MIDDLE -> scaledHeight / 2;
            case BOTTOM -> scaledHeight;
        };
    }

    // IF BOTTOM, prevent HUD from leaving the screen if you set x:0, y:0
    public int getTextureOffsetY(int textureHeight) {
        return switch (this) {
            case TOP -> 0;
            case MIDDLE -> textureHeight / 2;
            case BOTTOM -> textureHeight;
        };
    }
}
