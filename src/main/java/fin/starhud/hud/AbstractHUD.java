package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.config.BaseHUDSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

public abstract class AbstractHUD implements HUDInterface {
    private static final Window WINDOW = MinecraftClient.getInstance().getWindow();

    protected final BaseHUDSetting baseSetting;

    // the actual x and y point we use in HUDs
    protected int x;
    protected int y;

    // temporary x and y point.
    private int xTemp;
    private int yTemp;

    public AbstractHUD(BaseHUDSetting baseSetting) {
        this.baseSetting = baseSetting;
    }

    public abstract int getTextureWidth();

    public abstract int getTextureHeight();

    public void updateX() {
        xTemp = baseSetting.getCalculatedPosX(getTextureWidth());
        x = xTemp;
    }

    public void updateY() {
        yTemp = baseSetting.getCalculatedPosY(getTextureHeight());
        y = yTemp;
    }

    // we update every HUD's x and y points here.
    public void onUpdate() {
        updateX();
        updateY();
    }

    public boolean shouldHide() {
        return  (!baseSetting.on.f3.shouldRender && Helper.isDebugHUDOpen()) ||
                (!baseSetting.on.chat.shouldRender && Helper.isChatFocused()) ||
                (!baseSetting.on.bossBar.shouldRender && Helper.isBossBarShown() ||
                (!baseSetting.on.scoreBoard.shouldRender && Helper.isScoreBoardShown()));
    }

    @Override
    public boolean shouldRender() {
        return baseSetting.shouldRender && !shouldHide();
    }

    @Override
    public boolean isScaled() {
        return baseSetting.scale != 0;
    }

    @Override
    public void setHUDScale(DrawContext context) {
        float scaleFactor = baseSetting.scale / (float) WINDOW.getScaleFactor();
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
                (Helper.isChatFocused() ? baseSetting.on.chat.xOffset : 0) +
                (Helper.isDebugHUDOpen() ? baseSetting.on.f3.xOffset : 0) +
                (Helper.isBossBarShown() ? baseSetting.on.bossBar.xOffset : 0) +
                (Helper.isScoreBoardShown() ? baseSetting.on.scoreBoard.xOffset : 0);
    }

    public void modifyY() {
        y =     yTemp +
                (Helper.isChatFocused() ? baseSetting.on.chat.yOffset : 0) +
                (Helper.isDebugHUDOpen() ? baseSetting.on.f3.yOffset : 0) +
                (Helper.isBossBarShown() ? baseSetting.on.bossBar.yOffset : 0) +
                (Helper.isScoreBoardShown() ? baseSetting.on.scoreBoard.yOffset : 0);
    }
}
