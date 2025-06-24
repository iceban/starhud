package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSetting;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class InventorySetting {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSetting base = new BaseHUDSetting(true, -5, 0, ScreenAlignmentX.RIGHT, ScreenAlignmentY.MIDDLE);

    public boolean drawVertical = true;

}
