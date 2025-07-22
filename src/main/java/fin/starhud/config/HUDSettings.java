package fin.starhud.config;

import fin.starhud.hud.HUDId;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

public class HUDSettings {

    @ConfigEntry.Gui.Excluded
    public List<HUDId> individualHudIds = new ArrayList<>();

    public List<GroupedHUDSettings> groupedHuds = new ArrayList<>();

    public HUDSettings(List<HUDId> individualHudIds, List<GroupedHUDSettings> groupedHuds) {
        this.individualHudIds = individualHudIds;
        this.groupedHuds = groupedHuds;
    }

    public HUDSettings() {
        individualHudIds.addAll(List.of(HUDId.values()));
    }
}
