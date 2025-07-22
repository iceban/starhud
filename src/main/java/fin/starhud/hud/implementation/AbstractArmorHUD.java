package fin.starhud.hud.implementation;

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

public abstract class AbstractArmorHUD extends AbstractHUD {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private final ArmorSettings SETTINGS;
    private final Identifier TEXTURE;
    private final int armorIndex;

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13;
    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    public AbstractArmorHUD(ArmorSettings armorSettings, Identifier armorTexture, int armorIndex) {
        super(armorSettings.base);
        this.SETTINGS = armorSettings;
        this.TEXTURE = armorTexture;
        this.armorIndex = armorIndex;
    }

    public ItemStack getStack() {
        EquipmentSlot equipmentSlot = AttributeModifierSlot.ARMOR.getSlots().get(armorIndex);
        return CLIENT.player.getEquippedStack(equipmentSlot);
    }

    @Override
    public boolean shouldRender() {
        return super.shouldRender()
                && (!getStack().isEmpty() && getStack().isDamageable());
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        ItemStack armor = getStack();

        Box tempBox = RenderUtils.renderDurabilityHUD(
                context,
                armor,
                TEXTURE,
                x, y,
                0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                SETTINGS.color | 0xFF000000,
                SETTINGS.drawBar,
                SETTINGS.drawItem,
                SETTINGS.base.growthDirectionX, SETTINGS.base.growthDirectionY
        );

        copyBoundingBox(tempBox);

        return true;
    }
}
