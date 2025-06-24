package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.CoordSetting;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Coordinate extends AbstractHUD {

    private static final CoordSetting coordSetting = Main.settings.coordSetting;

    private static final Identifier COORD_TEXTURE = Identifier.of("starhud", "hud/coordinate.png");

    private static final int TEXTURE_WIDTH = 65;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Coordinate() {
        super(coordSetting.base);
    }

    @Override
    public void renderHUD(DrawContext context) {
        TextRenderer textRenderer = CLIENT.textRenderer;
        Vec3d vec3d = CLIENT.player.getPos();

        String coordX = Integer.toString((int) vec3d.x);
        String coordY = Integer.toString((int) vec3d.y);
        String coordZ = Integer.toString((int) vec3d.z);

        int colorX = coordSetting.X.color | 0xFF000000;
        int colorY = coordSetting.Y.color | 0xFF000000;
        int colorZ = coordSetting.Z.color | 0xFF000000;

        if (coordSetting.X.shouldRender)
            renderEachCoordinate(context, textRenderer, coordX, x + coordSetting.X.xOffset, y + coordSetting.X.yOffset, 0.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, colorX);
        if (coordSetting.Y.shouldRender)
            renderEachCoordinate(context, textRenderer, coordY, x + coordSetting.Y.xOffset, y + coordSetting.Y.yOffset, 14.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, colorY);
        if (coordSetting.Z.shouldRender)
            renderEachCoordinate(context, textRenderer, coordZ, x + coordSetting.Z.xOffset, y + coordSetting.Z.yOffset, 28.0F, TEXTURE_WIDTH, TEXTURE_HEIGHT, colorZ);

    }

    @Override
    public int getTextureWidth() {
        return TEXTURE_WIDTH;
    }

    @Override
    public int getTextureHeight() {
        return TEXTURE_HEIGHT;
    }

    public static void renderEachCoordinate(DrawContext context, TextRenderer textRenderer, String str, int x, int y, float v, int width, int height, int color) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, COORD_TEXTURE, x, y, 0.0F, v, width, height, width, 41, color);
        context.drawText(textRenderer, str, x + 19, y + 3, color, false);
    }
}
