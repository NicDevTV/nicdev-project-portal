/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.heldencommunity.bootstrap;

import org.bukkit.ChatColor;
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
        printBanner();

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

    private void printBanner() {
        String[] banner = {
            " _   _ _      ____             _______     __",
            "| \\ | (_) ___|  _ \\  _____   _|_   _\\ \\   / /",
            "|  \\| | |/ __| | | |/ _ \\ \\ / / | |  \\ \\ / / ",
            "| |\\  | | (__| |_| |  __/\\ V /  | |   \\ V /  ",
            "|_| \\_|_|\\___|____/ \\___| \\_/   |_|    \\_/   "
        };

        for (String line : banner) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + line);
        }

        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "IMPORTANT: THIS PLUGIN IS MAINTAINED BY A VOLUNTEER");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "AND IS NOT OFFICIALLY CONNECTED TO THE OFFICIAL MINECRAFT HELDEN");
    }
}
