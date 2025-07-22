package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.ArmorSettings;
import fin.starhud.helper.Box;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ArmorHUD extends AbstractHUD {

    private static final ArmorSettings ARMOR_SETTINGS = Main.settings.armorSettings;

    private static final Identifier[] ARMOR_BACKGROUND_TEXTURE = {
            Identifier.of("starhud", "hud/helmet.png"),
            Identifier.of("starhud", "hud/chestplate.png"),
            Identifier.of("starhud", "hud/leggings.png"),
            Identifier.of("starhud", "hud/boots.png")

    };

    private static final ArmorSettings.ArmorPieceSetting[] PIECE_SETTINGS = {
            ARMOR_SETTINGS.helmet,
            ARMOR_SETTINGS.chestplate,
            ARMOR_SETTINGS.leggings,
            ARMOR_SETTINGS.boots
    };

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13;
    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private static boolean needBoxUpdate = true;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public ArmorHUD() {
        super(ARMOR_SETTINGS.base);
    }

    @Override
    public String getName() {
        return "Armor HUD";
    }

    @Override
    public String getId() {
        return "armor";
    }

    @Override
    public boolean shouldRender() {
        return super.shouldRender()
                && (PIECE_SETTINGS[0].shouldRender || PIECE_SETTINGS[1].shouldRender || PIECE_SETTINGS[2].shouldRender || PIECE_SETTINGS[3].shouldRender);
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        int armorIndex = 3;

        boolean isRendered = false;

        for (EquipmentSlot equipmentSlot : AttributeModifierSlot.ARMOR) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = CLIENT.player.getEquippedStack(equipmentSlot);
                if (PIECE_SETTINGS[armorIndex].shouldRender && !armor.isEmpty() && armor.isDamageable()) {
                    Box tempBox = RenderUtils.renderDurabilityHUD(
                            context,
                            armor,
                            ARMOR_BACKGROUND_TEXTURE[armorIndex],
                            x + PIECE_SETTINGS[armorIndex].xOffset,
                            y + PIECE_SETTINGS[armorIndex].yOffset,
                            0.0F, 0.0F,
                            TEXTURE_WIDTH, TEXTURE_HEIGHT,
                            ICON_WIDTH, ICON_HEIGHT,
                            PIECE_SETTINGS[armorIndex].color | 0xFF000000,
                            PIECE_SETTINGS[armorIndex].drawBar,
                            PIECE_SETTINGS[armorIndex].drawItem,
                            ARMOR_SETTINGS.base.growthDirectionX,
                            ARMOR_SETTINGS.base.growthDirectionY
                    );
                    isRendered = true;

                    if (needBoxUpdate) {
                        if (super.boundingBox.isEmpty()) {
                            super.boundingBox.setBoundingBox(tempBox.getX(), tempBox.getY(), tempBox.getWidth(), tempBox.getHeight(), tempBox.getColor());
                        } else {
                            super.boundingBox.mergeWith(tempBox);
                        }
                    }
                }
            }
            --armorIndex;
        }

        needBoxUpdate = false;
        return isRendered;
    }

    @Override
    public void update() {
        super.update();

        needBoxUpdate = true;
        super.boundingBox.setEmpty(true);
    }
}
