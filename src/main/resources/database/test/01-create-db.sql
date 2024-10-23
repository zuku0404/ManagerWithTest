--liquibase formatted sql
--changeset zuku:1

CREATE TABLE users (
    id bigint not null primary key auto_increment,
    name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE tasks (
    id bigint not null primary key auto_increment,
    title VARCHAR(255),
    description TEXT,
    task_status VARCHAR(20),
    deadline DATE
);

CREATE TABLE user_task (
    user_id BIGINT,
    task_id BIGINT,
    PRIMARY KEY (user_id, task_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);