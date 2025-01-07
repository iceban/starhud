package fin.objhud.hud;

import fin.objhud.Helper;
import fin.objhud.Main;
import fin.objhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class coordinate {

    public static Settings.CoordSettings coord = Main.settings.coordSettings;
    private static final Identifier HUD_TEXTURE = Identifier.of("objhud", "hud/coordinate.png");

    public static void renderCoordinateHUD(DrawContext context) {
        if (!coord.renderCoordinateHUD) return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        Vec3d vec3d = client.player.getPos();

        String coordX = Integer.toString((int) vec3d.x);
        String coordY = Integer.toString((int) vec3d.y);
        String coordZ = Integer.toString((int) vec3d.z);

        int x = Helper.defaultHUDLocationX(coord.defX, context) + coord.x;
        int y = Helper.defaultHUDLocationY(coord.defY, context) + coord.y;

        boolean shadow = false;

        int colorX = coord.color.X | 0xFF000000;
        int colorY = coord.color.Y | 0xFF000000;
        int colorZ = coord.color.Z | 0xFF000000;

        context.drawTexture(RenderLayer::getGuiTextured, HUD_TEXTURE, x, y, 0.0F, 0.0F, 65, 14, 65, 41, colorX);
        context.drawTexture(RenderLayer::getGuiTextured, HUD_TEXTURE, x, y + 14, 0.0F, 14.0F, 65, 14, 65, 41, colorY);
        context.drawTexture(RenderLayer::getGuiTextured, HUD_TEXTURE, x, y + 28, 0.0F, 28.0F, 65, 14, 65, 41, colorZ);
        context.drawText(textRenderer, coordX, x + 19, y + 3, colorX, shadow);
        context.drawText(textRenderer, coordY, x + 19, y + 17, colorY, shadow);
        context.drawText(textRenderer, coordZ, x + 19, y + 31, colorZ, shadow);
    }
}