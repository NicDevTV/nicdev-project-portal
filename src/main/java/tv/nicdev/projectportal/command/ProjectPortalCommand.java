/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tv.nicdev.projectportal.command.subcommand.InfoSubCommand;
import tv.nicdev.projectportal.infra.config.ConfigService;
import tv.nicdev.projectportal.infra.update.UpdateCheckerService;
import tv.nicdev.projectportal.utils.MessageUtils;

public final class ProjectPortalCommand implements CommandExecutor, TabCompleter {
    private final Map<String, ProjectPortalSubCommand> subCommands = new LinkedHashMap<>();

    public ProjectPortalCommand(
        JavaPlugin plugin,
        ConfigService configService,
        UpdateCheckerService updateCheckerService
    ) {
        register(new InfoSubCommand(plugin, configService, updateCheckerService));
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (args.length == 0) {
            return executeSubCommand(sender, "info", new String[0]);
        }

        String subCommandName = args[0].toLowerCase(Locale.ROOT);
        String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);
        if (executeSubCommand(sender, subCommandName, subCommandArgs)) {
            return true;
        }

        MessageUtils.send(
            sender,
            "<red>Unknown subcommand.</red> <gray>Try</gray> <yellow>/" + MessageUtils.escape(label) + " info</yellow><gray>.</gray>"
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        @NotNull String[] args
    ) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return subCommands
                .keySet()
                .stream()
                .filter(name -> name.startsWith(prefix))
                .toList();
        }

        ProjectPortalSubCommand subCommand = subCommands.get(args[0].toLowerCase(Locale.ROOT));
        if (subCommand == null) {
            return List.of();
        }

        return new ArrayList<>(subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length)));
    }

    private void register(ProjectPortalSubCommand subCommand) {
        subCommands.put(subCommand.name(), subCommand);
    }

    private boolean executeSubCommand(CommandSender sender, String name, String[] args) {
        return Optional
            .ofNullable(subCommands.get(name))
            .map(subCommand -> {
                subCommand.execute(sender, args);
                return true;
            })
            .orElse(false);
    }
}
