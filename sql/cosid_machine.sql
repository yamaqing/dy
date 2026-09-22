create table if not exists cosid_machine
(
    name            varchar(100)     not null comment '{namespace}.{machine_id}',
    namespace       varchar(100)     not null,
    machine_id      integer unsigned not null default 0,
    last_timestamp  bigint unsigned  not null default 0,
    instance_id     varchar(100)     not null default '',
    distribute_time bigint unsigned  not null default 0,
    revert_time     bigint unsigned  not null default 0,
    constraint cosid_machine_pk
    primary key (name)
    ) engine = InnoDB;

create index idx_namespace on cosid_machine (namespace);
create index idx_instance_id on cosid_machine (instance_id);
