package fin.starhud.hud;

import fin.starhud.config.GroupedHUDSettings;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class GroupedHUD extends AbstractHUD {

    public final GroupedHUDSettings groupSettings;
    public final List<AbstractHUD> huds = new ArrayList<>();

    public int lastWidth = 0;
    public int lastHeight = 0;

    public GroupedHUD(GroupedHUDSettings groupSettings) {
        super(groupSettings.base);
        this.groupSettings = groupSettings;

        for (HUDId id : groupSettings.ids) {
            AbstractHUD hud = HUDComponent.getInstance().getHUD(id);
            huds.add(hud);
            hud.setInGroup(true);
        }
    }

    @Override
    public String getName() {
        return "Grouped HUD";
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        boolean isRendered = false;

        x -= getSettings().getGrowthDirectionHorizontal(lastWidth);
        y -= getSettings().getGrowthDirectionVertical(lastHeight);

        setBoundingBox(x, y, lastWidth, lastHeight);

        int width = 0;
        int height = 0;

        for (AbstractHUD hud : huds) {
            if (hud.shouldRender()) {
                hud.renderHUD(context, x, y);

                if (groupSettings.alignVertical) {
                    y += hud.getHeight() + groupSettings.gap;
                    height += hud.getHeight() + groupSettings.gap;
                    if (hud.getWidth() > width)
                        width = hud.getWidth();
                } else {
                    x += hud.getWidth() + groupSettings.gap;
                    width += hud.getWidth() + groupSettings.gap;
                    if (hud.getHeight() > height)
                        height = hud.getHeight();
                }

                isRendered = true;
            }
        }

        lastWidth = width - (groupSettings.alignVertical ? 0 : groupSettings.gap);
        lastHeight = height- (groupSettings.alignVertical ? groupSettings.gap : 0);
        return isRendered;
    }


    @Override
    public HUDId getId() {
        return null;
    }

    @Override
    public void update() {
        super.update();

        getBoundingBox().setColor(groupSettings.boxColor | 0xFF000000);
    }

    public void onUngroup() {
        for (AbstractHUD hud : huds)
            hud.setInGroup(false);
    }
}
