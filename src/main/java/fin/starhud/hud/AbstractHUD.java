package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.config.BaseHUDSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

public abstract class AbstractHUD implements HUDInterface {
    private static final Window WINDOW = MinecraftClient.getInstance().getWindow();

    protected final BaseHUDSettings baseHUDSettings;

    // the actual x and y point we use in HUDs
    protected int x;
    protected int y;

    // temporary x and y point.
    private int baseX;
    private int baseY;

    public AbstractHUD(BaseHUDSettings baseHUDSettings) {
        this.baseHUDSettings = baseHUDSettings;
    }

    @Override
    public void render(DrawContext context) {
        // I hate this piece of code
        // if Condition is triggered, the X will be modified with xOffset on that condition.
        // example: when bossbar is present, we want to move our hud under the bossbar, or avoid the bossbar.
        // modifyX and modifyY will add our xOffset and yOffset to our initial x position.
        modifyXY();

        // if the HUD' scale is set to default, don't... change the scale...? whatever, this is faster than the one below.
        if (baseHUDSettings.scale == 0) {
            renderHUD(context);
            return;
        }

        // this is so we can change the scale for one hud but not the others.
        context.getMatrices().pushMatrix();
        setHUDScale(context);

        try {
            renderHUD(context);
        } finally {
            context.getMatrices().popMatrix();
        }

    }

    public abstract void renderHUD(DrawContext context);

    public void setHUDScale(DrawContext context) {
        float scaleFactor = baseHUDSettings.scale / (float) WINDOW.getScaleFactor();
        if (scaleFactor == 1) return;

        context.getMatrices().scale(scaleFactor, scaleFactor);
    }

    public void modifyXY() {
        int tempX = 0, tempY = 0;

        if (Helper.isChatFocused()) {
            tempX += baseHUDSettings.on.chat.xOffset;
            tempY += baseHUDSettings.on.chat.yOffset;
        }

        if (Helper.isDebugHUDOpen()) {
            tempX += baseHUDSettings.on.f3.xOffset;
            tempY += baseHUDSettings.on.f3.yOffset;
        }

        if (Helper.isBossBarShown()) {
            tempX += baseHUDSettings.on.bossBar.xOffset;
            tempY += baseHUDSettings.on.bossBar.yOffset;
        }

        if (Helper.isScoreBoardShown()) {
            tempX += baseHUDSettings.on.scoreBoard.xOffset;
            tempY += baseHUDSettings.on.scoreBoard.yOffset;
        }

        x = baseX + tempX;
        y = baseY + tempY;
    }

    public abstract int getBaseHUDWidth();

    public abstract int getBaseHUDHeight();

    public void updateX() {
        baseX = baseHUDSettings.getCalculatedPosX(getBaseHUDWidth());
        x = baseX;
    }

    public void updateY() {
        baseY = baseHUDSettings.getCalculatedPosY(getBaseHUDHeight());
        y = baseY;
    }

    // we update every HUD's x and y points here.
    @Override
    public void update() {
        updateX();
        updateY();
    }

    public boolean shouldHide() {
        return  (!baseHUDSettings.on.f3.shouldRender && Helper.isDebugHUDOpen()) ||
                (!baseHUDSettings.on.chat.shouldRender && Helper.isChatFocused()) ||
                (!baseHUDSettings.on.bossBar.shouldRender && Helper.isBossBarShown()) ||
                (!baseHUDSettings.on.scoreBoard.shouldRender && Helper.isScoreBoardShown());
    }

    @Override
    public boolean shouldRender() {
        return baseHUDSettings.shouldRender && !shouldHide();
    }
}
