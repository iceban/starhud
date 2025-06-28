package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSettings;
import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class EffectSettings implements ConfigData {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSettings base = new BaseHUDSettings(true, -5, 5, ScreenAlignmentX.RIGHT, ScreenAlignmentY.TOP);

    @Comment("Which way should the HUD goes when a the texture increases?")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GrowthDirectionX growthDirectionX = GrowthDirectionX.LEFT;

    @Comment("Which way should the HUD goes when a the texture increases?")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GrowthDirectionY growthDirectionY = GrowthDirectionY.DOWN;

    public boolean drawVertical = false;

    public int differentTypeGap = 34;
    public int sameTypeGap = 25;

    @ConfigEntry.ColorPicker
    public int ambientColor = 0xd5feef;

    @ConfigEntry.ColorPicker
    public int infiniteColor = 0xB5D0E8;

}
