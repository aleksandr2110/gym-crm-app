INSERT INTO users (first_name, last_name, username, password, is_active) VALUES
    ('Anna', 'Lee', 'Anna.Lee', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true),
    ('Juli', 'Huston', 'Juli.Huston', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', false),
    ('Marina', 'Merkylova', 'Marina.Merkylova', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true),
    ('Martin', 'Brown', 'Martin.Brown', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true),
    ('Bob', 'Wilson', 'Bob.Wilson', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', false),
    ('David', 'Davis', 'David.Davis', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true),
    ('Sergey', 'Kaplyn', 'Sergey.Kaplyn', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true);


INSERT INTO trainees (id, date_birth, address)
SELECT u.id, '1987-05-17', '15 Red St' FROM users u WHERE u.username = 'Martin.Brown';

INSERT INTO trainees (id, date_birth, address)
SELECT u.id, '1988-09-24', '46 Blu Ave' FROM users u WHERE u.username = 'Bob.Wilson';

INSERT INTO trainees (id, date_birth, address)
SELECT u.id, '1989-05-15', '9 Red Ave' FROM users u WHERE u.username = 'David.Davis';


INSERT INTO trainers (id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Anna.Lee' AND tt.training_type_name = 'JAVA';

INSERT INTO trainers (id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Juli.Huston' AND tt.training_type_name = 'JAVASCRIPT';

INSERT INTO trainers (id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Marina.Merkylova' AND tt.training_type_name = 'PYTHON';

INSERT INTO trainers (id, specialization_id)
SELECT u.id, tt.id FROM users u, training_types tt
WHERE u.username = 'Sergey.Kaplyn' AND tt.training_type_name = 'ANGULAR';


-----trainee roles
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'Martin.Brown' AND r.name = 'ROLE_TRAINEE';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'Bob.Wilson' AND r.name = 'ROLE_TRAINEE';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'David.Davis' AND r.name = 'ROLE_TRAINEE';

-----trainer roles
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'Anna.Lee' AND r.name = 'ROLE_TRAINER';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'Juli.Huston' AND r.name = 'ROLE_TRAINER';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'Marina.Merkylova' AND r.name = 'ROLE_TRAINER';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'Sergey.Kaplyn' AND r.name = 'ROLE_TRAINER';



INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.id = tu.id AND tu.username = 'Anna.Lee'
  AND tr.id = uu.id AND uu.username = 'Martin.Brown';

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.id = tu.id AND tu.username = 'Juli.Huston'
  AND tr.id = uu.id AND uu.username = 'Bob.Wilson';

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.id = tu.id AND tu.username = 'Marina.Merkylova'
  AND tr.id = uu.id AND uu.username = 'Martin.Brown';

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT t.id, tr.id FROM trainers t, trainees tr, users tu, users uu
WHERE t.id = tu.id AND tu.username = 'Sergey.Kaplyn'
  AND tr.id = uu.id AND uu.username = 'David.Davis';



 INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
 SELECT tr.id, t.id, 'Learning Java', tt.id, '2026-09-15', 60
 FROM trainees tr, trainers t, training_types tt, users uu, users tu
 WHERE tr.id = uu.id AND uu.username = 'Martin.Brown'
   AND t.id = tu.id AND tu.username = 'Anna.Lee'
   AND tt.training_type_name = 'Java';

 INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
 SELECT tr.id, t.id, 'Learning JavaScript', tt.id, '2026-09-16', 90
 FROM trainees tr, trainers t, training_types tt, users uu, users tu
 WHERE tr.id = uu.id AND uu.username = 'Bob.Wilson'
   AND t.id = tu.id AND tu.username = 'David.Davis'
   AND tt.training_type_name = 'JavaScript';

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Learning Python', tt.id, '2026-10-10', 45
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.id = uu.id AND uu.username = 'Martin.Brown'
  AND t.id = tu.id AND tu.username = 'Marina.Merkylova'
  AND tt.training_type_name = 'Python';

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Learning Angular', tt.id, '2026-10-18', 75
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.id = uu.id AND uu.username = 'David.Davis'
  AND t.id = tu.id AND tu.username = 'Sergey.Kaplyn'
  AND tt.training_type_name = 'Angular';

INSERT INTO trainings (trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
SELECT tr.id, t.id, 'Learning Java', tt.id, '2026-10-19', 50
FROM trainees tr, trainers t, training_types tt, users uu, users tu
WHERE tr.id = uu.id AND uu.username = 'David.Davis'
  AND t.id = tu.id AND tu.username = 'Anna.Lee'
  AND tt.training_type_name = 'Java';