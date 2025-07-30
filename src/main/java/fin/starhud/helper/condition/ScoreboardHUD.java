package fin.starhud.helper.condition;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;

public class ScoreboardHUD {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static int x, y, width, height;

    public static boolean isShown() {
        return CLIENT.world.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) != null;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void captureBoundingBox(int x1, int y1, int x2, int y2) {
        x = x1;
        y = y1;

        width = x2 - x1;
        height = y2 - y1;
    }
}
