--liquibase formatted sql
--changeset zuku:1

CREATE TABLE users (
    id bigint not null primary key auto_increment,
    first_name VARCHAR(100) not null,
    last_name VARCHAR(100) not null,
    email VARCHAR(100) unique not null,
    password VARCHAR(100) not null,
    role VARCHAR(10) not null
);

CREATE TABLE tasks (
    id bigint not null primary key auto_increment,
    title VARCHAR(255) unique not null,
    description TEXT not null,
    task_status VARCHAR(20) not null,
    deadline DATE not null
);

CREATE TABLE user_task (
    user_id BIGINT,
    task_id BIGINT,
    PRIMARY KEY (user_id, task_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);