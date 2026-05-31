package tv.nicdev.heldencommunity;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import tv.nicdev.heldencommunity.bootstrap.PluginBootstrap;

public final class HeldenCommunityPlugin extends JavaPlugin {
    private PluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        // bStats
        int pluginId = 31726;
        Metrics metrics = new Metrics(this, pluginId);
        // bStats

        bootstrap = new PluginBootstrap(this);
        bootstrap.enable();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.disable();
        }
    }
}