package fin.starhud.hud;

import net.minecraft.client.gui.DrawContext;

public interface HUDInterface {

    default void render(DrawContext context) {
        // I hate this piece of code
        // if Condition is triggered, the X will be modified with xOffset on that condition.
        // example: when bossbar is present, we want to move our hud under the bossbar, or avoid the bossbar.
        // modifyX and modifyY will add our xOffset and yOffset to our initial x position.
        modifyXY();

        if (!isScaled()) { // if the HUD' scale is set to default, don't... change the scale...? whatever, this is faster than the one below.
            renderHUD(context);
            return;
        }

        // this is so we can change the scale for one hud but not the others.
        context.getMatrices().pushMatrix();
        setHUDScale(context);

        renderHUD(context);

        context.getMatrices().popMatrix();

    }

    // if not enabled, or "Hide On" condition is enabled and are active.
    boolean shouldRender();

    void onUpdate();

    // if user set HUD scale to anything other than default.
    boolean isScaled();

    // setX and Y.
    void modifyXY();

    // this is the actual HUD rendering implementation. each HUD obviously has different implementation.
    void renderHUD(DrawContext context);

    // change the HUD scale. according to their respective configuration.
    void setHUDScale(DrawContext context);
}
