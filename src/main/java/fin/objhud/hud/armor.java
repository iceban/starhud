package fin.objhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class armor {
    public static void renderArmorHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;

        final int x = client.getWindow().getScaledWidth() / 2 - 7;
        int y = client.getWindow().getScaledHeight() - 39;

        for (ItemStack armor : client.player.getArmorItems()) {
            drawItemBar(context, armor, x, y);
            y -= 3;
        }
    }

    public static void drawItemBar(DrawContext context, ItemStack stack, int x, int y) {
        if (stack.isItemBarVisible()) {
            context.fill(x, y, x + 13, y + 2, -16777216);
            context.fill(x, y, x + stack.getItemBarStep(), y + 1, stack.getItemBarColor() | 0xFF000000);
        }
    }
}