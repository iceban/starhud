package fin.objhud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import fin.objhud.Helper;

@Config.Gui.Background("cloth-config2:transparent")
@Config(name = "Objective-HUD")
public class Settings implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public ArmorSettings armorSettings = new ArmorSettings();
    public static class ArmorSettings {
        @Comment("Toggle Armor HUD")
        public boolean renderArmorHUD = true;

        @Comment("Armor HUD default Horizontal location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationX defX = Helper.ScreenLocationX.RIGHT;

        @Comment("Armor HUD default Vertical location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationY defY = Helper.ScreenLocationY.UNDER;

        public int x = -73;
        public int y = -65;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public CoordSettings coordSettings = new CoordSettings();
    public static class CoordSettings {
        @Comment("Toggle Coordinate HUD")
        public boolean renderCoordinateHUD = true;

        @Comment("Coordinate HUD default Horizontal location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationX defX = Helper.ScreenLocationX.LEFT;

        @Comment("Coordinate HUD default Vertical location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationY defY = Helper.ScreenLocationY.UPPER;

        public int x = 10;
        public int y = 10;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public FPSSettings fpsSettings = new FPSSettings();
    public static class FPSSettings {
        @Comment("Toggle FPS HUD")
        public boolean renderFPSHUD = true;

        @Comment("FPS HUD default Horizontal location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationX defX = Helper.ScreenLocationX.LEFT;

        @Comment("FPS HUD default Vertical location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationY defY = Helper.ScreenLocationY.UPPER;

        public int x = 80;
        public int y = 17;
    }

    @ConfigEntry.Gui.CollapsibleObject
    public PingSettings pingSettings = new PingSettings();
    public static class PingSettings {
        @Comment("Toggle Ping HUD")
        public boolean renderPingHUD = true;

        @Comment("Ping HUD default Horizontal location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationX defX = Helper.ScreenLocationX.LEFT;

        @Comment("Ping HUD default Vertical location")
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public Helper.ScreenLocationY defY = Helper.ScreenLocationY.UPPER;

        public int x = 80;
        public int y = 31;
    }

}


