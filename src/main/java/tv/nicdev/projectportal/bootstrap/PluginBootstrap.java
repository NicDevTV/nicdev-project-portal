/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.bootstrap;

import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tv.nicdev.projectportal.infra.config.ConfigService;
import tv.nicdev.projectportal.infra.platform.Paper120Capabilities;
import tv.nicdev.projectportal.infra.platform.PlatformCapabilities;

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
            plugin.getServer().getConsoleSender().sendMessage(Component.text(line, NamedTextColor.BLUE));
        }

        plugin.getServer()
            .getConsoleSender()
            .sendMessage(Component.text("IMPORTANT: THIS PLUGIN IS MAINTAINED BY A VOLUNTEER", NamedTextColor.RED));
        plugin.getServer()
            .getConsoleSender()
            .sendMessage(Component.text("AND IS NOT OFFICIALLY CONNECTED TO ANY OF THE OFFICIAL CREATORS, TEAMS, OR BRANDS.", NamedTextColor.RED));
        plugin.getServer()
            .getConsoleSender()
            .sendMessage(Component.text("WANNA HELP IMPROVE THE PLUGIN? TAKE A LOOK AT THE GITHUB REPOSITORY:", NamedTextColor.YELLOW));
        plugin.getServer()
            .getConsoleSender()
            .sendMessage(Component.text("https://github.com/NicDevTV/nicdev-project-portal", NamedTextColor.YELLOW));
    }
}
