package fin.starhud.config;

import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import fin.starhud.hud.HUDComponent;
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
    public List<String> hudIds = new ArrayList<>();

    @ConfigEntry.Gui.Excluded
    public String id;

    public GroupedHUDSettings(BaseHUDSettings base) {
        this.base = base;
        this.id = HUDComponent.getInstance().generateNextGroupId();
    }

    public GroupedHUDSettings(BaseHUDSettings base, String id, int gap, boolean alignVertical, int boxColor, List<String> hudIds) {
        this.base = base;
        this.id = id;
        this.gap = gap;
        this.alignVertical = alignVertical;
        this.boxColor = boxColor;
        this.hudIds = hudIds;
    }

    public GroupedHUDSettings(BaseHUDSettings base, int id, int gap, boolean alignVertical, int boxColor, List<String> hudIds) {
        this.base = base;
        this.id = "group_" + id;
        this.gap = gap;
        this.alignVertical = alignVertical;
        this.boxColor = boxColor;
        this.hudIds = hudIds;
    }

    public GroupedHUDSettings() {
        this(new BaseHUDSettings(true, 0, 0, ScreenAlignmentX.LEFT, ScreenAlignmentY.TOP, GrowthDirectionX.RIGHT, GrowthDirectionY.DOWN));
    }

    public boolean isEqual(GroupedHUDSettings other) {
        return this.base.isEqual(other.base)
                && this.gap == other.gap
                && this.alignVertical == other.alignVertical
                && this.hudIds.equals(other.hudIds)
                && this.id.equals(other.id);
    }

    public void copyFrom(GroupedHUDSettings other) {
        this.base.copySettings(other.base);
        this.gap = other.gap;
        this.alignVertical = other.alignVertical;
        this.hudIds = new ArrayList<>(other.hudIds);
        this.id = other.id;
    }

    public GroupedHUDSettings copy() {
        GroupedHUDSettings newSettings = new GroupedHUDSettings();
        newSettings.copyFrom(this);
        return newSettings;
    }

    @Override
    public String toString() {
        return "GroupedHUDSettings{" +
                "id='" + id + '\'' +
                ", hudIds=" + hudIds +
                ", gap=" + gap +
                ", alignVertical=" + alignVertical +
                ", boxColor=0x" + Integer.toHexString(boxColor).toUpperCase() +
                ", base=" + base +
                ", ptr=" + getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this)) +
                '}';
    }

}