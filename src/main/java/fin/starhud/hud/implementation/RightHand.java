package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.HandSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Arm;

public class RightHand extends Hand {

    private static final HandSetting rightHandSetting = Main.settings.handSettings.rightHandSetting;

    public RightHand() {
        super(rightHandSetting);
    }

    @Override
    int startV() {
        return 14;
    }

    @Override
    public void renderHUD(DrawContext context) {
        renderHandHUD(context, Arm.RIGHT, x, y);
    }
}
