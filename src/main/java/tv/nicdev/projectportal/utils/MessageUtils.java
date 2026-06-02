/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class MessageUtils {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private MessageUtils() {
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(message));
    }

    public static String escape(String value) {
        return MINI_MESSAGE.escapeTags(value);
    }
}
