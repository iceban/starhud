package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class armor {

    private static final Settings.ArmorSettings armorSettings = Main.settings.armorSettings;

    private static final Identifier ARMOR_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/armor.png");

    private static final int[] X_OFFSETS = new int[4];
    private static final int[] Y_OFFSETS = new int[4];
    private static final boolean[] SHOULD_RENDER = new boolean[4];

    private static final int TEXTURE_WIDTH = 63;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderArmorHUD(DrawContext context) {
        if ((armorSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (armorSettings.hideOn.chat && Helper.isChatFocused())) return;

        initArmorConfiguration();

        int x = Helper.calculatePositionX(armorSettings.x, armorSettings.originX, TEXTURE_WIDTH, armorSettings.scale);
        int y = Helper.calculatePositionY(armorSettings.y, armorSettings.originY, TEXTURE_HEIGHT, armorSettings.scale);

        int armorIndex = 3;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, armorSettings.scale);

        // for each armor pieces
        for (EquipmentSlot equipmentSlot : AttributeModifierSlot.ARMOR) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = CLIENT.player.getEquippedStack(equipmentSlot);
                if (SHOULD_RENDER[armorIndex] && !armor.isEmpty() && armor.isDamageable()) {
                    Helper.renderItemDurabilityHUD(context, ARMOR_BACKGROUND_TEXTURE, armor, x + X_OFFSETS[armorIndex], y + Y_OFFSETS[armorIndex], 14 * armorIndex, 13, 55, 0xFFFFFFFF);
                }
            }
            --armorIndex;
        }

        context.getMatrices().popMatrix();
    }

    private static void initArmorConfiguration() {
        X_OFFSETS[0] = armorSettings.helmet.xOffset;
        Y_OFFSETS[0] = armorSettings.helmet.yOffset;
        X_OFFSETS[1] = armorSettings.chestplate.xOffset;
        Y_OFFSETS[1] = armorSettings.chestplate.yOffset;
        X_OFFSETS[2] = armorSettings.leggings.xOffset;
        Y_OFFSETS[2] = armorSettings.leggings.yOffset;
        X_OFFSETS[3] = armorSettings.boots.xOffset;
        Y_OFFSETS[3] = armorSettings.boots.yOffset;

        SHOULD_RENDER[0] = armorSettings.helmet.shouldRender;
        SHOULD_RENDER[1] = armorSettings.chestplate.shouldRender;
        SHOULD_RENDER[2] = armorSettings.leggings.shouldRender;
        SHOULD_RENDER[3] = armorSettings.boots.shouldRender;
    }

}