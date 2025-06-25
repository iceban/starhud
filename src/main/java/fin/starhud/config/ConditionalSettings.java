package fin.starhud.config;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ConditionalSettings {

    @Comment("Enable this HUD To be Rendered")
    public boolean shouldRender;

    @Comment("Shifts this HUD in the X Axis")
    public int xOffset;

    @Comment("Shifts this HUD in the Y Axis")
    public int yOffset;

    public ConditionalSettings(boolean shouldRender, int xOffset, int yOffset) {
        this.shouldRender = shouldRender;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
}