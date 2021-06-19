package org.hydrate.apps.support;

import org.hydrate.apps.entity.ITask;
import org.hydrate.apps.entity.Task;

import java.time.LocalDate;
import java.util.UUID;

public class EntityMetadata {

    public static EntityInfo taskInfo() {
        EntityInfo info = new EntityInfo("tbl_task", Task.class);
        info.addColumn(ColumnInfoBuilder.newBuilder().name("id").isPk(true).type(UUID.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("name").build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("completed").column("done").type(Boolean.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("dateCreated").column("date_created").type(LocalDate.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("nextTask").column("next_task").isRelational(true).type(ITask.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("dependsOn").column("parent_task").isRelational(true).type(ITask.class).build());
        info.addColumn(ColumnInfoBuilder.newBuilder().name("subTasks").column("parent_task").isRelational(true).isCollection(true).type(ITask.class).build());
        return info;
    }

    public static EntityInfo entityInfo(Class<?> type) {
        if (type.isAssignableFrom(Task.class)) {
            return taskInfo();
        }
        throw new RuntimeException("The type '" + type.getName() + "' has no mapping yet");
    }
}
