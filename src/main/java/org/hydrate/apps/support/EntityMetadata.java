package org.hydrate.apps.support;

import org.hydrate.apps.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class EntityMetadata {

    public static EntityInfo taskInfo() {
        EntityInfo info = new EntityInfo("tbl_task", Task.class);
        info.addColumn(ColumnInfoBuilder.newBuilder().name("id").isPk(true).type(UUID.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("name").build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("status").isEmbedded(true).type(ITaskStatus.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("dateCreated").column("date_created").type(LocalDate.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("nextTask").column("next_task").isRelational(true).type(ITask.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("dependsOn").column("parent_task").isRelational(true).type(ITask.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("subTasks").column("parent_task").isRelational(true).isCollection(true).type(ITask.class).build());
        return info;
    }

    public static EntityInfo taskStatusInfo() {
        EntityInfo info = new EntityInfo(null, TaskStatus.class);
        info.addColumn(ColumnInfoBuilder.newBuilder().name("completed").column("done").type(Boolean.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("timeStarted").column("time_started").type(LocalDateTime.class).build());
        return info;
    }

    public static EntityInfo playerInfo() {
        EntityInfo info = new EntityInfo("tbl_player", Player.class);
        info.addColumn(ColumnInfoBuilder.newBuilder().name("id").isPk(true).type(UUID.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("nameAlias").column("name_alias").build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("dateJoined").column("date_joined").type(LocalDate.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("assignments").column("task_id").joinTable("tbl_task").isRelational(true).isCollection(true).type(ITask.class).build());
        return info;
    }

    public static EntityInfo assignmentInfo() {
        EntityInfo info = new EntityInfo("tbl_assignment", Assignment.class);
        info.addColumn(ColumnInfoBuilder.newBuilder().name("id").column("id").isCompoundPk(true).type(AssignmentId.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("dateAssigned").column("date_assigned").type(LocalDate.class).build());
        return info;
    }

    public static EntityInfo assignmentIdInfo() {
        EntityInfo info = new EntityInfo(null, AssignmentId.class);
        info.addColumn(ColumnInfoBuilder.newBuilder().name("taskId").column("task_id").isFk(true).isPk(true).type(UUID.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("playerId").column("player_id").isFk(true).isPk(true).type(UUID.class).build());
        return info;
    }

    public static EntityInfo entityInfo(Class<?> type) {
        if (type.isAssignableFrom(Task.class)) {
            return taskInfo();
        }
        if (type.isAssignableFrom(ITaskStatus.class)) {
            return taskStatusInfo();
        }
        throw new RuntimeException("The type '" + type.getName() + "' has no mapping yet");
    }
}
