/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.utils.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Optional;
import tv.nicdev.projectportal.utils.DatabaseUtil;

public final class GenericStorageRepository implements DatabaseRepository {
    private final DatabaseUtil databaseUtil;

    public GenericStorageRepository(DatabaseUtil databaseUtil) {
        this.databaseUtil = databaseUtil;
    }

    @Override
    public String name() {
        return "generic_storage";
    }

    @Override
    public void initializeSchema() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS generic_storage_entries (
                storage_type TEXT NOT NULL,
                entry_key TEXT NOT NULL,
                entry_value TEXT,
                updated_at INTEGER NOT NULL,
                PRIMARY KEY (storage_type, entry_key)
            )
            """;
        try (Statement statement = databaseUtil.connection().createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public void set(StorageType type, String key, String value) {
        String sql = """
            INSERT INTO generic_storage_entries (storage_type, entry_key, entry_value, updated_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(storage_type, entry_key) DO UPDATE SET
                entry_value = excluded.entry_value,
                updated_at = excluded.updated_at
            """;
        try (PreparedStatement statement = databaseUtil.connection().prepareStatement(sql)) {
            statement.setString(1, type.databaseKey());
            statement.setString(2, key);
            statement.setString(3, value);
            statement.setLong(4, Instant.now().toEpochMilli());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save generic storage entry: " + type + "/" + key, exception);
        }
    }

    public Optional<String> get(StorageType type, String key) {
        String sql = "SELECT entry_value FROM generic_storage_entries WHERE storage_type = ? AND entry_key = ?";
        try (PreparedStatement statement = databaseUtil.connection().prepareStatement(sql)) {
            statement.setString(1, type.databaseKey());
            statement.setString(2, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.ofNullable(resultSet.getString("entry_value"));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not read generic storage entry: " + type + "/" + key, exception);
        }
    }

    public boolean exists(StorageType type, String key) {
        String sql = "SELECT 1 FROM generic_storage_entries WHERE storage_type = ? AND entry_key = ? LIMIT 1";
        try (PreparedStatement statement = databaseUtil.connection().prepareStatement(sql)) {
            statement.setString(1, type.databaseKey());
            statement.setString(2, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check generic storage entry: " + type + "/" + key, exception);
        }
    }

    public void delete(StorageType type, String key) {
        String sql = "DELETE FROM generic_storage_entries WHERE storage_type = ? AND entry_key = ?";
        try (PreparedStatement statement = databaseUtil.connection().prepareStatement(sql)) {
            statement.setString(1, type.databaseKey());
            statement.setString(2, key);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not delete generic storage entry: " + type + "/" + key, exception);
        }
    }

    public enum StorageType {
        GENERIC("generic");

        private final String databaseKey;

        StorageType(String databaseKey) {
            this.databaseKey = databaseKey;
        }

        public String databaseKey() {
            return databaseKey;
        }
    }
}
