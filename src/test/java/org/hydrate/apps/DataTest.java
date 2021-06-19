package org.hydrate.apps;

import org.hydrate.apps.entity.Task;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class DataTest {

    @Test
    @Ignore("Not ready for testing")
    public void testMain() {
        System.out.println("Testing Data class");
        UUID id1 = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        Data.put(Task.TABLE_NAME, id1, "id", id1);
        Data.put(Task.TABLE_NAME, id1, "name", "Run a mile");
        Data.put(Task.TABLE_NAME, id1, "completed", false);
        Data.put(Task.TABLE_NAME, id1, "dateCreated", now);

        Task task = new Task();
        assertEquals(task.id().toString(), id1.toString());
        assertEquals(task.name(), "Run a mile");
        assertEquals(task.completed(), false);
        assertEquals(task.dateCreated(), now);
    }
}