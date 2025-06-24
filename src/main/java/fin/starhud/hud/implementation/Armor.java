package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.ArmorSetting;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Armor extends AbstractHUD {

    private static final ArmorSetting armorSetting = Main.settings.armorSetting;

    private static final Identifier ARMOR_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/armor.png");

    private static final int[] X_OFFSETS = new int[4];
    private static final int[] Y_OFFSETS = new int[4];
    private static final boolean[] SHOULD_RENDER = new boolean[4];

    private static final int TEXTURE_WIDTH = 63;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Armor() {
        super(armorSetting.base);
    }

    @Override
    public int getTextureWidth() {
        return TEXTURE_WIDTH;
    }

    @Override
    public int getTextureHeight() {
        return TEXTURE_HEIGHT;
    }

    @Override
    public void renderHUD(DrawContext context) {
        int armorIndex = 3;
        for (EquipmentSlot equipmentSlot : AttributeModifierSlot.ARMOR) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = CLIENT.player.getEquippedStack(equipmentSlot);
                if (SHOULD_RENDER[armorIndex] && !armor.isEmpty() && armor.isDamageable()) {
                    Helper.renderItemDurabilityHUD (
                            context,
                            ARMOR_BACKGROUND_TEXTURE,
                            armor,
                            x + X_OFFSETS[armorIndex],
                            y + Y_OFFSETS[armorIndex],
                            14 * armorIndex,
                            13,
                            TEXTURE_HEIGHT * 4 + 3,
                            0xFFFFFFFF
                    );
                }
            }
            --armorIndex;
        }
    }

    @Override
    public void onUpdate() {
        updateX();
        updateY();

        X_OFFSETS[0] = armorSetting.helmet.xOffset;
        Y_OFFSETS[0] = armorSetting.helmet.yOffset;
        X_OFFSETS[1] = armorSetting.chestplate.xOffset;
        Y_OFFSETS[1] = armorSetting.chestplate.yOffset;
        X_OFFSETS[2] = armorSetting.leggings.xOffset;
        Y_OFFSETS[2] = armorSetting.leggings.yOffset;
        X_OFFSETS[3] = armorSetting.boots.xOffset;
        Y_OFFSETS[3] = armorSetting.boots.yOffset;

        SHOULD_RENDER[0] = armorSetting.helmet.shouldRender;
        SHOULD_RENDER[1] = armorSetting.chestplate.shouldRender;
        SHOULD_RENDER[2] = armorSetting.leggings.shouldRender;
        SHOULD_RENDER[3] = armorSetting.boots.shouldRender;
    }
}
