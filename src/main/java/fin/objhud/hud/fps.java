package fin.objhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class fps {

    private static final Identifier FPS_TEXTURE = Identifier.of("objhud", "hud/fps.png");

    public static void renderFPSHUD(DrawContext context) {

        MinecraftClient client = MinecraftClient.getInstance();
        String fps = Integer.toString(client.getCurrentFps());

        int x = 100;
        int y = 5;

        context.drawTexture(RenderLayer::getGuiTextured, FPS_TEXTURE, x, y, 0.0F, 0.0F, 56, 13, 56, 13);
        context.drawText(client.textRenderer, fps, x + 28, y + 3, 0xFFFDD835, false);
    }
}