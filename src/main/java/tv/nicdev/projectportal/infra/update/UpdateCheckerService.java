/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.infra.update;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.plugin.java.JavaPlugin;
import tv.nicdev.projectportal.infra.config.ConfigService;

public final class UpdateCheckerService {
    private static final String UPDATE_MANIFEST_URL =
        "https://github.com/NicDevTV/nicdev-project-portal/releases/latest/download/update-manifest.json";
    private static final Pattern BUILD_ID_PATTERN = Pattern.compile("\"buildId\"\\s*:\\s*\"([^\"]+)\"");

    private final JavaPlugin plugin;

    public UpdateCheckerService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdateNotice() {
        if (ConfigService.get().isExperimentalBuild()) {
            return;
        }
        if (!plugin.getConfig().getBoolean("auto-updater", true)) {
            return;
        }

        String localBuildId = ConfigService.get().buildId();
        if (localBuildId.isBlank()) {
            plugin.getLogger().warning("Could not read local buildId; skipping update check.");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> checkRemote(UPDATE_MANIFEST_URL, localBuildId));
    }

    private void checkRemote(String manifestUrl, String localBuildId) {
        try {
            HttpRequest request =
                HttpRequest.newBuilder(URI.create(manifestUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                plugin.getLogger().warning("Update check failed with HTTP " + response.statusCode());
                return;
            }

            String body = response.body();
            String remoteBuildId = matchJsonValue(BUILD_ID_PATTERN, body);
            if (remoteBuildId.isBlank()) {
                plugin.getLogger().warning("Update manifest missing buildId.");
                return;
            }

            if (!remoteBuildId.equals(localBuildId)) {
                plugin
                    .getLogger()
                    .warning("A newer plugin build is available. Current buildId=" + localBuildId + ", latest buildId=" + remoteBuildId);
            }
        } catch (Exception exception) {
            plugin.getLogger().warning("Update check failed: " + exception.getMessage());
        }
    }

    private static String matchJsonValue(Pattern pattern, String json) {
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
}
