package fin.starhud.config;

import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import fin.starhud.hud.HUDId;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.*;

public class HUDSettings {

    @ConfigEntry.Gui.Excluded
    public List<HUDId> individualHudIds = new ArrayList<>();

    @Comment("It's not recommended to modify grouped HUD directly on the configuration screen.")
    public List<GroupedHUDSettings> groupedHuds = new ArrayList<>();

    public HUDSettings(List<HUDId> individualHudIds, List<GroupedHUDSettings> groupedHuds) {
        this.individualHudIds = individualHudIds;
        this.groupedHuds = groupedHuds;
    }

    public HUDSettings() {
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

        individualHudIds.addAll(
                List.of(
                        HUDId.FPS,
                        HUDId.DIRECTION,
                        HUDId.DAY,
                        HUDId.INVENTORY,
                        HUDId.EFFECT,
                        HUDId.TARGETED_CROSSHAIR,
                        HUDId.PING,
                        HUDId.BIOME,
                        HUDId.CLOCK_SYSTEM, HUDId.CLOCK_INGAME,
                        HUDId.X_COORDINATE, HUDId.Y_COORDINATE, HUDId.Z_COORDINATE
                )
        );
    }


    public void onConfigSaved() {
        // count appearances of each HUD ID
        Map<HUDId, Integer> appearanceMap = new EnumMap<>(HUDId.class);

        for (HUDId id : individualHudIds) {
            appearanceMap.put(id, appearanceMap.getOrDefault(id, 0) + 1);
        }

        for (GroupedHUDSettings group : groupedHuds) {
            for (HUDId id : group.hudIds) {
                appearanceMap.put(id, appearanceMap.getOrDefault(id, 0) + 1);
            }
        }

        for (Map.Entry<HUDId, Integer> entry : appearanceMap.entrySet()) {
            HUDId id = entry.getKey();
            int count = entry.getValue();

            if (count == 1) continue;

            boolean inIndividual = individualHudIds.contains(id);
            boolean inGrouped = groupedHuds.stream()
                    .anyMatch(group -> group.hudIds.contains(id));

            // if there are 2 and in individual and in grouped, prioritize group.
            if (count == 2 && inIndividual && inGrouped) {
                individualHudIds.remove(id);
            } else {
                // if there are many duplicates, remove all.
                for (GroupedHUDSettings group : groupedHuds)
                    group.hudIds.removeIf(hudId -> hudId.equals(id));

                individualHudIds.removeIf(hudId -> hudId.equals(id));
            }
        }

        // this ensures that every hudIds that isn't found in groupedhud to be put in individual hud.
        Set<HUDId> allRepresentedIds = new HashSet<>(individualHudIds);
        for (GroupedHUDSettings group : groupedHuds) {
            allRepresentedIds.addAll(group.hudIds);
        }

        for (HUDId id : HUDId.values()) {
            if (!allRepresentedIds.contains(id)) {
                individualHudIds.add(id);
            }
        }
    }
}
