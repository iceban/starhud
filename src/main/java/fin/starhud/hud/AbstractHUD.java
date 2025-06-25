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
    private int xTemp;
    private int yTemp;

    public AbstractHUD(BaseHUDSettings baseHUDSettings) {
        this.baseHUDSettings = baseHUDSettings;
    }

    public abstract int getTextureWidth();

    public abstract int getTextureHeight();

    public void updateX() {
        xTemp = baseHUDSettings.getCalculatedPosX(getTextureWidth());
        x = xTemp;
    }

    public void updateY() {
        yTemp = baseHUDSettings.getCalculatedPosY(getTextureHeight());
        y = yTemp;
    }

    // we update every HUD's x and y points here.
    public void onUpdate() {
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

    @Override
    public boolean isScaled() {
        return baseHUDSettings.scale != 0;
    }

    @Override
    public void setHUDScale(DrawContext context) {
        float scaleFactor = baseHUDSettings.scale / (float) WINDOW.getScaleFactor();
        if (scaleFactor == 1) return;

        context.getMatrices().scale(scaleFactor, scaleFactor);
    }

    @Override
    public void modifyXY() {
        modifyX();
        modifyY();
    }

    public void modifyX() {
        x =     xTemp +
                (Helper.isChatFocused() ? baseHUDSettings.on.chat.xOffset : 0) +
                (Helper.isDebugHUDOpen() ? baseHUDSettings.on.f3.xOffset : 0) +
                (Helper.isBossBarShown() ? baseHUDSettings.on.bossBar.xOffset : 0) +
                (Helper.isScoreBoardShown() ? baseHUDSettings.on.scoreBoard.xOffset : 0);
    }

    public void modifyY() {
        y =     yTemp +
                (Helper.isChatFocused() ? baseHUDSettings.on.chat.yOffset : 0) +
                (Helper.isDebugHUDOpen() ? baseHUDSettings.on.f3.yOffset : 0) +
                (Helper.isBossBarShown() ? baseHUDSettings.on.bossBar.yOffset : 0) +
                (Helper.isScoreBoardShown() ? baseHUDSettings.on.scoreBoard.yOffset : 0);
    }
}
