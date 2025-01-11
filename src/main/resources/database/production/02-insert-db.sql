--liquibase formatted sql
--changeset zuku:1

INSERT INTO users (first_name, last_name, email, role, password)
VALUES ('admin', 'adminek', 'admin@wp.pl', 'ROLE_ADMIN', '$2a$10$bfDWSvV..kZkFM6agRDf.euXE2C2YW9VIVzIDnJOA88BpV6aDK3h6'),
       ('user', 'user', 'user1@wp.pl', 'ROLE_USER', '$2a$10$Me/FzRMJgVM.OqRxUgblNuFn3/fLDA/kJPr5KedUQ6WbWUyZ8gaLi'),
       ('user', 'use', 'user2@wp.pl', 'ROLE_USER','$2a$10$hPS1jPFI.ESv3/DydiE3Yub1gGTSHGtbcbFCviPpZIfIFFbJnuKUe'),
       ('user', 'useree', 'user3@wp.pl', 'ROLE_USER','$2a$10$UT2a58NgaIMR9Er0pn9lIOUYi1OMy5YGua2sbSAotwKyYTc.sn6XC'),
       ('userek', 'usereusz', 'user4@wp.pl', 'ROLE_USER','$2a$10$MFKUuROyXvH8KBg05LSJMuL8GS8PUaLemqahYTK3rTZ9EQT8.c1gS'),
       ('userusz', 'user', 'user5@wp.pl', 'ROLE_USER', '$2a$10$iLuY6jwqMDVpSdtiifn1AePc0OTEuvwwF1JfnK/zzJgQAZ6QRDQtG');

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
VALUES (2, 1),
       (6, 1),
       (4, 1),
       (3, 1),
       (2, 2),
       (5, 3),
       (6, 3),
       (2, 3),
       (3, 3),
       (4, 3),
       (2, 4),
       (5, 4),
       (6, 4),
       (3, 4),
       (4, 4),
       (4, 5),
       (3, 5),
       (4, 6),
       (6, 6),
       (2, 6),
       (5, 7),
       (4, 8),
       (5, 8),
       (2, 8),
       (3, 9),
       (4, 9),
       (2, 9),
       (6, 10),
       (6, 11),
       (2, 11),
       (5, 11),
       (3, 11),
       (4, 11);
