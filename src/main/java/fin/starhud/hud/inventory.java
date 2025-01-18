package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class inventory {

    private static final Identifier INVENTORY_TEXTURE = Identifier.of("starhud", "hud/inventory.png");

    private static Settings.InventorySettings inventory = Main.settings.inventorySettings;

    private static final int width = 206;
    private static final int height = 68;

    public static void renderInventoryHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((inventory.hideOn.f3 && Helper.isDebugHUDOpen()) || (inventory.hideOn.chat && Helper.isChatFocused())) return;

        int x = Helper.calculatePositionX(inventory.x, inventory.originX, client.getWindow(), width, inventory.scale);
        int y = Helper.calculatePositionY(inventory.y, inventory.originY, client.getWindow(), height, inventory.scale);

        PlayerInventory playerInventory = client.player.getInventory();

        context.getMatrices().push();
        Helper.setHUDScale(context, client.getWindow(), inventory.scale);
        context.drawTexture(RenderLayer::getGuiTextured, INVENTORY_TEXTURE, x, y, 0.0F, 0.0F, width, height, width, height);

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
