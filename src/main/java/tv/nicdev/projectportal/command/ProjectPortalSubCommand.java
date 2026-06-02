/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.command;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface ProjectPortalSubCommand {
    String name();

    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
