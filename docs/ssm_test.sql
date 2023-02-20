create database ssm_test;
use ssm_test;
create table book
(
    id          int auto_increment
        primary key,
    type        varchar(20)  null,
    name        varchar(50)  null,
    description varchar(100) null,
    constraint book_id_uindex
        unique (id)
)
    engine = InnoDB;

create table student
(
    id   int         not null
        primary key,
    name varchar(20) null,
    tid  int         null
)
    engine = InnoDB;

create table teacher
(
    id   int         not null
        primary key,
    name varchar(20) null
)
    engine = InnoDB;

create table user
(
    id   int         not null
        primary key,
    name varchar(20) null,
    pwd  varchar(20) null
)
    engine = InnoDB;


