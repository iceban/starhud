package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class coordinate {

    private static final Settings.CoordSettings coordSettings = Main.settings.coordSettings;
    private static final Identifier COORD_TEXTURE = Identifier.of("starhud", "hud/coordinate.png");

    private static final int TEXTURE_WIDTH = 65;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderCoordinateHUD(DrawContext context) {
        if ((coordSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (coordSettings.hideOn.chat && Helper.isChatFocused())) return;

        TextRenderer textRenderer = CLIENT.textRenderer;

        Vec3d vec3d = CLIENT.player.getPos();

        String coordX = Integer.toString((int) vec3d.x);
        String coordY = Integer.toString((int) vec3d.y);
        String coordZ = Integer.toString((int) vec3d.z);

        int x = Helper.calculatePositionX(coordSettings.x, coordSettings.originX, TEXTURE_WIDTH, coordSettings.scale);
        int y = Helper.calculatePositionY(coordSettings.y, coordSettings.originY, TEXTURE_HEIGHT, coordSettings.scale);

        int colorX = coordSettings.coordXSettings.color | 0xFF000000;
        int colorY = coordSettings.coordYSettings.color | 0xFF000000;
        int colorZ = coordSettings.coordZSettings.color | 0xFF000000;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, coordSettings.scale);

        if (coordSettings.coordXSettings.shouldRender)
            renderEachCoordinate(context, textRenderer, coordX, x + coordSettings.coordXSettings.xOffset, y + coordSettings.coordXSettings.yOffset, 0.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, colorX);
        if (coordSettings.coordYSettings.shouldRender)
            renderEachCoordinate(context, textRenderer, coordY, x + coordSettings.coordYSettings.xOffset, y + coordSettings.coordYSettings.yOffset, 14.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, colorY);
        if (coordSettings.coordZSettings.shouldRender)
            renderEachCoordinate(context, textRenderer, coordZ, x + coordSettings.coordZSettings.xOffset, y + coordSettings.coordZSettings.yOffset, 28.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, colorZ);

        context.getMatrices().popMatrix();
    }

    public static void renderEachCoordinate(DrawContext context, TextRenderer textRenderer, String str, int x, int y, float v, int width, int height, int color) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, COORD_TEXTURE, x, y, 0.0F, v, width, height, width, 41, color);
        context.drawText(textRenderer, str, x + 19, y + 3, color, false);
    }
}