/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import tv.nicdev.projectportal.command.ProjectPortalSubCommand;
import tv.nicdev.projectportal.infra.config.ConfigService;
import tv.nicdev.projectportal.infra.update.UpdateCheckerService;
import tv.nicdev.projectportal.utils.MessageUtils;

public final class InfoSubCommand implements ProjectPortalSubCommand {
    private final JavaPlugin plugin;
    private final ConfigService configService;
    private final UpdateCheckerService updateCheckerService;

    public InfoSubCommand(
        JavaPlugin plugin,
        ConfigService configService,
        UpdateCheckerService updateCheckerService
    ) {
        this.plugin = plugin;
        this.configService = configService;
        this.updateCheckerService = updateCheckerService;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String buildId = configService.buildId().isBlank() ? "corrupted build" : configService.buildId();
        String buildType = configService.isExperimentalBuild()
            ? "<gradient:#ff4fd8:#8b5cf6>Experimental</gradient>"
            : "<gradient:#14d0ff:#3d53c2>Stable</gradient>";

        MessageUtils.send(sender,  "<bold><blue>" + MessageUtils.escape(plugin.getName()) + "</blue></bold>");
        MessageUtils.send(
            sender,
            "<gray>Version:</gray> <white>" + MessageUtils.escape(plugin.getPluginMeta().getVersion()) + "</white>"
        );
        MessageUtils.send(
            sender,
            "Build: " + MessageUtils.escape(buildId) + " (" + buildType + ")"
        );
        MessageUtils.send(
            sender,
            "<gray>Platform:</gray> <white>" + MessageUtils.escape(plugin.getServer().getVersion()) + "</white>"
        );
        MessageUtils.send(sender, "<gray>Updater:</gray> <white>" + MessageUtils.escape(updateCheckerService.statusLabel()) + "</white>");
    }
}
