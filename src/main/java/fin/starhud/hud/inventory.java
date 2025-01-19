package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class inventory {

    private static final Identifier INVENTORY_TEXTURE = Identifier.of("starhud", "hud/inventory.png");
    private static final Identifier INVENTORY_TEXTURE_VERTICAL = Identifier.of("starhud", "hud/inventory_vertical.png");

    private static Settings.InventorySettings inventory = Main.settings.inventorySettings;

    private static final int width = 206;
    private static final int height = 68;

    public static void renderInventoryHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if ((inventory.hideOn.f3 && Helper.isDebugHUDOpen()) || (inventory.hideOn.chat && Helper.isChatFocused())) return;

        PlayerInventory playerInventory = client.player.getInventory();

        if (isInventoryEmpty(playerInventory)) return;

        context.getMatrices().push();
        Helper.setHUDScale(context, client.getWindow(), inventory.scale);

        if (inventory.drawVertical) {
            int x = Helper.calculatePositionX(inventory.x, inventory.originX, client.getWindow(), height, inventory.scale);
            int y = Helper.calculatePositionY(inventory.y, inventory.originY, client.getWindow(), width, inventory.scale);
            drawInventoryVertical(playerInventory, client.textRenderer, context, x, y);
        } else {
            int x = Helper.calculatePositionX(inventory.x, inventory.originX, client.getWindow(), width, inventory.scale);
            int y = Helper.calculatePositionY(inventory.y, inventory.originY, client.getWindow(), height, inventory.scale);
            drawInventoryHorizontal(playerInventory, client.textRenderer, context, x, y);
        }

        context.getMatrices().pop();
    }

    private static void drawInventoryVertical(PlayerInventory inventory, TextRenderer textRenderer, DrawContext context, int x, int y) {
        context.drawTexture(RenderLayer::getGuiTextured, INVENTORY_TEXTURE_VERTICAL, x, y, 0.0F, 0.0F, height, width, height, width);

        int x1 = x + 49 + 23;
        int y1 = y + 3;

        for (int i = 9; i < 36; ++i) {
            if (i % 9 == 0) {
                y1 = y + 3;
                x1 -= 23;
            }

            ItemStack stack = inventory.main.get(i);

            context.drawItem(stack, x1, y1);
            context.drawStackOverlay(textRenderer, stack, x1, y1);

            y1 += 23;
        }
    }

    private static void drawInventoryHorizontal(PlayerInventory inventory, TextRenderer textRenderer, DrawContext context, int x, int y) {
        context.drawTexture(RenderLayer::getGuiTextured, INVENTORY_TEXTURE, x, y, 0.0F, 0.0F, width, height, width, height);

        int x1 = x + 3;
        int y1 = y + 3 - 23;

        for (int i = 9; i < 36; ++i) {

            if (i % 9 == 0) {
                y1 += 23;
                x1 = x + 3;
            }

            ItemStack stack = inventory.main.get(i);

            context.drawItem(stack, x1, y1);
            context.drawStackOverlay(textRenderer, stack, x1, y1);

            x1 += 23;
        }
    }

    private static boolean isInventoryEmpty(PlayerInventory inventory) {
        for (int i = 9; i < 36; ++i)
            if (!inventory.main.get(i).isEmpty())
                return false;
        return true;
    }
}
