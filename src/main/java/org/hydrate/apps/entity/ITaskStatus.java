package org.hydrate.apps.entity;

import java.time.LocalDateTime;

public interface ITaskStatus {

    Boolean completed();

    LocalDateTime timeStarted();
}