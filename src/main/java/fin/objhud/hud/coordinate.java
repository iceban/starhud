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

        context.drawTexture(RenderLayer::getGuiTextured, HUD_TEXTURE, x, y, 0.0F, 0.0F, 65, 41, 65, 41);
        context.drawText(textRenderer, coordX, x + 19, y + 3, 0xFFFF7972, shadow);
        context.drawText(textRenderer, coordY, x + 19, y + 17, 0xFFA8F4B1, shadow);
        context.drawText(textRenderer, coordZ, x + 19, y + 31, 0xFF6DE4FF, shadow);
    }
}