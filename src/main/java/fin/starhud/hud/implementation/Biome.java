package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.BiomeSetting;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Biome extends AbstractHUD {

    private static final BiomeSetting biomeSetting = Main.settings.biomeSetting;

    private static final Identifier DIMENSION_TEXTURE = Identifier.of("starhud", "hud/biome.png");

    private static String cachedFormattedBiomeStr = "";
    private static RegistryEntry<net.minecraft.world.biome.Biome> cachedBiome;
    private static int cachedTextWidth;

    private static final int TEXTURE_WIDTH = 24;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Biome() {
        super(biomeSetting.base);
    }

    @Override
    public void renderHUD(DrawContext context) {
        TextRenderer textRenderer = CLIENT.textRenderer;

        BlockPos blockPos = CLIENT.player.getBlockPos();
        RegistryEntry<net.minecraft.world.biome.Biome> currentBiome = CLIENT.world.getBiome(blockPos);

        if (cachedBiome != currentBiome) {
            cachedFormattedBiomeStr = biomeNameFormatter(currentBiome.getIdAsString());
            cachedBiome = currentBiome;
            cachedTextWidth = textRenderer.getWidth(cachedFormattedBiomeStr);
        }

        int dimensionIcon = getDimensionIcon(CLIENT.world.getRegistryKey());
        int color = getTextColorFromDimension(dimensionIcon) | 0xFF000000;

        int xTemp = x - biomeSetting.textGrowth.getGrowthDirection(cachedTextWidth);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, DIMENSION_TEXTURE, xTemp, y, 0.0F, dimensionIcon * TEXTURE_HEIGHT, 13, TEXTURE_HEIGHT, 13, 52);
        Helper.fillRoundedRightSide(context, xTemp + 14, y, xTemp + 14 + cachedTextWidth + 9, y + TEXTURE_HEIGHT, 0x80000000);
        context.drawText(CLIENT.textRenderer, cachedFormattedBiomeStr, xTemp + 19, y + 3, color, false);
    }

    private static int getDimensionIcon(RegistryKey<World> registryKey) {
        if (registryKey == World.OVERWORLD) return 0;
        else if (registryKey == World.NETHER) return 1;
        else if (registryKey == World.END) return 2;
        else return 3;
    }

    private static int getTextColorFromDimension(int dimension) {
        return switch (dimension) {
            case 0 -> biomeSetting.color.overworld;
            case 1 -> biomeSetting.color.nether;
            case 2 -> biomeSetting.color.end;
            default -> biomeSetting.color.custom;
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

    @Override
    public int getTextureWidth() {
        return TEXTURE_WIDTH;
    }

    @Override
    public int getTextureHeight() {
        return TEXTURE_HEIGHT;
    }
}
