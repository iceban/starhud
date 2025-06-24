package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSetting;
import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class HandSetting {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSetting base;

    @Comment("Which way should the HUD goes when a the texture increases?")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GrowthDirectionX textureGrowth;

    public boolean showCount = true;
    public boolean showDurability = true;

    @ConfigEntry.ColorPicker
    public int color;

    public HandSetting(boolean shouldRender, int x, int y, ScreenAlignmentX originX, ScreenAlignmentY originY, GrowthDirectionX textureGrowth, int color) {
        base = new BaseHUDSetting(shouldRender, x, y, originX, originY);
        this.textureGrowth = textureGrowth;
        this.color = color;
    }
}
