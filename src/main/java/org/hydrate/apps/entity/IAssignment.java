package org.hydrate.apps.entity;

import java.time.LocalDate;
import java.util.UUID;

public interface IAssignment {

    AssignmentId id();

    LocalDate dateAssigned();
}
