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
    private static final String LATEST_RELEASE_URL = "https://github.com/NicDevTV/nicdev-project-portal/releases/latest";
    private static final Pattern BUILD_ID_PATTERN = Pattern.compile("\"buildId\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile("\"downloadUrl\"\\s*:\\s*\"([^\"]+)\"");
    private static final HttpClient HTTP_CLIENT =
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.NORMAL).build();

    private final JavaPlugin plugin;
    private volatile String statusLabel = "⏳ pending";

    public UpdateCheckerService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdateNotice() {
        if (ConfigService.get().isExperimentalBuild()) {
            statusLabel = "⏭ experimental";
            return;
        }
        if (!plugin.getConfig().getBoolean("auto-updater", true)) {
            statusLabel = "⛔ off";
            return;
        }

        String localBuildId = ConfigService.get().buildId();
        if (localBuildId.isBlank()) {
            statusLabel = "⚠ LOCAL_BUILD_ID";
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> checkRemote(UPDATE_MANIFEST_URL, localBuildId));
    }

    public String statusLabel() {
        return statusLabel;
    }

    private void checkRemote(String manifestUrl, String localBuildId) {
        try {
            HttpRequest request =
                HttpRequest.newBuilder(URI.create(manifestUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                statusLabel = "⚠ HTTP_" + response.statusCode();
                return;
            }

            String body = response.body();
            String remoteBuildId = matchJsonValue(BUILD_ID_PATTERN, body);
            if (remoteBuildId.isBlank()) {
                statusLabel = "⚠ MANIFEST_BUILD_ID";
                return;
            }

            if (!remoteBuildId.equals(localBuildId)) {
                String downloadUrl = matchJsonValue(DOWNLOAD_URL_PATTERN, body);
                if (downloadUrl.isBlank()) {
                    downloadUrl = LATEST_RELEASE_URL;
                }

                plugin
                    .getLogger()
                    .warning("A newer plugin version is available. Download the latest JAR here: " + downloadUrl);
                statusLabel = "🆕 update";
                return;
            }

            statusLabel = "✅ ok";
        } catch (Exception exception) {
            statusLabel = "⚠ CHECK_FAILED";
            // Just ignore update-check failures in production
        }
    }

    private static String matchJsonValue(Pattern pattern, String json) {
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
}
