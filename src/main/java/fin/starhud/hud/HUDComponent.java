package fin.starhud.hud;

import fin.starhud.Main;
import fin.starhud.config.GroupedHUDSettings;
import fin.starhud.config.HUDSettings;
import fin.starhud.hud.implementation.*;
import net.minecraft.client.gui.DrawContext;

import java.util.*;

public class HUDComponent {

    // singleton
    private static HUDComponent instance;

    // Registered HUDs by ID
    private final Map<HUDId, AbstractHUD> hudMap = new EnumMap<>(HUDId.class);

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

    private void loadActiveHUDsFromConfig() {
        HUDSettings hudConfig = Main.settings.hudList;

        for (HUDId id : hudConfig.individualHudIds) {
            individualHUDs.add(hudMap.get(id));
        }

        for (GroupedHUDSettings groupedHud : hudConfig.groupedHuds) {
            groupedHUDs.add(new GroupedHUD(groupedHud));
        }
    }

    private void registerHUD(AbstractHUD hud) {
        hudMap.put(hud.getId(), hud);
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
        for (HUDInterface hud : individualHUDs)
            hud.update();

        for (HUDInterface hud : groupedHUDs)
            hud.update();
    }

    public void setRenderInGameScreen(boolean value) {
        this.renderInGameScreen = value;
    }

    public boolean isRenderInGameScreen() {
        return this.renderInGameScreen;
    }
}
