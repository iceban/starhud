package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSetting;
import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class DaySetting {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSetting base = new BaseHUDSetting(true, 0, 0, ScreenAlignmentX.CENTER, ScreenAlignmentY.TOP);

    @Comment("Which way should the HUD goes when a the texture increases?")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GrowthDirectionX textGrowth = GrowthDirectionX.RIGHT;

    @ConfigEntry.ColorPicker
    public int color = 0xFFFFFF;
}
