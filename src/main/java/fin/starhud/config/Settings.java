package fin.starhud.config;

import fin.starhud.config.hud.*;
import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config.Gui.Background("cloth-config2:transparent")
@Config(name = "starhud")
public class Settings implements ConfigData {

    @ConfigEntry.Category("armor")
    @ConfigEntry.Gui.TransitiveObject
    public ArmorSetting armorSetting = new ArmorSetting();

    @ConfigEntry.Category("fps")
    @ConfigEntry.Gui.TransitiveObject
    public FPSSetting fpsSetting = new FPSSetting();

    @ConfigEntry.Category("coord")
    @ConfigEntry.Gui.TransitiveObject
    public CoordSetting coordSetting = new CoordSetting();

    @ConfigEntry.Category("direction")
    @ConfigEntry.Gui.TransitiveObject
    public DirectionSetting directionSetting = new DirectionSetting();

    @ConfigEntry.Category("ping")
    @ConfigEntry.Gui.TransitiveObject
    public PingSetting pingSetting = new PingSetting();

    @ConfigEntry.Category("clock")
    @ConfigEntry.Gui.TransitiveObject
    public ClockSetting clockSetting = new ClockSetting();

    public static class ClockSetting {
        @ConfigEntry.Gui.CollapsibleObject
        public ClockSystemSetting systemSetting = new ClockSystemSetting();

        @ConfigEntry.Gui.CollapsibleObject
        public ClockInGameSetting inGameSetting = new ClockInGameSetting();
    }

    @ConfigEntry.Category("day")
    @ConfigEntry.Gui.TransitiveObject
    public DaySetting daySetting = new DaySetting();

    @ConfigEntry.Category("biome")
    @ConfigEntry.Gui.TransitiveObject
    public BiomeSetting biomeSetting = new BiomeSetting();

    @ConfigEntry.Category("inventory")
    @ConfigEntry.Gui.TransitiveObject
    public InventorySetting inventorySetting = new InventorySetting();

    @ConfigEntry.Category("hand")
    @ConfigEntry.Gui.TransitiveObject
    public HandSettings handSettings = new HandSettings();

    public static class HandSettings {
        @ConfigEntry.Gui.CollapsibleObject
        public HandSetting leftHandSetting = new HandSetting(true, -108, -25, ScreenAlignmentX.CENTER, ScreenAlignmentY.BOTTOM, GrowthDirectionX.LEFT, 0xffb3b3);

        @ConfigEntry.Gui.CollapsibleObject
        public HandSetting rightHandSetting = new HandSetting(true, 108, -25, ScreenAlignmentX.CENTER, ScreenAlignmentY.BOTTOM, GrowthDirectionX.RIGHT, 0x87ceeb);
    }
}