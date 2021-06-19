package org.hydrate.apps.repo.exception;

import java.sql.SQLException;

public class RollbackFailed extends RuntimeException {
    public RollbackFailed(String customMessage, SQLException ex) {
        super(customMessage, ex);
    }
}
