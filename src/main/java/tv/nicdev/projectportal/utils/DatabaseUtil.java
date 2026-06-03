/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import tv.nicdev.projectportal.infra.config.ConfigService;
import tv.nicdev.projectportal.utils.repository.DatabaseRepository;
import tv.nicdev.projectportal.utils.repository.GenericStorageRepository;

public final class DatabaseUtil {
    private static final String PRODUCTION_DATABASE_FILE = "data.db";
    private static final String EXPERIMENTAL_DATABASE_FILE = "experimental-data.db";
    private static final String STORAGE_FOLDER = "storage";

    private static DatabaseUtil instance;

    private final JavaPlugin plugin;
    private final ConfigService configService;
    private final List<DatabaseRepository> repositories = new ArrayList<>();
    private Connection connection;
    private GenericStorageRepository genericStorageRepository;

    public DatabaseUtil(JavaPlugin plugin, ConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
        instance = this;
    }

    public void connect() {
        File storageFolder = new File(plugin.getDataFolder(), STORAGE_FOLDER);
        if (!storageFolder.exists() && !storageFolder.mkdirs()) {
            throw new IllegalStateException("Could not create plugin storage folder: " + storageFolder);
        }

        File databaseFile = new File(storageFolder, databaseFileName());
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            configureConnection();
            genericStorageRepository = new GenericStorageRepository(this);
            registerRepository(genericStorageRepository);
            plugin.getLogger().info("Connected SQLite database: " + databaseFile.getName());
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not connect SQLite database: " + databaseFile, exception);
        }
    }

    public Connection connection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not initialized yet.");
        }
        return connection;
    }

    public GenericStorageRepository genericStorage() {
        if (genericStorageRepository == null) {
            throw new IllegalStateException("GenericStorageRepository is not initialized yet.");
        }
        return genericStorageRepository;
    }

    public void registerRepository(DatabaseRepository repository) {
        repositories.add(repository);
        if (connection != null) {
            initializeRepository(repository);
        }
    }

    public List<DatabaseRepository> repositories() {
        return Collections.unmodifiableList(repositories);
    }

    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException exception) {
            plugin.getLogger().warning("Could not close SQLite database connection: " + exception.getMessage());
        } finally {
            connection = null;
            genericStorageRepository = null;
            repositories.clear();
        }
    }

    public static DatabaseUtil get() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseUtil is not initialized yet.");
        }
        return instance;
    }

    private void configureConnection() throws SQLException {
        try (Statement statement = connection().createStatement()) {
            statement.executeUpdate("PRAGMA foreign_keys = ON");
            statement.executeUpdate("PRAGMA journal_mode = WAL");
            statement.executeUpdate("PRAGMA busy_timeout = 5000");
        }
    }

    private void initializeRepository(DatabaseRepository repository) {
        try {
            repository.initializeSchema();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not initialize database repository: " + repository.name(), exception);
        }
    }

    private String databaseFileName() {
        if (configService.isExperimentalBuild()) {
            return EXPERIMENTAL_DATABASE_FILE;
        }
        return PRODUCTION_DATABASE_FILE;
    }
}
