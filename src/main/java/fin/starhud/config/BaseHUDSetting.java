package fin.starhud.config;

import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.MinecraftClient;

public class BaseHUDSetting implements ConfigData {
    @Comment("Toggle HUD")
    public boolean shouldRender;

    public int x;
    public int y;

    @Comment("HUD default Horizontal location")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ScreenAlignmentX originX;

    @Comment("HUD default Vertical location")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ScreenAlignmentY originY;

    @ConfigEntry.BoundedDiscrete(max = 6)
    @Comment("Set to 0 for default GUI Scale")
    public int scale = 0;

    @ConfigEntry.Gui.CollapsibleObject
    public BaseHUDSetting.Conditional on = new Conditional();

    public static class Conditional {
        @ConfigEntry.Gui.CollapsibleObject
        public ConditionalSetting f3 = new ConditionalSetting(true, 0, 0);

        @ConfigEntry.Gui.CollapsibleObject
        public ConditionalSetting chat = new ConditionalSetting(true, 0, 0);

        @ConfigEntry.Gui.CollapsibleObject
        public ConditionalSetting bossBar = new ConditionalSetting(true, 0, 0);
    }

    public BaseHUDSetting(boolean shouldRender, int x, int y, ScreenAlignmentX originX, ScreenAlignmentY originY) {
        this.shouldRender = shouldRender;
        this.x = x;
        this.y = y;
        this.originX = originX;
        this.originY = originY;
    }

    // adjust position based on alignment
    // if RIGHT, place HUD on the right side of the screen, basically.
    public int getAlignmentXPos(int scaledWidth) {
        return switch (this.originX) {
            case LEFT -> 0;
            case CENTER -> scaledWidth / 2;
            case RIGHT -> scaledWidth;
        };
    }

    // if MIDDLE, place HUD in the middle of the screen
    public int getAlignmentYPos(int scaledHeight) {
        return switch (this.originY) {
            case TOP -> 0;
            case MIDDLE -> scaledHeight / 2;
            case BOTTOM -> scaledHeight;
        };
    }

    // IF RIGHT, shift hud to the left a bit so that no pixel is leaving the screen (supposed you have set x:0, y:0)
    public int getTextureOffsetX(int textureWidth) {
        return switch (this.originX) {
            case LEFT -> 0;
            case CENTER -> textureWidth / 2;
            case RIGHT -> textureWidth;
        };
    }

    // IF BOTTOM, prevent HUD from leaving the screen if you set x:0, y:0
    public int getTextureOffsetY(int textureHeight) {
        return switch (this.originY) {
            case TOP -> 0;
            case MIDDLE -> textureHeight / 2;
            case BOTTOM -> textureHeight;
        };
    }

    // get the scaled factor
    // this can either make your HUD bigger or smaller.
    public float getScaledFactor() {
        return this.scale == 0 ? 1 : (float) MinecraftClient.getInstance().getWindow().getScaleFactor() / this.scale;
    }

    // this shifts your HUD based on your x point, and alignment on X axis, and place them accordingly in your screen.
    public int getCalculatedPosX(int HUDWidth) {
        return this.x + (int) (getAlignmentXPos(MinecraftClient.getInstance().getWindow().getScaledWidth()) * getScaledFactor()) - getTextureOffsetX(HUDWidth);
    }

    // this also shifts your HUD based on your y point, and alignment on Y axis, and place them accordingly in your screen.
    public int getCalculatedPosY(int HUDHeight) {
        return this.y + (int) (getAlignmentYPos(MinecraftClient.getInstance().getWindow().getScaledHeight()) * getScaledFactor()) - getTextureOffsetY(HUDHeight);
    }

    public static class ConditionalSetting {
        public boolean shouldRender;
        public int xOffset;
        public int yOffset;

        public ConditionalSetting(boolean shouldRender, int xOffset, int yOffset) {
            this.shouldRender = shouldRender;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
