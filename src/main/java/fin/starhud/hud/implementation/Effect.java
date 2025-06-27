package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.ConditionalSettings;
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

    public Effect() {
        super(effectSettings.base);
    }

    public static boolean shouldStatusEffectRender() {
        if (effectSettings.base.shouldRender) return true;

        for (ConditionalSettings condition : effectSettings.base.conditions) {
            if (!condition.shouldRender & condition.isConditionMet())
                return true;
        }

        return false;
    }

    @Override
    public void renderHUD(DrawContext context) {

        // straight up copied from minecraft's own status effect rendering system.

        Collection<StatusEffectInstance> collection = CLIENT.player.getStatusEffects();
        if (collection.isEmpty())
            return;

        int beneficialIndex = 0;
        int harmIndex = 0;

        for (StatusEffectInstance statusEffectInstance : collection) {
            if (!statusEffectInstance.shouldShowIcon())
                continue;

            RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();

            int xTemp = x;
            int yTemp = y;

            if (registryEntry.value().isBeneficial()) {
                xTemp += effectSettings.beneficialGapX * beneficialIndex;
                yTemp += effectSettings.beneficialGapY * beneficialIndex;
                ++beneficialIndex;
            } else {
                xTemp += effectSettings.gapX + (effectSettings.harmGapX * harmIndex);
                yTemp += effectSettings.gapY + (effectSettings.harmGapY * harmIndex);
                ++harmIndex;
            }

            if (statusEffectInstance.isAmbient()) {

                // draw Soft blue bar and blue outline.

                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        STATUS_EFFECT_AMBIENT_TEXTURE,
                        xTemp, yTemp,
                        0, 0,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT
                );

            } else {
                // draw Timer Bar.
                int duration = statusEffectInstance.getDuration();
                int maxDuration = 20 * 60; // getMaxDuration() WIP

                int step = Helper.getStep(duration, maxDuration, 7);
                int color = RenderUtils.getItemBarColor(step, 7) | 0xFF000000;

                // draw background
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        STATUS_EFFECT_BACKGROUND_TEXTURE,
                        xTemp, yTemp,
                        0, 0,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT
                );

                // draw bar
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        STATUS_EFFECT_BAR_TEXTURE,
                        xTemp + 2, yTemp + 27,
                        0, 0,
                        3 * step, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                        STATUS_EFFECT_BAR_TEXTURE_WIDTH, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                        color
                );
            }

            // draw effect texture.
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    getEffectTexture(registryEntry),
                    xTemp + 3, yTemp + 3,
                    0,0,
                    18, 18,
                    18,18
            );
        }
    }

    public static Identifier getEffectTexture(RegistryEntry<StatusEffect> effect) {
        return effect.getKey()
                .map(RegistryKey::getValue)
                .map(id -> Identifier.of(id.getNamespace(), "textures/mob_effect/" + id.getPath() + ".png"))
                .orElseGet(MissingSprite::getMissingSpriteId);
    }

    @Override
    public int getBaseHUDWidth() {
        return STATUS_EFFECT_TEXTURE_WIDTH;
    }

    @Override
    public int getBaseHUDHeight() {
        return STATUS_EFFECT_TEXTURE_HEIGHT;
    }
}
