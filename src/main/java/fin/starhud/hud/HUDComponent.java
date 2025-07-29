package fin.starhud.hud;

import fin.starhud.Main;
import fin.starhud.config.GroupedHUDSettings;
import fin.starhud.config.HUDSettings;
import fin.starhud.config.Settings;
import fin.starhud.hud.implementation.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;

import java.util.*;

public class HUDComponent {

    // singleton
    private static HUDComponent instance;

    private static final Logger LOGGER = Main.LOGGER;

    // Registered HUDs by ID
    private final Map<HUDId, AbstractHUD> hudMap = new EnumMap<>(HUDId.class);

    // Active HUDs (selected in config)
    private final Map<HUDId, AbstractHUD> individualHUDs = new EnumMap<>(HUDId.class);
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
        registerHUD(new EffectHUD());
    }

    public Map<HUDId, AbstractHUD> getHudMap() {
        return hudMap;
    }

    public Map<HUDId, AbstractHUD> getIndividualHUDs() {
        return individualHUDs;
    }

    public Map<String, GroupedHUD> getGroupedHUDs() {
        return groupedHUDs;
    }

    public void loadActiveHUDsFromConfig() {
        HUDSettings hudConfig = Main.settings.hudList;

        individualHUDs.clear();

        for (HUDId id : hudConfig.individualHudIds) {
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

    public AbstractHUD getHUD(HUDId id) {
        return hudMap.get(id);
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
            if (group.shouldRender())
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
