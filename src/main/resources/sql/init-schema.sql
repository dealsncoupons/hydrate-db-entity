drop table if exists tbl_task;

create table if not exists tbl_task
(
    id           uuid      default random_uuid(),
    name         varchar(32) not null,
    done         boolean   default false,
    date_created timestamp default current_date,
    next_task    uuid,
    parent_task  uuid,
    constraint task_pk primary key (id),
    constraint uniq_name unique (name),
    constraint next_task_fk foreign key (next_task) REFERENCES tbl_task (id),
    constraint parent_task_fk foreign key (parent_task) REFERENCES tbl_task (id)
);