package fin.objhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public abstract class coordinate {
    public static void renderCoordinateHUD(DrawContext context) {

        MinecraftClient client = MinecraftClient.getInstance();

        assert client.player != null;

        Vec3d vec3d = client.player.getPos();
        String coord = new StringBuilder()
                .append((int) vec3d.x).append(' ')
                .append((int) vec3d.y).append(' ')
                .append((int) vec3d.z).toString();

        context.drawText(client.textRenderer, coord, 5, 5, 0xFFFFFFFF, false);
    }
}