package fin.starhud.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class GeneralSettings {

    @ConfigEntry.Gui.CollapsibleObject
    public EditHUDScreenSettings screenSettings = new EditHUDScreenSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public InGameHUDSettings inGameSettings = new InGameHUDSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public HUDSettings hudSettings = new HUDSettings();

    public static class EditHUDScreenSettings {

        @ConfigEntry.ColorPicker
        public int selectedBoxColor = 0x87ceeb;

        @ConfigEntry.ColorPicker
        public int selectedGroupBoxColor = 0xFc7871;

        @ConfigEntry.ColorPicker
        public int dragBoxColor = 0xa8d8ea;
    }

    public static class InGameHUDSettings {

        @Comment("Completely disable HUD Rendering.")
        public boolean disableHUDRendering = false;
    }

    public static class HUDSettings {

        @Comment("Since some fonts may have a different font heights, try adjusting this to your liking. Default is 0.")
        public int textYOffset = 0;
    }
}