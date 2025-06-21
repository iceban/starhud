package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class fps {

    public static final Settings.FPSSettings fpsSettings = Main.settings.fpsSettings;

    private static final Identifier FPS_TEXTURE = Identifier.of("starhud", "hud/fps.png");

    private static final int TEXTURE_WIDTH = 69;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderFPSHUD(DrawContext context) {
        if ((fpsSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (fpsSettings.hideOn.chat && Helper.isChatFocused())) return;

        String fpsStr = CLIENT.getCurrentFps() + " FPS";

        int x = Helper.calculatePositionX(fpsSettings.x, fpsSettings.originX, TEXTURE_WIDTH, fpsSettings.scale);
        int y = Helper.calculatePositionY(fpsSettings.y, fpsSettings.originY, TEXTURE_HEIGHT, fpsSettings.scale);

        int color = fpsSettings.color | 0xFF000000;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, fpsSettings.scale);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, FPS_TEXTURE, x, y, 0.0F, 0.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT, color);
        context.drawText(CLIENT.textRenderer, fpsStr, x + 19, y + 3, color, false);

        context.getMatrices().popMatrix();
    }
}