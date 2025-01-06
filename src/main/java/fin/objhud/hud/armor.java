package fin.objhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class armor {

    private static final Identifier ARMOR_BACKGROUND_TEXTURE = Identifier.of("objhud", "hud/armor.png");
    private static final Identifier ARMOR_ICONS_TEXTURE = Identifier.of("objhud", "hud/armor_icons.png");

    public static void renderArmorHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        final int x = 5;
        final int y = 51;

        int i = 3;
        for (ItemStack armor : client.player.getArmorItems()) {
            if (armor.isItemBarVisible())
                renderArmorPieces(context, armor, x, y, 14 * i);
            --i;
        }
    }

    public static void renderArmorPieces(DrawContext context, ItemStack armor, int x, int y, int gap) {
        int step = getItemBarStep(armor);
        int color = getItemBarColor(step);

        // draw the background
        context.drawTexture(RenderLayer::getGuiTextured, ARMOR_BACKGROUND_TEXTURE, x, y + gap, 0, gap, 63, 13, 63 ,55);
        // draw the information
        context.drawTexture(RenderLayer::getGuiTextured, ARMOR_ICONS_TEXTURE, x, y + gap, 0, gap, 18 + (4 * step), 13, 63, 55, color | 0xFF000000);
    }

    public static int getItemBarStep(ItemStack stack) {
        return MathHelper.clamp(Math.round(10 - (float)stack.getDamage() * 10 / (float)stack.getMaxDamage()), 0, 10);
    }

    public static int getItemBarColor(int stackStep) {
        // Using HSV interpolation for smoother transition
        // We'll slightly adjust saturation and value to maintain pastel quality
        float progress = (float)stackStep / 10.0F;

        return MathHelper.hsvToRgb(0.35F * progress, 0.45F, 0.95F);
    }

}