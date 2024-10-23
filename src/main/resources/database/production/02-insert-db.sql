--liquibase formatted sql
--changeset zuku:1

INSERT INTO users (name, last_name, email)
VALUES ('name_1', 'lastName_1', 'name.lastName1@example.com'),
       ('name_2', 'lastName_2', 'name.lastName2@example.com'),
       ('name_3', 'lastName_3', 'name.lastName3@example.com'),
       ('name_4', 'lastName_4', 'name.lastName4@example.com'),
       ('name_5', 'lastName_5', 'name.lastName5@example.com'),
       ('name_6', 'lastName_6', 'name.lastName6@example.com'),
       ('name_7', 'lastName_7', 'name.lastName7@example.com'),
       ('name_8', 'lastName_8', 'name.lastName8@example.com'),
       ('name_9', 'lastName_9', 'name.lastName9@example.com'),
       ('name_10', 'lastName_10', 'name.lastName10@example.com'),
       ('name_11', 'lastName_11', 'name.lastName11@example.com'),
       ('name_12', 'lastName_12', 'name.lastName12@example.com'),
       ('name_12', 'lastName_11', 'name.lastName13@example.com'),
       ('name_12', 'lastName_12', 'name.lastName14@example.com'),
       ('name_10', 'lastName_12', 'name.lastName15@example.com');

INSERT INTO tasks (title, description, task_status, deadline)
VALUES ('title_1', 'description_1', 'TO_DO', '2024-11-01'),
       ('title_2', 'description_2', 'IN_PROGRESS', '2024-10-25'),
       ('title_3', 'description_3', 'DONE', '2024-10-20'),
       ('title_4', 'description_4', 'TO_DO', '2024-12-15'),
       ('title_5', 'description_5', 'TO_DO', '2024-10-22'),
       ('title_6', 'description_6', 'TO_DO', '2024-11-10'),
       ('title_7', 'description_7', 'IN_PROGRESS', '2024-12-01'),
       ('title_8', 'description_8', 'TO_DO', '2024-11-05'),
       ('title_9', 'description_9', 'DONE', '2024-10-18'),
       ('title_10', 'description_10', 'TO_DO', '2024-11-25'),
       ('title_11', 'description_11', 'IN_PROGRESS', '2024-12-20'),
       ('title_12', 'description_12', 'TO_DO', '2024-11-30');

INSERT INTO user_task (user_id, task_id)
VALUES (1, 1),
       (2, 2),
       (1, 3),
       (3, 3),
       (4, 4),
       (5, 5),
       (1, 5),
       (6, 6),
       (7, 7),
       (2, 8),
       (8, 9),
       (3, 9),
       (3, 8),
       (1, 9),
       (7, 5),
       (5, 2),
       (6, 4);
