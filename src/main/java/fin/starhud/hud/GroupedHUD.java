package fin.starhud.hud;

import fin.starhud.Main;
import fin.starhud.config.GroupedHUDSettings;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupedHUD extends AbstractHUD {

    private static final Logger LOGGER = Main.LOGGER;

    public GroupedHUDSettings groupSettings;
    public final List<AbstractHUD> huds = new ArrayList<>();

    public GroupedHUD(GroupedHUDSettings groupSettings) {
        super(groupSettings.base);
        this.groupSettings = groupSettings;

        List<String> invalidIds = new ArrayList<>();

        for (String id : groupSettings.hudIds) {
            AbstractHUD hud = HUDComponent.getInstance().getHUD(id);
            if (hud == null) {
                invalidIds.add(id);
            } else {
                huds.add(hud);
                hud.setGroupId(this.groupSettings.id);
            }
        }
        groupSettings.hudIds.removeAll(invalidIds);
    }

    @Override
    public String getName() {
        if (huds.isEmpty())
            return "Groupped HUD";

        StringBuilder name = new StringBuilder();

        name.append("(");

        for (AbstractHUD hud : huds)
            name.append(hud.getName()).append(',');

        name.deleteCharAt(name.length() - 1).append(')');

        return name.toString();
    }

    private int width;
    private int height;

    private final Map<String, Boolean> shouldRenders = new HashMap<>();

    @Override
    public boolean collectHUDInformation() {
        width = 0;
        height = 0;

        int renderedCount = 0;

        for (AbstractHUD hud : huds) {
            if (!hud.shouldRender() || !hud.collectHUDInformation()) {
                shouldRenders.put(hud.getId(), false);
                continue;
            }

            renderedCount++;

            if (groupSettings.alignVertical) {
                height += hud.getHeight();
                width = Math.max(width, hud.getWidth());
            } else {
                width += hud.getWidth();
                height = Math.max(height, hud.getHeight());
            }

            shouldRenders.put(hud.getId(), true);
        }

        if (renderedCount > 0) {
            if (groupSettings.alignVertical) {
                height += groupSettings.gap * (renderedCount - 1);
            } else {
                width += groupSettings.gap * (renderedCount - 1);
            }
        } else
            return false;

        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height);

        return true;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {
        int w = getWidth();
        int h = getHeight();

        int drawX = x;
        int drawY = y;

        for (AbstractHUD hud : huds) {
            if (!shouldRenders.get(hud.getId()))
                continue;

            if (groupSettings.alignVertical) {
                drawX = x + getGrowthDirectionHorizontal(w - hud.getWidth());
            } else {
                drawY = y + getGrowthDirectionVertical(h - hud.getHeight());
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
    public String getId() {
        return groupSettings.id;
    }

    @Override
    public void update() {
        super.update();

        for (AbstractHUD hud : huds){
            if (!hud.isInGroup()) {
                LOGGER.warn("{} IS NOT IN A GROUP! FORCING TO CHANGE THEM.", hud.getName());
                hud.setGroupId(groupSettings.id);
            }
        }

        getBoundingBox().setColor(groupSettings.boxColor | 0xFF000000);
    }

    public void updateActiveHUDsFromConfig() {

        for (AbstractHUD hud : huds)
            hud.setGroupId(null);
        huds.clear();

        for (String id : groupSettings.hudIds) {
            AbstractHUD hud = HUDComponent.getInstance().getHUD(id);

            huds.add(hud);
            hud.setGroupId(groupSettings.id);
//            LOGGER.info("UpdateActiveHUDsFromConfig (added to group: {}) : {}", groupSettings.id, hud.getName());
        }
    }
}
