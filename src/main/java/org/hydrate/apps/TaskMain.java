package org.hydrate.apps;

import org.hydrate.apps.cycle.Cycle;
import org.hydrate.apps.entity.Task;
import org.hydrate.apps.repo.GenericRepo;
import org.hydrate.apps.repo.exception.EntityRelationCycle;
import org.hydrate.apps.support.EntityMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.emptyList;

public class TaskMain {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskMain.class);

    public static void main(String[] args) {
        System.out.println("Apps up!");
        //create repo object
        GenericRepo repo = new GenericRepo();

        //create column values
        String task1Name = "Run a mile";
        Boolean task1Done = Boolean.FALSE;
        LocalDate task1Date = LocalDate.now();

        String task2Name = "Hit those push-ups";
        Boolean task2Done = Boolean.FALSE;
        LocalDate task2Date = LocalDate.now();

        String task3Name = "Quick shower";
        Boolean task3Done = Boolean.FALSE;
        LocalDate task3Date = LocalDate.now();

        //style 1 - apply next task
//        Task task1 = new Task(null, task1Name, task1Done, task1Date,
//                new Task(null, task2Name, task2Done, task2Date,
//                        new Task(null, task3Name, task3Done, task3Date, null, null), null), null);
//
//        //save task 1
//        Task saved1 = repo.save(task1);
//        System.out.printf("%s\n", saved1.id().toString());
//
//        //style 2 - apply dependsOn task
//        Task task21 = new Task(null, task1Name + "2", !task1Done, task1Date, null, null);
//        Task task22 = new Task(null, task2Name + "2", !task2Done, task2Date, null, task21);
//        Task task23 = new Task(null, task3Name + "2", !task3Done, task3Date, null, task22);
//
//        //save task 2
//        Task saved23 = repo.save(task23);
//        System.out.printf("%s\n", saved23.id().toString());

        //style 3 - apply both next and dependsOn task, and handle stack-overflow condition
        Task task31 = new Task(null, task1Name + "3", true, task1Date, null, null, emptyList());
        Task task32 = new Task(null, task2Name + "3", true, task2Date, null, task31, emptyList());
        Task task33 = new Task(null, task3Name + "3", true, task3Date, null, task32, emptyList());

        task31.set("nextTask", task32);
        task32.set("nextTask", task33);

        //save task 3
        try {
            new Cycle<Task>().checkCycle(task31);
            Task saved31 = repo.save(task31);
            System.out.printf("%s\n", saved31.id().toString());
        } catch (EntityRelationCycle e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        //fixing scenario with cyclic relation
        Task task41 = new Task(null, task1Name + "4", true, task1Date,
                new Task(null, task2Name + "4", true, task2Date,
                        new Task(null, task3Name + "4", true, task3Date, null, null, emptyList()), null, emptyList()), null, emptyList());

        Task saved41 = repo.save(task41);
        System.out.printf("inserted %s\n", saved41.id().toString());

        //set field to update
        Task saved42 = (Task) saved41.nextTask();
        saved42.set("dependsOn", task41);

        //save the update
        Task saved42u = repo.update(saved42);
        System.out.printf("updated %s\n", saved42u.id().toString());

        //set field to update
        Task saved43 = (Task) saved42.nextTask();
        saved43.set("dependsOn", saved42);

        //save the update
        Task saved43u = repo.update(saved43);
        System.out.printf("updated %s\n", saved43u.id().toString());

        //delete entity
        try {
            Task deleted1 = repo.delete(saved43);
            System.out.printf("deleted %s\n", deleted1.name());
        } catch (RuntimeException e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        UUID task41Id = saved41.id();
        Map<String, Object> params = new HashMap<>();
        params.put("id", task41Id);
        Task selected41 = repo.select(EntityMetadata.entityInfo(Task.class), params);
        System.out.println(selected41.name());
    }
}
