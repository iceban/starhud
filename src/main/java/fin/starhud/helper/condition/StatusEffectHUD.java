package fin.starhud.helper.condition;

import fin.starhud.hud.HUDComponent;
import fin.starhud.hud.HUDId;
import fin.starhud.hud.implementation.EffectHUD;
import net.minecraft.client.MinecraftClient;

public class StatusEffectHUD {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final EffectHUD EFFECT_HUD = (EffectHUD) HUDComponent.getInstance().getHUD(HUDId.EFFECT);

    public static boolean isPositiveShown() {
        return EffectHUD.getBeneficialSize() > 0;
    }

    public static int getPositiveWidth() {
        if (EFFECT_HUD.shouldRender()) {
            int beneficialSize = EffectHUD.beneficialSize;
            int harmSize = EffectHUD.harmSize;
            return EFFECT_HUD.getDynamicHeight(false, beneficialSize, harmSize);
        } else {
            return EffectHUD.getBeneficialSize() * 25 - 1;
        }
    }

    public static int getPositiveHeight() {
        if (EFFECT_HUD.shouldRender()) {
            int beneficialSize = EffectHUD.beneficialSize;
            int harmSize = EffectHUD.harmSize;
            return EFFECT_HUD.getDynamicHeight(false, beneficialSize, harmSize);
        } else {
            return 24;
        }
    }

    public static boolean isNegativeShown() {
        return CLIENT.player.getStatusEffects().size() - EffectHUD.getBeneficialSize() > 0;
    }

    public static int getNegativeWidth() {
        if (EFFECT_HUD.shouldRender()) {
            int beneficialSize = EffectHUD.beneficialSize;
            int harmSize = EffectHUD.harmSize;
            return EFFECT_HUD.getDynamicHeight(false, beneficialSize, harmSize);
        } else {
            return EffectHUD.getBeneficialSize() * 25 - 1;
        }
    }

    public static int getNegativeHeight() {
        if (EFFECT_HUD.shouldRender()) {
            int beneficialSize = EffectHUD.beneficialSize;
            int harmSize = EffectHUD.harmSize;
            return EFFECT_HUD.getDynamicHeight(false, beneficialSize, harmSize);
        } else {
            return 24;
        }
    }

}
