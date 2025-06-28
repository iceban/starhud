package fin.starhud.hud;

import fin.starhud.hud.implementation.*;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;

public class HUDComponent {

    public static final ArrayList<HUDInterface> huds = new ArrayList<>();

    // separate status effect hud as they are rendered in a different place.
    public static final HUDInterface effectHUD = new Effect();

    static {
        huds.add(new Armor());
        huds.add(new Biome());
        huds.add(new ClockInGame());
        huds.add(new ClockSystem());
        huds.add(new Day());
        huds.add(new Coordinate());
        huds.add(new Direction());
        huds.add(new FPS());
        huds.add(new Inventory());
        huds.add(new Ping());
        huds.add(new LeftHand());
        huds.add(new RightHand());
    }

    public static void renderAll(DrawContext context) {
        for (HUDInterface hud : huds) {
            if (hud.shouldRender())
                hud.render(context);
        }
    }

    public static void updateAll() {
        for (HUDInterface hud : huds) {
            hud.update();
        }

        effectHUD.update();
    }
}
