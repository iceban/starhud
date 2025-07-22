package fin.starhud.config;

import fin.starhud.config.hud.*;
import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config.Gui.Background("cloth-config2:transparent")
@Config(name = "starhud")
public class Settings implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public GeneralSettings generalSettings = new GeneralSettings();

    @ConfigEntry.Category("armor")
    @ConfigEntry.Gui.TransitiveObject
    public Armor armorSettings = new Armor();

    public static class Armor {
        @ConfigEntry.Gui.CollapsibleObject
        public ArmorSettings helmet = new ArmorSettings(
                new BaseHUDSettings(true, 5, 0, ScreenAlignmentX.LEFT, ScreenAlignmentY.MIDDLE, GrowthDirectionX.RIGHT, GrowthDirectionY.MIDDLE)
        );

        @ConfigEntry.Gui.CollapsibleObject
        public ArmorSettings chestplate = new ArmorSettings(
                new BaseHUDSettings(true, 5, 14, ScreenAlignmentX.LEFT, ScreenAlignmentY.MIDDLE, GrowthDirectionX.RIGHT, GrowthDirectionY.MIDDLE)
        );

        @ConfigEntry.Gui.CollapsibleObject
        public ArmorSettings leggings = new ArmorSettings(
                new BaseHUDSettings(true, 5, 14 * 2, ScreenAlignmentX.LEFT, ScreenAlignmentY.MIDDLE, GrowthDirectionX.RIGHT, GrowthDirectionY.MIDDLE)
        );

        @ConfigEntry.Gui.CollapsibleObject
        public ArmorSettings boots = new ArmorSettings(
                new BaseHUDSettings(true, 5, 14 * 3, ScreenAlignmentX.LEFT, ScreenAlignmentY.MIDDLE, GrowthDirectionX.RIGHT, GrowthDirectionY.MIDDLE)
        );
    }

    @ConfigEntry.Category("fps")
    @ConfigEntry.Gui.TransitiveObject
    public FPSSettings fpsSettings = new FPSSettings();

    @ConfigEntry.Category("coord")
    @ConfigEntry.Gui.TransitiveObject
    public Coord coordSettings = new Coord();

    public static class Coord {
        @ConfigEntry.Gui.CollapsibleObject
        public CoordSettings X = new CoordSettings(
                new BaseHUDSettings(true, 5, 5, ScreenAlignmentX.LEFT, ScreenAlignmentY.TOP, GrowthDirectionX.RIGHT, GrowthDirectionY.DOWN),
                0xFc7871
        );

        @ConfigEntry.Gui.CollapsibleObject
        public CoordSettings Y = new CoordSettings(
                new BaseHUDSettings(true, 5, 5 + 14, ScreenAlignmentX.LEFT, ScreenAlignmentY.TOP, GrowthDirectionX.RIGHT, GrowthDirectionY.DOWN),
                0xA6F1AF
        );

        @ConfigEntry.Gui.CollapsibleObject
        public CoordSettings Z = new CoordSettings(
                new BaseHUDSettings(true, 5, 5 + 14 * 2, ScreenAlignmentX.LEFT, ScreenAlignmentY.TOP, GrowthDirectionX.RIGHT, GrowthDirectionY.DOWN),
                0x6CE1FC
        );
    }

    @ConfigEntry.Category("direction")
    @ConfigEntry.Gui.TransitiveObject
    public DirectionSettings directionSettings = new DirectionSettings();

    @ConfigEntry.Category("ping")
    @ConfigEntry.Gui.TransitiveObject
    public PingSettings pingSettings = new PingSettings();

    @ConfigEntry.Category("clock")
    @ConfigEntry.Gui.TransitiveObject
    public Clock clockSettings = new Clock();

    public static class Clock {
        @ConfigEntry.Gui.CollapsibleObject
        public ClockSystemSettings systemSetting = new ClockSystemSettings();

        @ConfigEntry.Gui.CollapsibleObject
        public ClockInGameSettings inGameSetting = new ClockInGameSettings();
    }

    @ConfigEntry.Category("day")
    @ConfigEntry.Gui.TransitiveObject
    public DaySettings daySettings = new DaySettings();

    @ConfigEntry.Category("biome")
    @ConfigEntry.Gui.TransitiveObject
    public BiomeSettings biomeSettings = new BiomeSettings();

    @ConfigEntry.Category("inventory")
    @ConfigEntry.Gui.TransitiveObject
    public InventorySettings inventorySettings = new InventorySettings();

    @ConfigEntry.Category("hand")
    @ConfigEntry.Gui.TransitiveObject
    public Hand handSettings = new Hand();

    public static class Hand {
        @ConfigEntry.Gui.CollapsibleObject
        public HandSettings leftHandSettings = new HandSettings(true, -108, -25, ScreenAlignmentX.CENTER, ScreenAlignmentY.BOTTOM, GrowthDirectionX.LEFT, GrowthDirectionY.UP,0xffb3b3);

        @ConfigEntry.Gui.CollapsibleObject
        public HandSettings rightHandSettings = new HandSettings(true, 108, -25, ScreenAlignmentX.CENTER, ScreenAlignmentY.BOTTOM, GrowthDirectionX.RIGHT, GrowthDirectionY.UP, 0x87ceeb);
    }

    @ConfigEntry.Category("effect")
    @ConfigEntry.Gui.TransitiveObject
    public EffectSettings effectSettings = new EffectSettings();

    @ConfigEntry.Category("targeted")
    @ConfigEntry.Gui.TransitiveObject
    public TargetedCrosshairSettings targetedCrosshairSettings = new TargetedCrosshairSettings();
}