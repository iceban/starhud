package fin.starhud.config;

import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import fin.starhud.hud.HUDId;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.*;

public class HUDSettings {

    @ConfigEntry.Gui.Excluded
    public List<HUDId> individualHudIds = new ArrayList<>();

    public List<GroupedHUDSettings> groupedHuds = new ArrayList<>();

    public HUDSettings(List<HUDId> individualHudIds, List<GroupedHUDSettings> groupedHuds) {
        this.individualHudIds = individualHudIds;
        this.groupedHuds = groupedHuds;
    }

    public HUDSettings() {
        groupedHuds.add(
                new GroupedHUDSettings(
                        new BaseHUDSettings(true, 5, 5, ScreenAlignmentX.LEFT, ScreenAlignmentY.TOP, GrowthDirectionX.RIGHT, GrowthDirectionY.DOWN),
                        1,
                        2,
                        false,
                        0xFFFFFF,
                        new ArrayList<>(List.of(HUDId.X_COORDINATE, HUDId.Y_COORDINATE, HUDId.Z_COORDINATE))
                )
        );

        groupedHuds.add(
                new GroupedHUDSettings(
                        new BaseHUDSettings(true, 0, 5, ScreenAlignmentX.CENTER, ScreenAlignmentY.TOP, GrowthDirectionX.CENTER, GrowthDirectionY.DOWN),
                        2,
                        2,
                        false,
                        0xFFFFFF,
                        new ArrayList<>(List.of(HUDId.BIOME, HUDId.CLOCK_INGAME))
                )
        );

        groupedHuds.add(
                new GroupedHUDSettings(
                        new BaseHUDSettings(true, 5, 0, ScreenAlignmentX.LEFT, ScreenAlignmentY.MIDDLE, GrowthDirectionX.RIGHT, GrowthDirectionY.MIDDLE),
                        3,
                        1,
                        true,
                        0xFFFFFF,
                        new ArrayList<>(List.of(HUDId.HELMET, HUDId.CHESTPLATE, HUDId.LEGGINGS, HUDId.BOOTS))
                )
        );

        groupedHuds.add(
                new GroupedHUDSettings(
                        new BaseHUDSettings(true, -5, -5, ScreenAlignmentX.RIGHT, ScreenAlignmentY.BOTTOM, GrowthDirectionX.LEFT, GrowthDirectionY.UP),
                        4,
                        2,
                        false,
                        0xFFFFFF,
                        new ArrayList<>(List.of(HUDId.PING, HUDId.CLOCK_SYSTEM))
                )
        );

        individualHudIds.addAll(List.of(HUDId.FPS, HUDId.DIRECTION, HUDId.DAY, HUDId.INVENTORY, HUDId.EFFECT, HUDId.TARGETED_CROSSHAIR));
    }


    public void onConfigSaved() {
        // check each HUD id appearance, if there's more than 1 appearance, we must clear.
        Map<HUDId, Integer> appearanceMap = new EnumMap<>(HUDId.class);

        for (HUDId id : individualHudIds) {
            appearanceMap.put(id, appearanceMap.getOrDefault(id, 0) + 1);
        }

        for (GroupedHUDSettings group : groupedHuds) {
            for (HUDId id : group.hudIds) {
                appearanceMap.put(id, appearanceMap.getOrDefault(id, 0) + 1);
            }
        }

        for (GroupedHUDSettings group : groupedHuds) {
            group.hudIds.removeIf(id -> {
                int count = appearanceMap.get(id);
                if (count > 1) {
                    appearanceMap.put(id, count - 1);
                    return true;
                }
                return false;
            });
        }

        Set<HUDId> seen = new HashSet<>();
        individualHudIds.removeIf(id -> {
            if (seen.contains(id)) return true;
            seen.add(id);
            return false;
        });

        for (HUDId id : HUDId.values()) {
            if (!appearanceMap.containsKey(id) || appearanceMap.get(id) == 0) {
                individualHudIds.add(id);
            }
        }

    }


}
