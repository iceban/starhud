package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.ArmorSettings;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Armor extends AbstractHUD {

    private static final ArmorSettings ARMOR_SETTINGS = Main.settings.armorSettings;

    private static final Identifier ARMOR_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/armor.png");

    private static final int[] X_OFFSETS = new int[4];
    private static final int[] Y_OFFSETS = new int[4];
    private static final boolean[] SHOULD_RENDER = new boolean[4];

    private static final int TEXTURE_WIDTH = 63;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Armor() {
        super(ARMOR_SETTINGS.base);
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

        X_OFFSETS[0] = ARMOR_SETTINGS.helmet.xOffset;
        Y_OFFSETS[0] = ARMOR_SETTINGS.helmet.yOffset;
        X_OFFSETS[1] = ARMOR_SETTINGS.chestplate.xOffset;
        Y_OFFSETS[1] = ARMOR_SETTINGS.chestplate.yOffset;
        X_OFFSETS[2] = ARMOR_SETTINGS.leggings.xOffset;
        Y_OFFSETS[2] = ARMOR_SETTINGS.leggings.yOffset;
        X_OFFSETS[3] = ARMOR_SETTINGS.boots.xOffset;
        Y_OFFSETS[3] = ARMOR_SETTINGS.boots.yOffset;

        SHOULD_RENDER[0] = ARMOR_SETTINGS.helmet.shouldRender;
        SHOULD_RENDER[1] = ARMOR_SETTINGS.chestplate.shouldRender;
        SHOULD_RENDER[2] = ARMOR_SETTINGS.leggings.shouldRender;
        SHOULD_RENDER[3] = ARMOR_SETTINGS.boots.shouldRender;
    }
}
