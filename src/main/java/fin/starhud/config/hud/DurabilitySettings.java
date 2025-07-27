package fin.starhud.config.hud;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class DurabilitySettings {

    @Comment("Draw The Durability using Bars instead of numbers.")
    public boolean drawBar = true;

    @Comment("Draw The Icon Using the Item instead of the HUD icon. (Warning: LARGE HUD)")
    public boolean drawItem = false;
}
