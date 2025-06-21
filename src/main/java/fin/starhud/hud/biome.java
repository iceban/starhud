package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class biome {

    private static final Settings.BiomeSettings biomeSettings = Main.settings.biomeSettings;

    private static final Identifier DIMENSION_TEXTURE = Identifier.of("starhud", "hud/biome.png");

    private static String cachedFormattedBiomeStr = "";
    private static RegistryEntry<Biome> cachedBiome;
    private static int cachedTextWidth;

    private static final int TEXTURE_WIDTH = 24;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderBiomeIndicatorHUD(DrawContext context) {
        if ((biomeSettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (biomeSettings.hideOn.chat && Helper.isChatFocused())) return;

        TextRenderer textRenderer = CLIENT.textRenderer;

        BlockPos blockPos = CLIENT.player.getBlockPos();
        RegistryEntry<Biome> currentBiome = CLIENT.world.getBiome(blockPos);

        if (cachedBiome != currentBiome) {
            cachedFormattedBiomeStr = biomeNameFormatter(currentBiome.getIdAsString());
            cachedBiome = currentBiome;
            cachedTextWidth = textRenderer.getWidth(cachedFormattedBiomeStr);
        }

        int x = Helper.calculatePositionX(biomeSettings.x, biomeSettings.originX, TEXTURE_WIDTH, biomeSettings.scale)
                - Helper.getGrowthDirection(biomeSettings.textGrowth, cachedTextWidth);
        int y = Helper.calculatePositionY(biomeSettings.y, biomeSettings.originY, TEXTURE_HEIGHT, biomeSettings.scale);

        int dimensionIcon = getDimensionIcon(CLIENT.world.getRegistryKey());
        int color = getTextColorFromDimension(dimensionIcon) | 0xFF000000;

        context.getMatrices().pushMatrix();
        Helper.setHUDScale(context, biomeSettings.scale);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, DIMENSION_TEXTURE, x, y, 0.0F, dimensionIcon * TEXTURE_HEIGHT, 13, TEXTURE_HEIGHT, 13, 52);
        Helper.fillRoundedRightSide(context, x + 14, y, x + 14 + cachedTextWidth + 9, y + TEXTURE_HEIGHT, 0x80000000);
        context.drawText(CLIENT.textRenderer, cachedFormattedBiomeStr, x + 19, y + 3, color, false);

        context.getMatrices().popMatrix();
    }

    private static int getDimensionIcon(RegistryKey<World> registryKey) {
        if (registryKey == World.OVERWORLD) return 0;
        else if (registryKey == World.NETHER) return 1;
        else if (registryKey == World.END) return 2;
        else return 3;
    }

    private static int getTextColorFromDimension(int dimension) {
        return switch (dimension) {
            case 0 -> biomeSettings.color.overworld;
            case 1 -> biomeSettings.color.nether;
            case 2 -> biomeSettings.color.end;
            default -> biomeSettings.color.custom;
        };
    }

    private static String biomeNameFormatter(String oldString) {

        // trim every character from ':' until first index
        oldString = oldString.substring(oldString.indexOf(':') + 1);

        char[] chars = oldString.toCharArray();

        if (chars.length == 0) return "-";

        chars[0] = Character.toUpperCase(chars[0]);
        for (int i = 1; i < chars.length; ++i) {
            if (chars[i] != '_') continue;

            chars[i] = ' ';

            // capitalize the first character after spaces
            if (i + 1 < chars.length) {
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }
}
