create database mybatis_plus;
use mybatis_plus;
create table user
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    name        varchar(30)   null comment '姓名',
    age         int           null comment '年龄',
    email       varchar(50)   null comment '邮箱',
    deleted     int default 0 null comment '逻辑删除',
    update_time datetime      null comment '更新时间',
    version     int default 1 null,
    create_time datetime      null comment '修改时间'
);


