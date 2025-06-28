package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.EffectSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// WIP
public class Effect extends AbstractHUD {

    private static final EffectSettings effectSettings = Main.settings.effectSettings;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final Identifier STATUS_EFFECT_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/effect.png");
    private static final Identifier STATUS_EFFECT_BAR_TEXTURE = Identifier.of("starhud", "hud/effect_bar.png");
    private static final Identifier STATUS_EFFECT_AMBIENT_TEXTURE = Identifier.of("starhud", "hud/effect_ambient.png");

    private static final int STATUS_EFFECT_TEXTURE_WIDTH = 24;
    private static final int STATUS_EFFECT_TEXTURE_HEIGHT = 32;
    private static final int STATUS_EFFECT_BAR_TEXTURE_WIDTH = 21;
    private static final int STATUS_EFFECT_BAR_TEXTURE_HEIGHT = 3;

    private static final Map<RegistryEntry<StatusEffect>, StatusEffectAttribute> STATUS_EFFECT_ATTRIBUTE_MAP = new HashMap<>();
    private static final Map<RegistryEntry<StatusEffect>, Identifier> STATUS_EFFECT_TEXTURE_MAP = new HashMap<>();

    public Effect() {
        super(effectSettings.base);
    }

    @Override
    public void renderHUD(DrawContext context) {

        // straight up copied from minecraft's own status effect rendering system.

        Collection<StatusEffectInstance> collection = CLIENT.player.getStatusEffects();
        if (collection.isEmpty())
            return;

        int beneficialIndex = 0;
        int harmIndex = 0;

        int sameTypeGap = effectSettings.sameTypeGap;

        int beneficialSize = getBeneficialSize();
        int harmSize = collection.size() - beneficialSize;

        int xBeneficial = x - effectSettings.growthDirectionX.getGrowthDirection(getDynamicWidth(true, beneficialSize, harmSize));
        int yBeneficial = y - effectSettings.growthDirectionY.getGrowthDirection(getDynamicHeight(true, beneficialSize, harmSize));

        int xHarm = x - effectSettings.growthDirectionX.getGrowthDirection(getDynamicWidth(false, beneficialSize, harmSize));
        int yHarm = y - effectSettings.growthDirectionY.getGrowthDirection(getDynamicHeight(false, beneficialSize, harmSize));

        for (StatusEffectInstance statusEffectInstance : collection) {
            if (!statusEffectInstance.shouldShowIcon())
                continue;

            RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
            StatusEffectAttribute statusEffectAttribute = getStatusEffectAttribute(statusEffectInstance);

            int x2;
            int y2;

            if (registryEntry.value().isBeneficial()) {
                x2 = (xBeneficial) + ((effectSettings.drawVertical ? 0 : sameTypeGap) * beneficialIndex);
                y2 = (yBeneficial) + ((effectSettings.drawVertical ? sameTypeGap : 0) * beneficialIndex);
                ++beneficialIndex;
            } else {
                x2 = (xHarm) + (effectSettings.drawVertical ? effectSettings.differentTypeGap : 0)  + ((effectSettings.drawVertical ? 0 : sameTypeGap) * harmIndex);
                y2 = (yHarm) + (effectSettings.drawVertical ? 0 : effectSettings.differentTypeGap) + ((effectSettings.drawVertical ? sameTypeGap : 0) * harmIndex);
                ++harmIndex;
            }

            if (statusEffectInstance.isAmbient()) {

                // draw soft blue outlined background...
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        STATUS_EFFECT_AMBIENT_TEXTURE,
                        x2, y2,
                        0, 0,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        effectSettings.ambientColor | 0xFF000000
                );

            } else {

                // draw background
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        STATUS_EFFECT_BACKGROUND_TEXTURE,
                        x2, y2,
                        0, 0,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT
                );
            }

            int step, color;
            if (statusEffectInstance.isInfinite()) {
                step = 7;
                color = effectSettings.infiniteColor | 0xFF000000;
            } else {
                int duration = statusEffectInstance.getDuration();
                int maxDuration = statusEffectAttribute.maxDuration;

                step = Helper.getStep(duration, maxDuration, 7);
                color = RenderUtils.getItemBarColor(step, 7) | 0xFF000000;
            }

            // draw timer bar
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    STATUS_EFFECT_BAR_TEXTURE,
                    x2 + 2, y2 + 27,
                    0, 0,
                    3 * step, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                    STATUS_EFFECT_BAR_TEXTURE_WIDTH, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                    color
            );

            // draw effect texture.
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    getStatusEffectTexture(registryEntry),
                    x2 + 3, y2 + 3,
                    0,0,
                    18, 18,
                    18,18
            );

            // draw amplifier text.
            int amplifier = statusEffectAttribute.amplifier + 1;
            if (amplifier == 1)
                continue;

            String amplifierStr = Helper.toSubscript(Integer.toString(amplifier));

            context.drawText(
                    CLIENT.textRenderer,
                    amplifierStr,
                    x2 + 3 + 18 - CLIENT.textRenderer.getWidth(amplifierStr) + 1, y2 + 2 + 18 - 7,
                    0xFFFFFFFF,
                    true
            );

        }
    }

    public int getDynamicWidth(boolean isBeneficial, int beneficialSize, int harmSize) {
         return effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_WIDTH : (isBeneficial ? beneficialSize : harmSize) * effectSettings.sameTypeGap;
    }

    public int getDynamicHeight(boolean isBeneficial, int beneficialSize, int harmSize) {
        return effectSettings.drawVertical ? (isBeneficial ? beneficialSize : harmSize) * effectSettings.sameTypeGap : STATUS_EFFECT_TEXTURE_HEIGHT;
    }

    public int getBeneficialSize() {
        int size = 0;
        for (StatusEffectInstance collection : CLIENT.player.getStatusEffects()) {
            if (collection.getEffectType().value().isBeneficial())
                ++size;
        }
        return size;
    }

    // 0 because the width is dependent to how many status effect are present.

    @Override
    public int getBaseHUDWidth() {
        return 0;
    }

    @Override
    public int getBaseHUDHeight() {
        return 0;
    }

    public static Identifier getStatusEffectTexture(RegistryEntry<StatusEffect> effect) {
        return STATUS_EFFECT_TEXTURE_MAP.computeIfAbsent(
                effect,
                e -> e.getKey()
                        .map(RegistryKey::getValue)
                        .map(id -> Identifier.of(id.getNamespace(), "textures/mob_effect/" + id.getPath() + ".png"))
                        .orElseGet(MissingSprite::getMissingSpriteId)
        );
    }

    // ---------------------------------------------------------------------------------------------- //
    // this Implementation is inspired from @SoRadGaming Simple-HUD-Enhanced StatusEffectTracker class
    // see: https://github.com/SoRadGaming/Simple-HUD-Enhanced/blob/main/src/main/java/com/soradgaming/simplehudenhanced/utli/StatusEffectsTracker.java

    // maxDuration for maxDuration, amplifier and isAmbient to help updating the map.
    public record StatusEffectAttribute(int maxDuration, int amplifier, boolean isAmbient) {}

    public static StatusEffectAttribute getStatusEffectAttribute(StatusEffectInstance effect) {
        return STATUS_EFFECT_ATTRIBUTE_MAP.computeIfAbsent(effect.getEffectType(), key ->
                new StatusEffectAttribute(
                        effect.getDuration(),
                        effect.getAmplifier(),
                        effect.isAmbient()
                )
        );
    }

    public static StatusEffectAttribute updateStatusEffectAttribute(RegistryEntry<StatusEffect> effect, int maxDuration, int amplifier, boolean isAmbient) {
        StatusEffectAttribute newEffect = new StatusEffectAttribute(
                maxDuration,
                amplifier,
                isAmbient
        );

        return STATUS_EFFECT_ATTRIBUTE_MAP.put(effect, newEffect);
    }

    // used when status effect no longer present in player's status effect list.
    public static StatusEffectAttribute removeStatusEffectAttribute(RegistryEntry<StatusEffect> effectRegistry) {
        return STATUS_EFFECT_ATTRIBUTE_MAP.remove(effectRegistry);
    }
}
