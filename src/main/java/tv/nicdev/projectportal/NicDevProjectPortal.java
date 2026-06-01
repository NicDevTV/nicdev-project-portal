/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import tv.nicdev.projectportal.bootstrap.PluginBootstrap;

public final class NicDevProjectPortal extends JavaPlugin {
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