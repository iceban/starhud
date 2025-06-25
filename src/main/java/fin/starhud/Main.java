package fin.starhud;

import fin.starhud.config.Settings;
import fin.starhud.hud.HUDComponent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class Main implements ClientModInitializer {

    public static Settings settings;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(Settings.class, GsonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(Settings.class).getConfig();
        AutoConfig.getConfigHolder(Settings.class).registerSaveListener(this::onConfigSaved);
    }

    private ActionResult onConfigSaved(ConfigHolder<Settings> holder, Settings config) {
        HUDComponent.updateAll();
        return ActionResult.SUCCESS;
    }
}
