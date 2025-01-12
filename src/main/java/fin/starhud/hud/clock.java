package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class clock {

    private static Settings.ClockSettings.ClockSystemSettings clock_system = Main.settings.clockSettings.systemSettings;
    private static Settings.ClockSettings.ClockInGameSettings clock_ingame = Main.settings.clockSettings.inGameSettings;

    private static final Identifier CLOCK_12 = Identifier.of("starhud", "hud/clock_12.png");
    private static final Identifier CLOCK_24 = Identifier.of("starhud", "hud/clock_24.png");

    private static String minecraftTimeStr = "";
    private static int cachedMinecraftMinute = -1;

    private static boolean LAST_UPDATED_use12Hour_ingame = clock_ingame.use12Hour;

    public static void renderInGameTimeHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        // update each tick
        long time = world.getTimeOfDay() % 24000;

        boolean use12Hour = clock_ingame.use12Hour;

        int minutes = (int) ((time % 1000) * 3 / 50);
        if (minutes != cachedMinecraftMinute || LAST_UPDATED_use12Hour_ingame != use12Hour) {
            cachedMinecraftMinute = minutes;
            LAST_UPDATED_use12Hour_ingame = use12Hour;

            int hours = (int) ((time / 1000) + 6) % 24;
            minecraftTimeStr = use12Hour ?
                    buildMinecraftCivilianTimeString(hours, minutes):
                    buildMinecraftMilitaryTimeString(hours, minutes);
        }

        int width = use12Hour ? 65 : 49;
        int height = 13;

        int x = Helper.defaultHUDAlignmentX(clock_ingame.originX, context.getScaledWindowWidth(), width) + clock_ingame.x;
        int y = Helper.defaultHUDAlignmentY(clock_ingame.originY, context.getScaledWindowHeight(), height) + clock_ingame.y;

        int icon = getWeatherOrTime(world);
        int color = getIconColor(icon) | 0xFF000000;

        context.drawTexture(RenderLayer::getGuiTextured, use12Hour ? CLOCK_12 : CLOCK_24, x, y, 0.0F, icon * 13, width, height, width, height * 5, color);
        context.drawText(client.textRenderer, minecraftTimeStr, x + 19, y + 3, color, false);
    }

    private static int getIconColor(int icon) {
        return switch (icon) {
            case 1 -> clock_ingame.color.day;
            case 2 -> clock_ingame.color.night;
            case 3 -> clock_ingame.color.rain;
            case 4 -> clock_ingame.color.thunder;
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

    private static final SimpleDateFormat militaryTimeFormat = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat civilianTimeFormat = new SimpleDateFormat("hh:mm a");

    private static String systemTimeStr = buildSystemMilitaryTimeString(System.currentTimeMillis());
    private static long cachedSystemMinute = -1;

    private static boolean LAST_UPDATED_use12Hour_system = clock_system.use12Hour;

    public static void renderSystemTimeHUD(DrawContext context) {
        if (!clock_system.shouldRender) return;

        MinecraftClient client = MinecraftClient.getInstance();

        boolean use12Hour = clock_system.use12Hour;

        // update each minute
        long currentTime = System.currentTimeMillis();
        long minute = currentTime / 60000;
        if (minute != cachedSystemMinute || LAST_UPDATED_use12Hour_system != use12Hour) {
            cachedSystemMinute = minute;
            LAST_UPDATED_use12Hour_system = use12Hour;

            systemTimeStr = use12Hour ?
                    buildSystemCivilianTimeString(currentTime):
                    buildSystemMilitaryTimeString(currentTime);
        }

        int width = use12Hour ? 65 : 49;
        int height = 13;

        int x = Helper.defaultHUDAlignmentX(clock_system.originX, context.getScaledWindowWidth(), width) + clock_system.x;
        int y = Helper.defaultHUDAlignmentY(clock_system.originY, context.getScaledWindowHeight(), height) + clock_system.y;
        int color = clock_system.color | 0xFF000000;

        context.drawTexture(RenderLayer::getGuiTextured, clock_system.use12Hour ? CLOCK_12 : CLOCK_24, x, y, 0.0F, 0.0F, width, height, width, height * 5, color);
        context.drawText(client.textRenderer, systemTimeStr, x + 19, y + 3, color, false);
    }

    private static String buildSystemMilitaryTimeString(long time) {
        return militaryTimeFormat.format(new Date(time));
    }
    private static String buildSystemCivilianTimeString(long time) { return civilianTimeFormat.format(new Date(time)); }
}
