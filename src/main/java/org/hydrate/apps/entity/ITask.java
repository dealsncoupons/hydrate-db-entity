package org.hydrate.apps.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

public interface ITask {

    UUID id();

    String name();

    ITaskStatus status();

    LocalDate dateCreated();

    ITask nextTask();

    ITask dependsOn();

    Collection<ITask> subTasks();
}