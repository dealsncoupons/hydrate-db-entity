package org.hydrate.apps.repo.exception;

import java.sql.SQLException;

public class ConnectionProblem extends RuntimeException {
    public ConnectionProblem(String customMessage, SQLException e) {
        super(customMessage, e);
    }
}
