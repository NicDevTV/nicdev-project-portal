/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import tv.nicdev.projectportal.bootstrap.PluginBootstrap;
import tv.nicdev.projectportal.infra.config.ConfigService;

public final class NicDevProjectPortal extends JavaPlugin {
    private PluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        if (!ConfigService.get().isExperimentalBuild()) {
            // bStats
            Metrics metrics = new Metrics(this, 31737);
            // bStats
        }

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