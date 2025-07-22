package fin.starhud.config;

import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import fin.starhud.hud.HUDId;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

public class GroupedHUDSettings {
    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSettings base;

    public int gap = 0;

    public boolean alignVertical = false;

    @ConfigEntry.ColorPicker
    public int boxColor = 0xFFFFFF;

    @ConfigEntry.Gui.Excluded
    public List<HUDId> ids = new ArrayList<>();

    public GroupedHUDSettings(BaseHUDSettings base) {
        this.base = base;
    }

    public GroupedHUDSettings() {
        this(new BaseHUDSettings(true, 0, 0, ScreenAlignmentX.LEFT, ScreenAlignmentY.TOP, GrowthDirectionX.RIGHT, GrowthDirectionY.DOWN));
    }
}