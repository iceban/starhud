package fin.starhud.hud;

import fin.starhud.config.GroupedHUDSettings;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;


public class GroupedHUD extends AbstractHUD {

    public final GroupedHUDSettings groupSettings;
    public final List<AbstractHUD> huds = new ArrayList<>();

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
        StringBuilder name = new StringBuilder();

        for (AbstractHUD hud : huds)
            name.append(hud).append(' ');

        name.deleteCharAt(name.length() - 1);

        return name.toString();
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        int width = 0;
        int height = 0;

        int renderedCount = 0;

        for (AbstractHUD hud : huds) {
            if (!hud.shouldRender()) continue;
            renderedCount++;

            if (hud.getBoundingBox().isEmpty()) continue;

            if (groupSettings.alignVertical) {
                height += hud.getHeight();
                width = Math.max(width, hud.getWidth());
            } else {
                width += hud.getWidth();
                height = Math.max(height, hud.getHeight());
            }

        }

        if (renderedCount > 0) {
            if (groupSettings.alignVertical) {
                height += groupSettings.gap * (renderedCount - 1);
            } else {
                width += groupSettings.gap * (renderedCount - 1);
            }
        } else
            return false;

        x -= getSettings().getGrowthDirectionHorizontal(width);
        y -= getSettings().getGrowthDirectionVertical(height);

        int drawX = x;
        int drawY = y;

        setBoundingBox(x, y, width, height);

        for (AbstractHUD hud : huds) {
            if (!hud.shouldRender()) continue;

            if (groupSettings.alignVertical) {
                drawX = x + getGrowthDirectionHorizontal(width - hud.getWidth());
            } else {
                drawY = y + getGrowthDirectionVertical(height - hud.getHeight());
            }

            if (!hud.renderHUD(context, drawX, drawY))
                continue;

            if (groupSettings.alignVertical) {
                drawY += hud.getHeight() + groupSettings.gap;
            } else {
                drawX += hud.getWidth() + groupSettings.gap;
            }
        }

        return true;
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

    public void updateActiveHUDsFromConfig() {
        huds.removeIf(hud -> {
            boolean result = !groupSettings.ids.contains(hud.getId());
            if (result) hud.setInGroup(false);
            return result;
        });

        for (HUDId id : groupSettings.ids) {
            AbstractHUD hud = HUDComponent.getInstance().getHUD(id);
            if (!huds.contains(hud)) {
                huds.add(hud);
                hud.setInGroup(true);
            }
        }
    }

    public void onUngroup() {
        for (AbstractHUD hud : huds)
            hud.setInGroup(false);
    }
}
