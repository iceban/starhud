package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSetting;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class FPSSetting {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSetting base = new BaseHUDSetting(true, 5, -5, ScreenAlignmentX.LEFT, ScreenAlignmentY.BOTTOM);

    @ConfigEntry.ColorPicker
    public int color = 0xE5ECf8;
}
