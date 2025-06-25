package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.DaySetting;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class Day extends AbstractHUD {

    private static final DaySetting daySetting = Main.settings.daySetting;

    private static final Identifier DAY_TEXTURE = Identifier.of("starhud", "hud/day.png");

    private static final int TEXTURE_HEIGHT = 13;
    private static final int TEXTURE_WIDTH = 13 + 1 + 5 + 5;

    private int cachedTextLength = -1;
    private int cachedTextWidth;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Day() {
        super(daySetting.base);
    }

    @Override
    public void renderHUD(DrawContext context) {

        long day = CLIENT.world.getTimeOfDay() / 24000L;
        String dayStr = Long.toString(day);
        int textLength = dayStr.length();

        // I cached these because textRendered.getWidth() is expensive.
        // And since day count hardly updates at all, doing this is reasonable.
        if (textLength != cachedTextLength) {
            cachedTextLength = textLength;
            cachedTextWidth = CLIENT.textRenderer.getWidth(dayStr);
        }

        int xTemp = x - daySetting.textGrowth.getGrowthDirection(cachedTextWidth);
        int color = daySetting.color | 0xFF000000;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, DAY_TEXTURE, xTemp, y, 0.0F, 0, 13, TEXTURE_HEIGHT, 13,  TEXTURE_HEIGHT);
        Helper.fillRoundedRightSide(context, xTemp + 14, y, xTemp + 14 + cachedTextWidth + 9, y + TEXTURE_HEIGHT, 0x80000000);
        context.drawText(CLIENT.textRenderer, dayStr, xTemp + 19, y + 3, color, false);
    }

    @Override
    public int getTextureWidth() {
        return TEXTURE_WIDTH;
    }

    @Override
    public int getTextureHeight() {
        return TEXTURE_HEIGHT;
    }
}
