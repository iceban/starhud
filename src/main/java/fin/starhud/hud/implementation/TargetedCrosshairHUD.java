package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.TargetedCrosshairSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import fin.starhud.hud.HUDId;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;


// HUD similar to JADE's. TargetedCrosshairHUD.
public class TargetedCrosshairHUD extends AbstractHUD {

    private static final TargetedCrosshairSettings TARGETED_CROSSHAIR_SETTINGS = Main.settings.targetedCrosshairSettings;

    private static final Identifier ICON_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/item.png");
    private static final Identifier ENTITY_ICON_TEXTURE = Identifier.of("starhud", "hud/targeted_icon_entity.png");

    // left padding + texture + right padding
    private static final int ICON_BACKGROUND_WIDTH = 3 + 16 + 3;
    private static final int ICON_BACKGROUND_HEIGHT = 3 + 16 + 3;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();


    public TargetedCrosshairHUD() {
        super(TARGETED_CROSSHAIR_SETTINGS.base);
    }

    @Override
    public String getName() {
        return "Targeted Crosshair HUD";
    }

    @Override
    public String getId() {
        return HUDId.TARGETED_CROSSHAIR.toString();
    }

    public static boolean isShown() {

        if (!TARGETED_CROSSHAIR_SETTINGS.base.shouldRender())
            return false;

        return CLIENT.crosshairTarget != null && CLIENT.crosshairTarget.getType() != HitResult.Type.MISS;
    }

    @Override
    public boolean shouldRender() {
        return super.shouldRender() && CLIENT.crosshairTarget.getType() != HitResult.Type.MISS;
    }

    private int width;
    private int height;
    private HitResult.Type hitResultType;

    @Override
    public boolean collectHUDInformation() {
        hitResultType = CLIENT.crosshairTarget.getType();
        return switch (hitResultType) {
            case BLOCK -> collectDataBlock();
            case ENTITY -> collectDataEntity();
            default -> false;
        };
    }

    private ItemStack blockStack;
    private Block cachedBlock = null;
    private OrderedText cachedBlockName = null;
    private String cachedBlockModName = null;
    private int cachedBlockMaxWidth = -1;

    public boolean collectDataBlock() {
        BlockPos pos = ((BlockHitResult) CLIENT.crosshairTarget).getBlockPos();

        BlockState blockState = CLIENT.world.getBlockState(pos);
        Block block = blockState.getBlock();
        Item blockItem = block.asItem();
        blockStack = blockItem.getDefaultStack();

        if (!block.equals(cachedBlock)) {
            cachedBlock = block;

            if (blockItem == Items.AIR) cachedBlockName = Text.translatable(block.getTranslationKey()).asOrderedText();
            else cachedBlockName = blockStack.getName().asOrderedText();

            cachedBlockModName = Helper.getModName(Registries.BLOCK.getId(block));

            int blockNameWidth = CLIENT.textRenderer.getWidth(cachedBlockName);
            int modNameWidth = CLIENT.textRenderer.getWidth(cachedBlockModName);
            cachedBlockMaxWidth = Math.max(modNameWidth, blockNameWidth) - 1;
        }

        width = ICON_BACKGROUND_WIDTH + 1 + 5 + cachedBlockMaxWidth + 5;
        height = ICON_BACKGROUND_HEIGHT;

        setWidth(width);
        setHeight(height);
        return true;
    }

    private int color;
    private Entity cachedTargetedEntity = null;
    private OrderedText cachedEntityName = null;
    private String cachedEntityModName = null;
    private int cachedEntityMaxWidth = -1;
    private int cachedIndex = -1;

    public boolean collectDataEntity() {
        Entity targetedEntity = ((EntityHitResult) CLIENT.crosshairTarget).getEntity();

        if (!targetedEntity.equals(cachedTargetedEntity)) {
            cachedTargetedEntity = targetedEntity;
            cachedEntityName = targetedEntity.getName().asOrderedText();
            cachedEntityModName = Helper.getModName(Registries.ENTITY_TYPE.getId(targetedEntity.getType()));

            int entityNameWidth = CLIENT.textRenderer.getWidth(cachedEntityName);
            int modNameWidth = CLIENT.textRenderer.getWidth(cachedEntityModName);
            cachedEntityMaxWidth = Math.max(entityNameWidth, modNameWidth) - 1;

            cachedIndex = getEntityIconIndex(targetedEntity);
        }

        width = ICON_BACKGROUND_WIDTH + 1 + 5 + cachedEntityMaxWidth + 5;
        height = ICON_BACKGROUND_HEIGHT;
        color = getEntityIconColor(cachedIndex) | 0xFF000000;

        setWidth(width);
        setHeight(height);

        return true;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        return switch (hitResultType) {
            case BLOCK -> renderBlockInfoHUD(context, x, y);
            case ENTITY -> renderEntityInfoHUD(context, x, y);
            default -> false;
        };
    }

    public boolean renderBlockInfoHUD(DrawContext context, int x, int y) {

        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height);

        RenderUtils.drawTextureHUD(
                context,
                ICON_BACKGROUND_TEXTURE,
                x, y,
                0, 0,
                ICON_BACKGROUND_WIDTH, ICON_BACKGROUND_HEIGHT,
                ICON_BACKGROUND_WIDTH, ICON_BACKGROUND_HEIGHT
        );
        RenderUtils.fillRoundedRightSide(
                context,
                x + ICON_BACKGROUND_WIDTH + 1, y,
                x + ICON_BACKGROUND_WIDTH + 1 + 5 + cachedBlockMaxWidth + 5, y + ICON_BACKGROUND_HEIGHT,
                0x80000000
        );

        context.drawItem(blockStack, x + 3, y + 3);
        RenderUtils.drawTextHUD(
                context,
                cachedBlockName,
                x + ICON_BACKGROUND_WIDTH + 1 + 5, y + 3,
                TARGETED_CROSSHAIR_SETTINGS.targetedNameColor | 0xFF000000,
                false
        );
        RenderUtils.drawTextHUD(
                context,
                cachedBlockModName,
                x + ICON_BACKGROUND_WIDTH + 1 + 5,
                y + ICON_BACKGROUND_HEIGHT - 3 - 7,
                TARGETED_CROSSHAIR_SETTINGS.modNameColor | 0xFF000000,
                false
        );

        return true;
    }

    public boolean renderEntityInfoHUD(DrawContext context, int x, int y) {
        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height, color);

        RenderUtils.drawTextureHUD(
                context,
                ENTITY_ICON_TEXTURE,
                x, y,
                0, 22 * cachedIndex,
                ICON_BACKGROUND_WIDTH, ICON_BACKGROUND_HEIGHT,
                ICON_BACKGROUND_WIDTH, ICON_BACKGROUND_HEIGHT * 5,
                color
        );
        RenderUtils.fillRoundedRightSide(
                context,
                x + ICON_BACKGROUND_WIDTH + 1, y,
                x + ICON_BACKGROUND_WIDTH + 1 + 5 + cachedEntityMaxWidth + 5, y + ICON_BACKGROUND_HEIGHT,
                0x80000000
        );

        RenderUtils.drawTextHUD(
                context,
                cachedEntityName,
                x + ICON_BACKGROUND_WIDTH + 1 + 5, y + 3,
                color,
                false
        );
        RenderUtils.drawTextHUD(
                context,
                cachedEntityModName,
                x + ICON_BACKGROUND_WIDTH + 1 + 5, y + ICON_BACKGROUND_HEIGHT - 3 - 7,
                TARGETED_CROSSHAIR_SETTINGS.modNameColor | 0xFF000000,
                false
        );

        return true;
    }

    private int getEntityIconIndex(Entity e) {
        if (isHostileMob(e)) return 0;
        else if (isAngerableMob(e)) return 1;
        else if (isPassiveMob(e)) return 2;
        else if (isPlayerEntity(e)) return 3;
        else return 4;
    }

    private int getEntityIconColor(int index) {
        return switch (index) {
            case 0 -> TARGETED_CROSSHAIR_SETTINGS.entityColors.hostile;
            case 1 -> TARGETED_CROSSHAIR_SETTINGS.entityColors.angerable;
            case 2 -> TARGETED_CROSSHAIR_SETTINGS.entityColors.passive;
            case 3 -> TARGETED_CROSSHAIR_SETTINGS.entityColors.player;
            default -> TARGETED_CROSSHAIR_SETTINGS.entityColors.unknown;
        };
    }

    private static boolean isHostileMob(Entity e) {
        return switch (e) {
            case EnderDragonEntity ignored -> true;
            case EnderDragonPart ignored -> true;
            case Monster ignored -> true;
            default -> false;
        };
    }

    private static boolean isAngerableMob(Entity e) {
        return e instanceof Angerable;
    }

    private static boolean isPassiveMob(Entity e) {
        return switch (e) {
            case PassiveEntity ignored -> true;
            case WaterCreatureEntity ignored -> true;
            case AllayEntity ignored -> true;
            case SnowGolemEntity ignored -> true;
            default -> false;
        };
    }

    private static boolean isPlayerEntity(Entity e) {
        return e instanceof PlayerEntity;
    }
}
