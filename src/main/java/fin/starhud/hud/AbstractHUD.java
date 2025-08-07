package fin.starhud.hud;

import fin.starhud.config.BaseHUDSettings;
import fin.starhud.config.ConditionalSettings;
import fin.starhud.helper.Box;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class AbstractHUD implements HUDInterface {

    protected final BaseHUDSettings baseHUDSettings;

    protected int x;
    protected int y;

    private int baseX;
    private int baseY;

    protected final Box boundingBox = new Box(-1, -1, -1, -1);

    public String groupId = null;

    public AbstractHUD(BaseHUDSettings baseHUDSettings) {
        this.baseHUDSettings = baseHUDSettings;
    }

    @Override
    public boolean shouldRender() {
        return getSettings().shouldRender();
    }

    // we update every HUD's x and y points here.
    @Override
    public void update() {
        updateX();
        updateY();
    }

    @Override
    public boolean render(DrawContext context) {

        // modify our X and Y points based on conditions.
        modifyXY();

        if (!collectHUDInformation())
            return false;

        if (!isScaled())
            return renderHUD(context, x, y, shouldDrawBackground());

        // this is so we can change the scale for one hud but not the others.
        context.getMatrices().pushMatrix();
        setHUDScale(context);

        try {
            return renderHUD(context, x, y, shouldDrawBackground());
        } finally {
            context.getMatrices().popMatrix();
        }
    }

    // collect what is needed for the hud to render.
    // the true purpose of collectData is to collect the width and height during data collection,
    // this is to ensure that the width and height can be used before the rendering
    // returns false if the HUD cannot be rendered
    // returns true if the HUD is ready to be rendered.
    public abstract boolean collectHUDInformation();

    // this is where the hud is rendered. Where we put the rendering logic.
    // it is highly discouraged to put information collecting in this function.
    // for information collecting please refer to collectHUDInformation()
    public abstract boolean renderHUD(DrawContext context, int x, int y, boolean drawBackground);

    public abstract String getName();

    public void setHUDScale(DrawContext context) {
        float scaleFactor = getSettings().getScale() / (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
        context.getMatrices().scale(scaleFactor, scaleFactor);
    }

    public void modifyXY() {
        int tempX = 0, tempY = 0;

        for (ConditionalSettings condition : baseHUDSettings.getConditions()) {
            if (condition.isConditionMet()) {
                float scaleFactor = getSettings().getScaledFactor();
                tempX += condition.getXOffset(scaleFactor);
                tempY += condition.getYOffset(scaleFactor);
            }
        }

        x = baseX + tempX;
        y = baseY + tempY;
    }

    public void updateX() {
        baseX = getSettings().getCalculatedPosX();
    }

    public void updateY() {
        baseY = getSettings().getCalculatedPosY();
    }

    public boolean isScaled() {
        return this.getSettings().getScale() != 0 && (this.getSettings().getScale() != MinecraftClient.getInstance().getWindow().getScaleFactor());
    }

    public boolean isInGroup() {
        return groupId != null;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public BaseHUDSettings getSettings() {
        return baseHUDSettings;
    }

    public int getGrowthDirectionHorizontal(int dynamicWidth) {
        return getSettings().getGrowthDirectionX().getGrowthDirection(dynamicWidth);
    }

    public int getGrowthDirectionVertical(int dynamicHeight) {
        return getSettings().getGrowthDirectionY().getGrowthDirection(dynamicHeight);
    }

    public boolean shouldDrawBackground() {
        return getSettings().drawBackground;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // bounding box attribute will return 0 if HUD is not rendered once.
    // the HUD must be rendered at least once to update the bounding box.

    public void setWidth(int width) {
        getBoundingBox().setWidth(width);
    }

    public void setHeight(int height) {
        getBoundingBox().setHeight(height);
    }

    public int getRawX() {
        return getBoundingBox().getX();
    }

    public int getRawY() {
        return getBoundingBox().getY();
    }

    public int getWidth() {
        return getBoundingBox().getWidth();
    }

    public int getHeight() {
        return getBoundingBox().getHeight();
    }

    public Box getBoundingBox() {
        return boundingBox;
    }

    public void copyBoundingBox(Box boundingBox) {
        if (boundingBox != null)
            this.boundingBox.copyFrom(boundingBox);
    }

    public void setBoundingBox(int x, int y, int width, int height, int color) {
        this.boundingBox.setBoundingBox(x, y, width, height, color);
    }

    public void setBoundingBox(int x, int y, int width, int height) {
        this.boundingBox.setBoundingBox(x, y, width, height);
    }
}
