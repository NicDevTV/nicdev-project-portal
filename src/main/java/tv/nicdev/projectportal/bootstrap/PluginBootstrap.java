/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.bootstrap;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tv.nicdev.projectportal.command.ProjectPortalCommand;
import tv.nicdev.projectportal.infra.config.ConfigService;
import tv.nicdev.projectportal.infra.platform.Paper120Capabilities;
import tv.nicdev.projectportal.infra.platform.PlatformCapabilities;
import tv.nicdev.projectportal.infra.update.UpdateCheckerService;

public final class PluginBootstrap {
    private final JavaPlugin plugin;
    private ConfigService configService;
    private PlatformCapabilities platformCapabilities;
    private UpdateCheckerService updateCheckerService;

    public PluginBootstrap(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        configService = new ConfigService(plugin);
        configService.load();
        updateCheckerService = new UpdateCheckerService(plugin);

        platformCapabilities = new Paper120Capabilities();
        registerCommands();
        printBanner();
        updateCheckerService.checkForUpdateNotice();

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
        boolean experimentalBuild = configService.isExperimentalBuild();
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
        if (experimentalBuild) {
            String[] experimentalBanner = {
                " _____                      _                      _        _ ",
                "| ____|_  ___ __   ___ _ __(_)_ __ ___   ___ _ __ | |_ __ _| |",
                "|  _| \\ \\/ / '_ \\ / _ \\ '__| | '_ ` _ \\ / _ \\ '_ \\| __/ _` | |",
                "| |___ >  <| |_) |  __/ |  | | | | | | |  __/ | | | || (_| | |",
                "|_____/_/\\_\\ .__/ \\___|_|  |_|_| |_| |_|\\___|_| |_|\\__\\__,_|_|",
                "           |_|                                                "
            };
            for (String line : experimentalBanner) {
                plugin.getServer().getConsoleSender().sendMessage(Component.text(line, NamedTextColor.LIGHT_PURPLE));
            }
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
        if (experimentalBuild) {
            plugin.getServer()
                .getConsoleSender()
                .sendMessage(Component.text("EXPERIMENTAL BUILDS ARE DISCONNECTED FROM OFFICIAL BUILDS,", NamedTextColor.LIGHT_PURPLE));
            plugin.getServer()
                .getConsoleSender()
                .sendMessage(Component.text("INCLUDING DATABASES, AUTO-UPDATER, AND OTHER CORE SYSTEMS.", NamedTextColor.LIGHT_PURPLE));
        }
    }

    private void registerCommands() {
        PluginCommand command = plugin.getCommand("project-portal");
        if (command == null) {
            plugin.getLogger().warning("Could not register /project-portal command; plugin.yml entry is missing.");
            return;
        }

        ProjectPortalCommand projectPortalCommand = new ProjectPortalCommand(plugin, configService, updateCheckerService);
        command.setExecutor(projectPortalCommand);
        command.setTabCompleter(projectPortalCommand);
    }
}
