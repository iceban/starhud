package fin.starhud.hud;

public enum HUDId {
    HELMET("helmet"),
    CHESTPLATE("chestplate"),
    LEGGINGS("leggings"),
    BOOTS("boots"),

    X_COORDINATE("x_coordinate"),
    Y_COORDINATE("y_coordinate"),
    Z_COORDINATE("z_coordinate"),

    LEFT_HAND("left_hand"),
    RIGHT_HAND("right_hand"),

    CLOCK_INGAME("clock_ingame"),
    CLOCK_SYSTEM("clock_system"),

    BIOME("biome"),
    DAY("day"),
    DIRECTION("direction"),
    FPS("fps"),
    PING("ping"),
    INVENTORY("inventory"),
    TARGETED_CROSSHAIR("targeted_crosshair"),
    EFFECT("effect");

    private final String str;
    HUDId(String str) {
        this.str = str;
    }

    public String getString() {
        return str;
    }
}
