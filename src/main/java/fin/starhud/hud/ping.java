package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import net.minecraft.world.World;

public class ping {

    private static final Settings.PingSettings pingSettings = Main.settings.pingSettings;

    private static final Identifier PING_TEXTURE = Identifier.of("starhud", "hud/ping.png");

    private static long LAST_PING_UPDATE = -1L;
    private static World LAST_WORLD = null;
    private static PingMeasurer cachedPingMeasurer;

    private static final int TEXTURE_WIDTH = 63;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderPingHUD(DrawContext context) {
        if ((pingSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (pingSettings.hideOn.chat && Helper.isChatFocused())) return;
        if (CLIENT.isInSingleplayer()) return;

        MultiValueDebugSampleLogImpl pingLog = CLIENT.getDebugHud().getPingLog();

        // different world and server checking for PingMeasurer renewal.
        World currentWorld = CLIENT.world;
        if (currentWorld != LAST_WORLD) {
            cachedPingMeasurer = new PingMeasurer(CLIENT.getNetworkHandler(), pingLog);
            LAST_WORLD = currentWorld;
        }

        updatePingLog();

        int pingLogLen = pingLog.getLength();
        if (pingLogLen <= 0) return;

        // get the latest updated ping through the last element.
        long currentPing = pingLog.get(pingLogLen - 1);
        String pingStr = currentPing + " ms";

        int x = Helper.calculatePositionX(pingSettings.x, pingSettings.originX, TEXTURE_WIDTH, pingSettings.scale);
        int y = Helper.calculatePositionY(pingSettings.y, pingSettings.originY, TEXTURE_HEIGHT, pingSettings.scale);

        // 0, 150, 300, 450
        int step = Math.min((int) currentPing / 150, 3);
        int color = getPingColor(step) | 0xFF000000;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, pingSettings.scale);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, PING_TEXTURE, x, y, 0.0F, step * 13, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT * 4, color);
        context.drawText(CLIENT.textRenderer, pingStr, x + 19, y + 3, color, false);

        context.getMatrices().popMatrix();
    }

    public static int getPingColor(int step) {
        return switch (step) {
            case 0 -> pingSettings.pingColor.first;
            case 1 -> pingSettings.pingColor.second;
            case 2 -> pingSettings.pingColor.third;
            case 3 -> pingSettings.pingColor.fourth;
            default -> 0xFFFFFFFF;
        };
    }

    // update pingLog every n seconds. Because this is quite expensive.
    private static void updatePingLog() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - LAST_PING_UPDATE >= 1000 * pingSettings.updateInterval) {
            LAST_PING_UPDATE = currentTimeMillis;
            cachedPingMeasurer.ping();
        }
    }
}
