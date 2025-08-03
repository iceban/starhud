package fin.starhud.hud;

import fin.starhud.Main;
import fin.starhud.config.GroupedHUDSettings;
import fin.starhud.config.HUDList;
import fin.starhud.hud.implementation.*;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUDComponent {

    // singleton
    private static HUDComponent instance;

    private static final Logger LOGGER = Main.LOGGER;

    // Registered HUDs by ID
    private final Map<String, AbstractHUD> hudMap = new HashMap<>();

    // Active HUDs (selected in config)
    private final Map<String, AbstractHUD> individualHUDs = new HashMap<>();
    private final Map<String, GroupedHUD> groupedHUDs = new HashMap<>();

    private boolean renderInGameScreen = true;

    private HUDComponent() {}

    public static HUDComponent getInstance() {
        if (instance == null) {
            instance = new HUDComponent();
        }
        return instance;
    }

    public void init() {
        registerBuiltInHUDs();
        loadActiveHUDsFromConfig();
    }

    private void registerBuiltInHUDs() {
        registerHUD(new HelmetHUD());
        registerHUD(new ChestplateHUD());
        registerHUD(new LeggingsHUD());
        registerHUD(new BootsHUD());

        registerHUD(new XCoordinateHUD());
        registerHUD(new YCoordinateHUD());
        registerHUD(new ZCoordinateHUD());

        registerHUD(new LeftHandHUD());
        registerHUD(new RightHandHUD());

        registerHUD(new ClockInGameHUD());
        registerHUD(new ClockSystemHUD());

        registerHUD(new BiomeHUD());
        registerHUD(new DayHUD());
        registerHUD(new DirectionHUD());
        registerHUD(new FPSHUD());
        registerHUD(new InventoryHUD());
        registerHUD(new PingHUD());
        registerHUD(new TargetedCrosshairHUD());

        registerHUD(new PositiveEffectHUD());
        registerHUD(new NegativeEffectHUD());
    }

    public Map<String, AbstractHUD> getHudMap() {
        return hudMap;
    }

    public Map<String, AbstractHUD> getIndividualHUDs() {
        return individualHUDs;
    }

    public Map<String, GroupedHUD> getGroupedHUDs() {
        return groupedHUDs;
    }

    public void loadActiveHUDsFromConfig() {
        HUDList hudConfig = Main.settings.hudList;

        individualHUDs.clear();

        for (String id : hudConfig.individualHudIds) {
            AbstractHUD hud = hudMap.get(id);
            hud.setGroupId(null);
            individualHUDs.put(id, hud);
        }

        groupedHUDs.clear();

        for (GroupedHUDSettings settings : hudConfig.groupedHuds) {
            groupedHUDs.put(settings.id, new GroupedHUD(settings));
        }
    }


    private void registerHUD(AbstractHUD hud) {
        hudMap.put(hud.getId(), hud);
        LOGGER.info("{} Added to Hud Map.", hud.getId());
    }

    public AbstractHUD getHUD(String id) {
        AbstractHUD hud = hudMap.get(id);
        if (hud != null)
            return hud;

        hud = groupedHUDs.get(id);
        if (hud != null)
            return hud;

        LOGGER.warn("No such group with id: {} existed in the map, creating new one if available in config", id);

        List<GroupedHUDSettings> groupSettings = Main.settings.hudList.groupedHuds;
        for (GroupedHUDSettings setting : groupSettings) {
            System.out.println(setting.id);
            if (id.equals(setting.id)) {
                hud = new GroupedHUD(setting);
                groupedHUDs.put(id, (GroupedHUD) hud);
                return hud;
            }
        }

        LOGGER.error("Group with ID: {} does not exist. returning null.", id);

        return null;
    }

    public AbstractHUD getHUD(HUDId id) {
        return hudMap.get(id.toString());
    }

    public void renderAll(DrawContext context) {
        renderIndividualHUDs(context);
        renderGroupedHUDs(context);
    }

    private void renderIndividualHUDs(DrawContext context) {
        for (HUDInterface hud : individualHUDs.values()) {
            if (hud.shouldRender()) {
                hud.render(context);
            }
        }
    }

    private void renderGroupedHUDs(DrawContext context) {
        for (GroupedHUD group : groupedHUDs.values()) {
            if (!group.isInGroup() && group.shouldRender())
                group.render(context);
        }
    }

    public void updateAll() {
        for (HUDInterface hud : hudMap.values()) {
            hud.update();
        }

        for (HUDInterface hud : groupedHUDs.values()) {
            hud.update();
        }
    }

    // follow up with the updated config.
    public void updateActiveHUDs() {
        loadActiveHUDsFromConfig();
        updateAll();
    }

    public void removeActiveHUDs() {
        individualHUDs.clear();
        groupedHUDs.clear();
    }

    public String generateNextGroupId() {
        int index = 1;
        String id;
        do {
            id = "group_" + index++;
        } while (groupedHUDs.containsKey(id));
        return id;
    }

    public void setRenderInGameScreen(boolean value) {
        this.renderInGameScreen = value;
    }

    public boolean isRenderInGameScreen() {
        return this.renderInGameScreen;
    }
}
