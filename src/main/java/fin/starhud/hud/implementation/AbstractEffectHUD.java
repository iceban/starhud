package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.config.hud.EffectSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.helper.StatusEffectAttribute;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEffectHUD extends AbstractHUD {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final Identifier STATUS_EFFECT_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/effect.png");
    private static final Identifier STATUS_EFFECT_BAR_TEXTURE = Identifier.of("starhud", "hud/effect_bar.png");
    private static final Identifier STATUS_EFFECT_AMBIENT_TEXTURE = Identifier.of("starhud", "hud/effect_ambient.png");

    private static final int STATUS_EFFECT_TEXTURE_WIDTH = 24;
    private static final int STATUS_EFFECT_TEXTURE_HEIGHT = 32;
    private static final int STATUS_EFFECT_BAR_TEXTURE_WIDTH = 21;
    private static final int STATUS_EFFECT_BAR_TEXTURE_HEIGHT = 3;

    private static final Map<RegistryEntry<StatusEffect>, Identifier> STATUS_EFFECT_TEXTURE_MAP = new HashMap<>();

    private final EffectSettings effectSettings;

    public AbstractEffectHUD(EffectSettings effectSettings) {
        super(effectSettings.base);
        this.effectSettings = effectSettings;
    }

    public abstract boolean isEffectAllowedToRender(RegistryEntry<StatusEffect> registryEntry);

    public int size;
    private int width;
    private int height;
    private int sameTypeGap;

    private boolean drawVertical;

    Collection<StatusEffectInstance> collection;

    @Override
    public boolean shouldRender() {
        return super.shouldRender() && !CLIENT.player.getStatusEffects().isEmpty();
    }

    @Override
    public boolean collectHUDInformation() {

        collection = CLIENT.player.getStatusEffects();

        size = 0;
        for (StatusEffectInstance instance : collection) {
            if (instance.shouldShowIcon() && isEffectAllowedToRender(instance.getEffectType()))
                ++size;
        }

        if (size == 0) return false;

        width = getDynamicWidth(size);
        height = getDynamicHeight(size);

        drawVertical = effectSettings.drawVertical;
        sameTypeGap = getSameTypeGap();

        return true;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {

        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height);

        for (StatusEffectInstance statusEffectInstance : collection) {

            if (drawStatusEffectHUD(context, x, y, statusEffectInstance)) {
                if (drawVertical) {
                    y += sameTypeGap;
                } else {
                    x += sameTypeGap;
                }
            }
        }

        return true;
    }

    public boolean drawStatusEffectHUD(DrawContext context, int x, int y, StatusEffectInstance statusEffectInstance) {
        if (!statusEffectInstance.shouldShowIcon())
            return false;

        RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();

        if (!isEffectAllowedToRender(registryEntry))
            return false;

        StatusEffectAttribute statusEffectAttribute = StatusEffectAttribute.getStatusEffectAttribute(statusEffectInstance);

        if (statusEffectInstance.isAmbient()) {

            // draw soft blue outlined background...
            RenderUtils.drawTextureHUD(
                    context,
                    STATUS_EFFECT_AMBIENT_TEXTURE,
                    x, y,
                    0, 0,
                    STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                    STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                    effectSettings.ambientColor | 0xFF000000
            );

        } else {

            // draw background
            RenderUtils.drawTextureHUD(
                    context,
                    STATUS_EFFECT_BACKGROUND_TEXTURE,
                    x, y,
                    0, 0,
                    STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                    STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT
            );
        }

        int duration = statusEffectInstance.getDuration();

        int step, color;
        if (statusEffectInstance.isInfinite()) {
            step = 7;
            color = effectSettings.infiniteColor | 0xFF000000;
        } else {
            int maxDuration = statusEffectAttribute.maxDuration();

            step = Helper.getStep(duration, maxDuration, 7);
            color = (effectSettings.useEffectColor ? registryEntry.value().getColor() : AbstractDurabilityHUD.getItemBarColor(step, 7)) | 0xFF000000;
        }

        // draw timer bar
        RenderUtils.drawTextureHUD(
                context,
                STATUS_EFFECT_BAR_TEXTURE,
                x + 2, y + 27,
                0, 0,
                3 * step, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                STATUS_EFFECT_BAR_TEXTURE_WIDTH, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                color
        );

        float alpha = 1.0F;
        if (duration <= 200 && !statusEffectInstance.isInfinite()) { // minecraft's status effect blinking.
            int n = 10 - duration / 20;
            alpha = MathHelper.clamp((float)duration / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)duration * (float)Math.PI / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
            alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
        }

        // draw effect texture.
        RenderUtils.drawTextureHUD(
                context,
                getStatusEffectTexture(registryEntry),
                x + 3, y + 3,
                0,0,
                18, 18,
                18,18,
                ColorHelper.getWhite(alpha)
        );

        // draw amplifier text.
        int amplifier = statusEffectAttribute.amplifier() + 1;
        if (amplifier == 1) {
            return true;
        }

        String amplifierStr = Helper.toSubscript(Integer.toString(amplifier));

        RenderUtils.drawTextHUD(
                context,
                amplifierStr,
                x + 3 + 18 - CLIENT.textRenderer.getWidth(amplifierStr) + 1, y + 2 + 18 - 7,
                0xFFFFFFFF,
                true
        );

        return true;
    }

    public int getDynamicWidth(int size) {
        // if we draw the HUD vertically, essentially the width should be the texture width
        return effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_WIDTH
                // else, the width should be the whole column of Effect HUDs.
                : (size * getSameTypeGap()) - effectSettings.sameTypeGap;
    }

    public int getDynamicHeight(int size) {
        // if the HUD is drawn Vertically, the Height should be the whole row of Effect HUDs
        return effectSettings.drawVertical ? (size * getSameTypeGap()) - effectSettings.sameTypeGap
                // else, the height is just the same as the texture height.
                : STATUS_EFFECT_TEXTURE_HEIGHT;
    }

    public int getSameTypeGap() {
        return (effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_HEIGHT : STATUS_EFFECT_TEXTURE_WIDTH) + effectSettings.sameTypeGap;
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
}
