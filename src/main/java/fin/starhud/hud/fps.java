package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class fps {

    public static Settings.FPSSettings fps = Main.settings.fpsSettings;

    private static final Identifier FPS_TEXTURE = Identifier.of("starhud", "hud/fps.png");

    private static final int width = 69;
    private static final int height = 13;

    public static void renderFPSHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        String fpsStr = client.getCurrentFps() + " FPS";

        int x = Helper.calculatePositionX(fps.x, fps.originX, client.getWindow(), width, fps.scale);
        int y = Helper.calculatePositionY(fps.y, fps.originY, client.getWindow(), height, fps.scale);

        int color = fps.color | 0xFF000000;

        context.getMatrices().push();
        Helper.setHUDScale(context, client.getWindow(), fps.scale);
        context.drawTexture(RenderLayer::getGuiTextured, FPS_TEXTURE, x, y, 0.0F, 0.0F, width, height, width, height, color);
        context.drawText(client.textRenderer, fpsStr, x + 19, y + 3, color, false);
        context.getMatrices().pop();
    }
}