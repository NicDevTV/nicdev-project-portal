/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.heldencommunity.bootstrap;

import org.bukkit.plugin.java.JavaPlugin;
import tv.nicdev.heldencommunity.infra.config.ConfigService;
import tv.nicdev.heldencommunity.infra.platform.Paper120Capabilities;
import tv.nicdev.heldencommunity.infra.platform.PlatformCapabilities;

public final class PluginBootstrap {
    private final JavaPlugin plugin;
    private ConfigService configService;
    private PlatformCapabilities platformCapabilities;

    public PluginBootstrap(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        configService = new ConfigService(plugin);
        configService.load();

        platformCapabilities = new Paper120Capabilities();

        plugin.getLogger().info("Enabled " + plugin.getName() + " on " + platformCapabilities.platformVersionLabel());
    }

    public void disable() {
        plugin.getLogger().info("Disabled " + plugin.getName());
    }

    public ConfigService configService() {
        return configService;
    }

    public PlatformCapabilities platformCapabilities() {
        return platformCapabilities;
    }
}
