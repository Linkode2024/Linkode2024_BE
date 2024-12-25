INSERT INTO avatar (created_at, modified_at, avatar_img, status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 'img1.png', 'ACTIVE');
INSERT INTO avatar (created_at, modified_at, avatar_img, status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 'img2.png', 'ACTIVE');

INSERT INTO color (created_at, modified_at, hex_code, status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', '헥스코드1', 'ACTIVE');
INSERT INTO color (created_at, modified_at, hex_code, status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', '헥스코드2', 'ACTIVE');
INSERT INTO color (created_at, modified_at, hex_code, status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', '헥스코드3', 'ACTIVE');

INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 1, 'jsilver01','두바이쩡', 1, 'ACTIVE');
INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 2, 'Mouon','두바이', 2, 'ACTIVE');
INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 1, 'Mouon2','두바이클론', 3, 'ACTIVE');
INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 1, 'Mouon4','두바이클론2', 1, 'ACTIVE');
INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 1, 'Mouon5','두바이클론3', 1, 'ACTIVE');
INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 1, 'Mouon6','두바이클론4', 1, 'ACTIVE');
INSERT INTO member (created_at, modified_at, avatar_id, github_id, nickname, color_id,status) VALUES
('2024-06-30 12:00:00.000000', '2024-06-30 12:30:00.000000', 1, 'Mouon7','두바이클론5', 1, 'ACTIVE');

INSERT INTO studyroom (created_at, modified_at, studyroom_name, studyroom_profile, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', '맴버 1의 스터디룸', 'Profile for New Studyroom', 'ACTIVE');
INSERT INTO studyroom (created_at, modified_at, studyroom_name, studyroom_profile, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', '맴버 2의 스터디룸', 'Profile for New Studyroom', 'ACTIVE');
INSERT INTO studyroom (created_at, modified_at, studyroom_name, studyroom_profile, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', '맴버 3의 스터디룸', 'Profile for New Studyroom', 'ACTIVE');
INSERT INTO studyroom (created_at, modified_at, studyroom_name, studyroom_profile, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', '맴버 1의 스터디룸', 'Profile for New Studyroom', 'ACTIVE');


INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 1, 1, 'CAPTAIN', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 2, 1, 'CAPTAIN', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 3, 1, 'CAPTAIN', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 4, 1, 'CAPTAIN', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 5, 1, 'CAPTAIN', 'ACTIVE');

INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 1, 2, 'CREW', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 3, 2, 'CREW', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 4, 2, 'CAPTAIN', 'ACTIVE');
INSERT INTO member_studyroom (created_at, modified_at, member_id, studyroom_id, role, status) VALUES
('2024-07-04 12:00:00.000000', '2024-07-04 12:00:00.000000', 5, 2, 'CAPTAIN', 'ACTIVE');