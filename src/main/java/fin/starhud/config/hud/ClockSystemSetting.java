package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSetting;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ClockSystemSetting {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSetting base = new BaseHUDSetting(true, -5, -5, ScreenAlignmentX.RIGHT, ScreenAlignmentY.BOTTOM);

    public boolean use12Hour = false;

    @ConfigEntry.ColorPicker
    public int color = 0xFFFFFF;
}
