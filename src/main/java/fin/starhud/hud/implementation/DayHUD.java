package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.DaySettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import fin.starhud.hud.HUDId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DayHUD extends AbstractHUD {

    private static final DaySettings DAY_SETTINGS = Main.settings.daySettings;

    private static final Identifier DAY_TEXTURE = Identifier.of("starhud", "hud/day.png");

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13;

    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private long lastDay = -1;
    private int cachedTextWidth;
    private String cachedDayString;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public DayHUD() {
        super(DAY_SETTINGS.base);
    }

    @Override
    public String getName() {
        return "Day HUD";
    }

    @Override
    public HUDId getId() {
        return HUDId.DAY;
    }

    private int width;
    private int height;
    private int color;

    @Override
    public boolean collectHUDInformation() {
        long day = CLIENT.world.getTimeOfDay() / 24000L;

        // I cached these because textRendered.getWidth() is expensive.
        // And since day count hardly updates at all, doing this is reasonable.
        if (day != lastDay) {
            lastDay = day;
            cachedDayString = Long.toString(day);
            cachedTextWidth = CLIENT.textRenderer.getWidth(cachedDayString) - 1;
        }

        color = DAY_SETTINGS.color | 0xFF000000;
        width = ICON_WIDTH + 1 + 5 + cachedTextWidth + 5;
        height = ICON_HEIGHT;

        setWidth(width);
        setHeight(height);
        return true;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {

        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height, color);

        RenderUtils.drawSmallHUD(
                context,
                cachedDayString,
                x, y,
                width, height,
                DAY_TEXTURE,
                0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                color
        );
        return true;
    }
}
