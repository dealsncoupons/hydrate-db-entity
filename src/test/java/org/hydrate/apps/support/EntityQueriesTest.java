package org.hydrate.apps.support;

import org.junit.Test;

public class EntityQueriesTest {

    @Test
    public void generateSaveQuery() {
        String saveTaskQuery = EntityQueries.generateSaveQuery(EntityMetadata.taskInfo());
        System.out.println(saveTaskQuery);
    }

    @Test
    public void generateUpdateQuery() {
        String updateTaskQuery = EntityQueries.generateUpdateQuery(EntityMetadata.taskInfo());
        System.out.println(updateTaskQuery);
    }

    @Test
    public void generateDeleteQuery() {
        String deleteTaskQuery = EntityQueries.generateDeleteQuery(EntityMetadata.taskInfo());
        System.out.println(deleteTaskQuery);
    }

    @Test
    public void generateSelectQuery() {
        String selectTaskQuery = EntityQueries.generateSelectOneQuery(EntityMetadata.taskInfo());
        System.out.println(selectTaskQuery);
    }
}