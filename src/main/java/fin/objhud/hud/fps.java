package fin.objhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class fps {

    public static void renderFPSHUD(DrawContext context) {

        MinecraftClient client = MinecraftClient.getInstance();

        String fps = new StringBuffer().append(client.getCurrentFps()).toString();
        context.drawText(client.textRenderer, fps, 5, 15, 0xFFFFFFFF, false);
    }
}
