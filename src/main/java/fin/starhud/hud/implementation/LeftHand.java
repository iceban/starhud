package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.HandSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Arm;

public class LeftHand extends Hand{

    private static final HandSetting leftHandSetting = Main.settings.handSettings.leftHandSetting;

    public LeftHand() {
        super(leftHandSetting);
    }

    @Override
    int startV() {
        return 0;
    }

    @Override
    public void renderHUD(DrawContext context) {
        renderHandHUD(context, Arm.LEFT, x, y);
    }
}
