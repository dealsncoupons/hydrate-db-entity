drop table if exists tbl_assignment;
drop table if exists tbl_player;
drop table if exists tbl_task;

create table if not exists tbl_task
(
    id           uuid      default random_uuid(),
    name         varchar(32) not null,
    done         boolean   default false,
    time_started timestamp,
    date_created timestamp default current_date,
    next_task    uuid,
    parent_task  uuid,
    constraint task_pk primary key (id),
    constraint uniq_name unique (name),
    constraint next_task_fk foreign key (next_task) REFERENCES tbl_task (id),
    constraint parent_task_fk foreign key (parent_task) REFERENCES tbl_task (id)
);

create table if not exists tbl_player
(
    id          uuid      default random_uuid(),
    name_alias  varchar(32) not null,
    date_joined timestamp default current_date,
    constraint player_pk primary key (id),
    constraint uniq_player unique (name_alias)
);

create table if not exists tbl_assignment
(
    player_id     uuid      default random_uuid(),
    task_id       varchar(32) not null,
    date_assigned timestamp default current_date,
    constraint assignment_id primary key (player_id, task_id),
    constraint player_assignment_fk foreign key (player_id) REFERENCES tbl_player (id),
    constraint task_assignment_fk foreign key (task_id) REFERENCES tbl_task (id)
);