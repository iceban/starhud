package fin.starhud.helper;

public enum GrowthDirectionX {
    LEFT,
    CENTER,
    RIGHT;

    public int getGrowthDirection(int growableWidth) {
        return switch (this) {
            case LEFT -> growableWidth;
            case CENTER -> growableWidth / 2;
            case RIGHT -> 0;
        };
    }
}
