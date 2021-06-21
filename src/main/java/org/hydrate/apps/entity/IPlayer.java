package org.hydrate.apps.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

public interface IPlayer {

    UUID id();

    String nameAlias();

    LocalDate dateJoined();

    Collection<IAssignment> assignments();

}
