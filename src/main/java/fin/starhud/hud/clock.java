package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class clock {

    private static final Settings.ClockSettings.ClockInGameSettings clockInGameSettings = Main.settings.clockSettings.inGameSettings;

    private static final Identifier CLOCK_12_TEXTURE = Identifier.of("starhud", "hud/clock_12.png");
    private static final Identifier CLOCK_24_TEXTURE = Identifier.of("starhud", "hud/clock_24.png");

    private static final int TEXTURE_HEIGHT = 13;

    private static String cachedMinecraftTimeString = "";
    private static int cachedMinecraftMinute = -1;
    private static boolean cachedInGameUse12Hour = clockInGameSettings.use12Hour;

    private static final int TEXTURE_INGAME_12_WIDTH = 65;
    private static final int TEXTURE_INGAME_24_WIDTH = 49;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderInGameTimeHUD(DrawContext context) {
        if ((clockInGameSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (clockInGameSettings.hideOn.chat && Helper.isChatFocused()))
            return;

        ClientWorld world = CLIENT.world;

        long time = world.getTimeOfDay() % 24000;

        boolean use12Hour = clockInGameSettings.use12Hour;

        int minutes = (int) ((time % 1000) * 3 / 50);
        int hours = (int) ((time / 1000) + 6) % 24;
        if (minutes != cachedMinecraftMinute || use12Hour != cachedInGameUse12Hour) {
            cachedMinecraftMinute = minutes;
            cachedInGameUse12Hour = use12Hour;

            cachedMinecraftTimeString = use12Hour ?
                    buildMinecraftCivilianTimeString(hours, minutes) :
                    buildMinecraftMilitaryTimeString(hours, minutes);
        }

        int icon = getWeatherOrTime(world);
        int color = getIconColor(icon) | 0xFF000000;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, clockInGameSettings.scale);

        if (use12Hour) {
            int x = Helper.calculatePositionX(clockInGameSettings.x, clockInGameSettings.originX, TEXTURE_INGAME_12_WIDTH, clockInGameSettings.scale);
            int y = Helper.calculatePositionY(clockInGameSettings.y, clockInGameSettings.originY, TEXTURE_HEIGHT, clockInGameSettings.scale);

            context.drawTexture(RenderPipelines.GUI_TEXTURED, CLOCK_12_TEXTURE, x, y, 0.0F, icon * 13, TEXTURE_INGAME_12_WIDTH, TEXTURE_HEIGHT, TEXTURE_INGAME_12_WIDTH, TEXTURE_HEIGHT * 5, color);
            context.drawText(CLIENT.textRenderer, cachedMinecraftTimeString, x + 19, y + 3, color, false);
        } else {
            int x = Helper.calculatePositionX(clockInGameSettings.x, clockInGameSettings.originX, TEXTURE_INGAME_24_WIDTH, clockInGameSettings.scale);
            int y = Helper.calculatePositionY(clockInGameSettings.y, clockInGameSettings.originY, TEXTURE_HEIGHT, clockInGameSettings.scale);

            context.drawTexture(RenderPipelines.GUI_TEXTURED, CLOCK_24_TEXTURE, x, y, 0.0F, icon * 13, TEXTURE_INGAME_24_WIDTH, TEXTURE_HEIGHT, TEXTURE_INGAME_24_WIDTH, TEXTURE_HEIGHT * 5, color);
            context.drawText(CLIENT.textRenderer, cachedMinecraftTimeString, x + 19, y + 3, color, false);
        }
        context.getMatrices().popMatrix();
    }

    private static int getIconColor(int icon) {
        return switch (icon) {
            case 1 -> clockInGameSettings.color.day;
            case 2 -> clockInGameSettings.color.night;
            case 3 -> clockInGameSettings.color.rain;
            case 4 -> clockInGameSettings.color.thunder;
            default -> 0xFFFFFF;
        };
    }

    private static int getWeatherOrTime(ClientWorld clientWorld) {
        if (clientWorld.isThundering()) return 4;
        else if (clientWorld.isRaining()) return 3;
        else if (clientWorld.isNight()) return 2;
        else return 1;
    }

    private static String buildMinecraftMilitaryTimeString(int hours, int minutes) {
        StringBuilder timeBuilder = new StringBuilder();

        if (hours < 10) timeBuilder.append('0');
        timeBuilder.append(hours).append(':');

        if (minutes < 10) timeBuilder.append('0');
        timeBuilder.append(minutes);

        return timeBuilder.toString();
    }

    private static String buildMinecraftCivilianTimeString(int hours, int minutes) {
        StringBuilder timeBuilder = new StringBuilder();

        String period = hours >= 12 ? " PM" : " AM";

        // 01.00 until 12.59 AM / PM
        hours %= 12;
        if (hours == 0) hours = 12;

        timeBuilder.append(buildMinecraftMilitaryTimeString(hours, minutes)).append(period);

        return timeBuilder.toString();
    }

    private static final Settings.ClockSettings.ClockSystemSettings clockSystemSettings = Main.settings.clockSettings.systemSettings;

    private static final SimpleDateFormat MILITARY_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat CIVILIAN_TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    private static String cachedSystemTimeString = buildSystemMilitaryTimeString(System.currentTimeMillis());
    private static long cachedSystemMinute = -1;
    private static boolean cachedSystemUse12Hour = clockSystemSettings.use12Hour;

    private static final int TEXTURE_SYSTEM_12_WIDTH = 65;
    private static final int TEXTURE_SYSTEM_24_WIDTH = 49;

    public static void renderSystemTimeHUD(DrawContext context) {
        if ((clockSystemSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (clockSystemSettings.hideOn.chat && Helper.isChatFocused()))
            return;

        // update each minute
        long currentTime = System.currentTimeMillis();
        long minute = currentTime / 60000;

        boolean use12Hour = clockSystemSettings.use12Hour;

        // update on either a new minute or user updated the config
        if (minute != cachedSystemMinute || use12Hour != cachedSystemUse12Hour) {
            cachedSystemMinute = minute;
            cachedSystemUse12Hour = use12Hour;

            cachedSystemTimeString = use12Hour ?
                    buildSystemCivilianTimeString(currentTime) :
                    buildSystemMilitaryTimeString(currentTime);
        }

        int color = clockSystemSettings.color | 0xFF000000;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, clockSystemSettings.scale);

        if (use12Hour) {
            int x = Helper.calculatePositionX(clockSystemSettings.x, clockSystemSettings.originX, TEXTURE_SYSTEM_12_WIDTH, clockSystemSettings.scale);
            int y = Helper.calculatePositionY(clockSystemSettings.y, clockSystemSettings.originY, TEXTURE_HEIGHT, clockSystemSettings.scale);

            context.drawTexture(RenderPipelines.GUI_TEXTURED, CLOCK_12_TEXTURE, x, y, 0.0F, 0.0F, TEXTURE_SYSTEM_12_WIDTH, TEXTURE_HEIGHT, TEXTURE_SYSTEM_12_WIDTH, TEXTURE_HEIGHT * 5, color);
            context.drawText(CLIENT.textRenderer, cachedSystemTimeString, x + 19, y + 3, color, false);
        } else {
            int x = Helper.calculatePositionX(clockSystemSettings.x, clockSystemSettings.originX, TEXTURE_SYSTEM_24_WIDTH, clockSystemSettings.scale);
            int y = Helper.calculatePositionY(clockSystemSettings.y, clockSystemSettings.originY, TEXTURE_HEIGHT, clockSystemSettings.scale);

            context.drawTexture(RenderPipelines.GUI_TEXTURED, CLOCK_24_TEXTURE, x, y, 0.0F, 0.0F, TEXTURE_SYSTEM_24_WIDTH, TEXTURE_HEIGHT, TEXTURE_SYSTEM_24_WIDTH, TEXTURE_HEIGHT * 5, color);
            context.drawText(CLIENT.textRenderer, cachedSystemTimeString, x + 19, y + 3, color, false);
        }

        context.getMatrices().popMatrix();
    }

    private static String buildSystemMilitaryTimeString(long time) {
        return MILITARY_TIME_FORMAT.format(new Date(time));
    }

    private static String buildSystemCivilianTimeString(long time) {
        return CIVILIAN_TIME_FORMAT.format(new Date(time));
    }
}