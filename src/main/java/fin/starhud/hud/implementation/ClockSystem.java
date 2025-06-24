package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.ClockSystemSetting;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockSystem extends AbstractHUD {

    private static final ClockSystemSetting clockSystemSetting = Main.settings.clockSetting.systemSetting;

    private static final Identifier CLOCK_12_TEXTURE = Identifier.of("starhud", "hud/clock_12.png");
    private static final Identifier CLOCK_24_TEXTURE = Identifier.of("starhud", "hud/clock_24.png");

    private static final int TEXTURE_HEIGHT = 13;

    private static final SimpleDateFormat MILITARY_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat CIVILIAN_TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    private static String cachedSystemTimeString = buildSystemMilitaryTimeString(System.currentTimeMillis());
    private static long cachedSystemMinute = -1;
    private static boolean cachedSystemUse12Hour = clockSystemSetting.use12Hour;

    private static final int TEXTURE_SYSTEM_12_WIDTH = 65;
    private static final int TEXTURE_SYSTEM_24_WIDTH = 49;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public ClockSystem() {
        super(clockSystemSetting.base);
    }

    @Override
    public void renderHUD(DrawContext context) {
        // update each minute
        long currentTime = System.currentTimeMillis();
        long minute = currentTime / 60000;

        boolean use12Hour = clockSystemSetting.use12Hour;

        // update on either a new minute or user updated the config
        if (minute != cachedSystemMinute || use12Hour != cachedSystemUse12Hour) {
            cachedSystemMinute = minute;
            cachedSystemUse12Hour = use12Hour;

            cachedSystemTimeString = use12Hour ?
                    buildSystemCivilianTimeString(currentTime) :
                    buildSystemMilitaryTimeString(currentTime);
        }

        int color = clockSystemSetting.color | 0xFF000000;

        if (use12Hour) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, CLOCK_12_TEXTURE, x, y, 0.0F, 0.0F, TEXTURE_SYSTEM_12_WIDTH, TEXTURE_HEIGHT, TEXTURE_SYSTEM_12_WIDTH, TEXTURE_HEIGHT * 5, color);
            context.drawText(CLIENT.textRenderer, cachedSystemTimeString, x + 19, y + 3, color, false);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, CLOCK_24_TEXTURE, x, y, 0.0F, 0.0F, TEXTURE_SYSTEM_24_WIDTH, TEXTURE_HEIGHT, TEXTURE_SYSTEM_24_WIDTH, TEXTURE_HEIGHT * 5, color);
            context.drawText(CLIENT.textRenderer, cachedSystemTimeString, x + 19, y + 3, color, false);
        }
    }

    private static String buildSystemMilitaryTimeString(long time) {
        return MILITARY_TIME_FORMAT.format(new Date(time));
    }

    private static String buildSystemCivilianTimeString(long time) {
        return CIVILIAN_TIME_FORMAT.format(new Date(time));
    }

    @Override
    public int getTextureWidth() {
        return clockSystemSetting.use12Hour ? TEXTURE_SYSTEM_12_WIDTH : TEXTURE_SYSTEM_24_WIDTH;
    }

    @Override
    public int getTextureHeight() {
        return TEXTURE_HEIGHT;
    }
}
