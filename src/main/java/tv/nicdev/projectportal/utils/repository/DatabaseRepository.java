/*
 * Copyright (c) 2026 NicDevTV
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
package tv.nicdev.projectportal.utils.repository;

import java.sql.SQLException;

public interface DatabaseRepository {
    String name();

    void initializeSchema() throws SQLException;
}
