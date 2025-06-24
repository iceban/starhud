package fin.starhud;

import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import fin.starhud.mixin.accessor.AccessorBossBarHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class Helper {

    private static final Identifier DURABILITY_TEXTURE = Identifier.of("starhud", "hud/durability.png");
    private static final Identifier DURABILITY_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/durability_background.png");
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static int getGrowthDirection(GrowthDirectionX growthDirection, int growableWidth) {
        return switch (growthDirection) {
            case LEFT -> growableWidth;
            case CENTER -> growableWidth / 2;
            case RIGHT -> 0;
        };
    }

    public static void fillRoundedRightSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2 - 1, y2, color);
        context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    // get the durability "steps" or progress.
    public static int getItemBarStep(ItemStack stack) {
        return MathHelper.clamp(Math.round(10 - (float) stack.getDamage() * 10 / (float) stack.getMaxDamage()), 0, 10);
    }

    // color transition from pastel (red to green).
    public static int getItemBarColor(int stackStep) {
        return MathHelper.hsvToRgb(0.35F * stackStep / 10.0F, 0.45F, 0.95F);
    }

    public static void renderItemDurabilityHUD(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float v, int textureWidth, int textureHeight, int color) {
        int step = getItemBarStep(stack);
        int durabilityColor = getItemBarColor(step) | 0xFF000000;

        // draw the icon
        context.drawTexture(RenderPipelines.GUI_TEXTURED, ICON, x, y, 0.0F, v, 13, 13, textureWidth, textureHeight, color);

        // draw the durability background and steps
        context.drawTexture(RenderPipelines.GUI_TEXTURED, DURABILITY_BACKGROUND_TEXTURE, x + 14, y, 0.0F, 0.0F, 49, 13, 49, 13);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, DURABILITY_TEXTURE, x + 19, y + 3, 0, 0, 4 * step, 7, 40, 7, durabilityColor);
    }

    public static boolean isChatFocused() {
        return CLIENT.inGameHud.getChatHud().isChatFocused();
    }

    public static boolean isDebugHUDOpen() {
        return CLIENT.getDebugHud().shouldShowDebugHud();
    }

    public static boolean isBossBarShown() {
        return !((AccessorBossBarHud) CLIENT.inGameHud.getBossBarHud()).getBossBars().isEmpty();
    }
}