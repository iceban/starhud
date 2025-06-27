package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSettings;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class EffectSettings {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSettings base = new BaseHUDSettings(true, -5, 5, ScreenAlignmentX.RIGHT, ScreenAlignmentY.TOP);

    public int beneficialGapX = -25;
    public int beneficialGapY = 0;

    public int harmGapX = -25;
    public int harmGapY = 0;

    public int gapX = 0;
    public int gapY = 32 + 2;


}
