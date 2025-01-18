package fin.starhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class inventory {

    private static final Identifier INVENTORY_TEXTURE = Identifier.of("starhud", "hud/inventory.png");

    public static void renderInventoryHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        int x = 200 * client.getWindow().getWidth() / context.getScaledWindowWidth();
        int y = 200 * client.getWindow().getWidth() / context.getScaledWindowWidth();

        PlayerInventory playerInventory = client.player.getInventory();

        float uhh = context.getScaledWindowWidth() * 1.0F / client.getWindow().getWidth();

        context.getMatrices().push();
        context.getMatrices().scale(uhh, uhh, uhh);
        context.drawTexture(RenderLayer::getGuiTextured, INVENTORY_TEXTURE, x, y, 0.0F, 0.0F, 206, 68, 206, 68);

        int lx = x + 3;
        int ly = y + 3;

        for (int i = 9; i < 36; ++i) {

            if (i % 9 == 0 && i != 9) {
                ly += 23;
                lx = x + 3;
            }

            ItemStack stack = playerInventory.getStack(i);

            context.drawItem(stack, lx, ly);
            context.drawStackOverlay(client.textRenderer, stack, lx, ly);

            lx += 23;
        }
        context.getMatrices().pop();
    }
}
