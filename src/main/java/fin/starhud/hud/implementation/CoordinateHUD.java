package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.CoordSettings;
import fin.starhud.helper.Box;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class CoordinateHUD extends AbstractHUD {

    private static final CoordSettings COORD_SETTINGS = Main.settings.coordSettings;

    private static final Identifier COORD_X_TEXTURE = Identifier.of("starhud", "hud/coordinate_x.png");
    private static final Identifier COORD_Y_TEXTURE = Identifier.of("starhud", "hud/coordinate_y.png");
    private static final Identifier COORD_Z_TEXTURE = Identifier.of("starhud", "hud/coordinate_z.png");

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13;
    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static boolean needBoxUpdate = true;
    private static final Box tempBox = new Box(0, 0);

    public CoordinateHUD() {
        super(COORD_SETTINGS.base);
    }

    @Override
    public boolean shouldRender() {
        return super.shouldRender()
                && (COORD_SETTINGS.X.shouldRender || COORD_SETTINGS.Y.shouldRender || COORD_SETTINGS.Z.shouldRender);
    }

    @Override
    public String getName() {
        return "Coordinate HUD";
    }

    @Override
    public String getId() {
        return "coordinate";
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        Vec3d vec3d = CLIENT.player.getPos();

        String coordX = Integer.toString((int) vec3d.x);
        String coordY = Integer.toString((int) vec3d.y);
        String coordZ = Integer.toString((int) vec3d.z);

        if (COORD_SETTINGS.X.shouldRender)
            renderEachCoordinate(context, coordX, COORD_X_TEXTURE, x, y, COORD_SETTINGS.X);

        if (COORD_SETTINGS.Y.shouldRender)
            renderEachCoordinate(context, coordY, COORD_Y_TEXTURE, x, y, COORD_SETTINGS.Y);

        if (COORD_SETTINGS.Z.shouldRender)
            renderEachCoordinate(context, coordZ, COORD_Z_TEXTURE, x, y, COORD_SETTINGS.Z);

        needBoxUpdate = false;
        return true;
    }

    @Override
    public void update() {
        super.update();

        needBoxUpdate = true;
        super.boundingBox.setEmpty(true);
    }

    public void renderEachCoordinate(DrawContext context, String str, Identifier iconTexture, int x, int y, CoordSettings.CoordPieceSetting pieceSetting) {

        int textWidth = CLIENT.textRenderer.getWidth(str) - 1;
        int width = ICON_WIDTH + 1 + 5 + textWidth + 5;
        int height = ICON_HEIGHT;

        x += pieceSetting.xOffset - getSettings().getGrowthDirectionHorizontal(width);
        y += pieceSetting.yOffset - getSettings().getGrowthDirectionVertical(height);

        int color = pieceSetting.color | 0xFF000000;

        tempBox.setBoundingBox(x, y, width, height, color);

        RenderUtils.drawSmallHUD(
                context,
                str,
                x, y,
                width, height,
                iconTexture,
                0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                color
        );

        if (needBoxUpdate) {
            if (super.boundingBox.isEmpty())
                super.boundingBox.setBoundingBox(tempBox.getX(), tempBox.getY(), tempBox.getWidth(), tempBox.getHeight(), tempBox.getColor());
            else
                super.boundingBox.mergeWith(tempBox);
        }
    }
}
