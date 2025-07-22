package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.FPSSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class FPSHUD extends AbstractHUD {

    private static final FPSSettings FPS_SETTINGS = Main.settings.fpsSettings;

    private static final Identifier FPS_TEXTURE = Identifier.of("starhud", "hud/fps.png");

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13;
    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public FPSHUD() {
        super(FPS_SETTINGS.base);
    }

    @Override
    public String getName() {
        return "FPS HUD";
    }

    @Override
    public String getId() {
        return "fps";
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        String fpsStr = CLIENT.getCurrentFps() + " FPS";
        int strWidth = CLIENT.textRenderer.getWidth(fpsStr) - 1;

        int width = TEXTURE_WIDTH + 1 + 5 + strWidth + 5;
        int height = TEXTURE_HEIGHT;

        x -= getSettings().getGrowthDirectionHorizontal(width);
        y -= getSettings().getGrowthDirectionVertical(height);

        int color = FPS_SETTINGS.color | 0xFF000000;

        setBoundingBox(x, y, width, height, color);

        RenderUtils.drawSmallHUD(
                context,
                fpsStr,
                x, y,
                width, height,
                FPS_TEXTURE,
                0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                color
        );
        return true;
    }
}
