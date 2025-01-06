package fin.objhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class armor {

    private static final Identifier ARMOR_TEXTURE = Identifier.of("objhud", "hud/armor.png");
    private static final Identifier ARMOR_ICONS_TEXTURE = Identifier.of("objhud", "hud/armor_icons.png");

    public static void renderArmorHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        final int x = 5;
        final int y = 50;

        int i = 3;
        for (ItemStack armor : client.player.getArmorItems()) {
            if (armor.isItemBarVisible()) {
                int step = getItemBarStep(armor);
                int color = getItemBarColor(step);
                int gap = 14 * i;

                //draw the background
                context.drawTexture(RenderLayer::getGuiTextured, ARMOR_TEXTURE, x, y + gap, 0, gap, 67, 13, 67 ,55);

                // draw the actual information
                context.drawTexture(RenderLayer::getGuiTextured, ARMOR_ICONS_TEXTURE, x, y + gap, 0, gap, 18 + (4 * step), 13, 67, 55, color | 0xFF000000);
            }
            --i;
        }
    }

    public static int getItemBarStep(ItemStack stack) {
        return MathHelper.clamp(Math.round(11 - (float)stack.getDamage() * 11 / (float)stack.getMaxDamage()), 0, 11);
    }

    public static int getItemBarColor(int stackStep) {
        // starting color: A8F4B1 (168, 244, 177)
        // ending color: FF7972 (255, 121, 114)
        float progress = (float)stackStep / 11.0F;

        int r = Math.round(168 + (255 - 168) * (1 - progress));
        int g = Math.round(244 + (121 - 244) * (1 - progress));
        int b = Math.round(177 + (114 - 177) * (1 - progress));

        return (r << 16) | (g << 8) | b;
    }

}