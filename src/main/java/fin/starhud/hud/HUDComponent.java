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
    private final Map<String, GroupedHUD> groupedHUDMap = new HashMap<>();

    // Active HUDs (selected in config)
    private final List<AbstractHUD> individualHUDs = new ArrayList<>();
    private final List<GroupedHUD> groupedHUDs = new ArrayList<>();

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

        AutoConfig.getConfigHolder(Settings.class).save();
    }

    public Map<HUDId, AbstractHUD> getHudMap() {
        return hudMap;
    }

    public List<AbstractHUD> getIndividualHUDs() {
        return individualHUDs;
    }

    public List<GroupedHUD> getGroupedHUDs() {
        return groupedHUDs;
    }

    public void loadActiveHUDsFromConfig() {
        HUDSettings hudConfig = Main.settings.hudList;

        // remove hud that is in a group from individual huds
        // this is done by checking if said hud id is no longer present in the individual hud ids.
        individualHUDs.removeIf(hud -> {
            boolean result = !hudConfig.individualHudIds.contains(hud.getId());
            if (result) {
//                LOGGER.info("Removed {} from Individual HUD", hud.getName());
            }
            return result;
        });

        // if there are any individual hud that exist in the config but not here, add them
        for (HUDId id : hudConfig.individualHudIds) {
            AbstractHUD hud = hudMap.get(id);
            if (individualHUDs.stream().noneMatch(existingHud -> existingHud.getId().equals(id))) {
//                LOGGER.info("Added {} to Individual HUD", hud.getName());
                hud.setInGroup(null);
                individualHUDs.add(hud);
            }
        }

        // remove group that is no longer present in the config.
        groupedHUDs.removeIf(group -> {
            boolean missing = hudConfig.groupedHuds.stream().noneMatch(settings -> settings.id.equals(group.groupSettings.id));
            if (missing) {
                groupedHUDMap.remove(group.groupSettings.id);
//                LOGGER.info("Removed Group ({}) from Groupped HUDs", group.getName());
            }
            return missing;
        });

        // if there are any group that exist in the config but not here, add them
        for (GroupedHUDSettings settings : hudConfig.groupedHuds) {

            // this is usually not needed since when creating the object we also construct the id.
            if (settings.id == null || settings.id.isEmpty()) {
                settings.id = generateNextGroupId();
            }

            GroupedHUD existing = groupedHUDMap.get(settings.id);

            // if the said group is already exist, we need to update them.
            if (existing != null) {
//                LOGGER.info("Group ({}) already exist, updating the settings...", existing.getName());
                existing.groupSettings.copyFrom(settings);
                existing.updateActiveHUDsFromConfig();
            } else { // otherwise create a new one
//                LOGGER.info("{} have not yet exist, creating new Groupped HUD...", settings.id);
                GroupedHUD newGroup = new GroupedHUD(settings);
                groupedHUDs.add(newGroup);
                groupedHUDMap.put(settings.id, newGroup);
            }
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
        for (HUDInterface hud : individualHUDs) {
            if (hud.shouldRender()) {
                hud.render(context);
            }
        }
    }

    private void renderGroupedHUDs(DrawContext context) {
        for (GroupedHUD group : groupedHUDs) {
            if (group.shouldRender())
                group.render(context);
        }
    }

    public void updateAll() {
        for (HUDInterface hud : hudMap.values()) {
            hud.update();
        }

        for (HUDInterface hud : groupedHUDs) {
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
        } while (groupedHUDMap.containsKey(id));
        return id;
    }

    public void setRenderInGameScreen(boolean value) {
        this.renderInGameScreen = value;
    }

    public boolean isRenderInGameScreen() {
        return this.renderInGameScreen;
    }
}
