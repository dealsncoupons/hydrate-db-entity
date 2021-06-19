package org.hydrate.apps.repo.exception;

import java.sql.SQLException;

public class RecordKeyNotGenerated extends SQLException {
    public RecordKeyNotGenerated(String customMessage) {
        super(customMessage);
    }
}
