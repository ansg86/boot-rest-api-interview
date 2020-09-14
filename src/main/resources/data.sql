DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS ROLES;
DROP TABLE IF EXISTS BOOKS;

CREATE TABLE USERS (
  user_id VARCHAR(25) PRIMARY KEY,
  name VARCHAR(250),
  password VARCHAR(250),
  user_role_id BIGINT
);

CREATE TABLE ROLES (
  id BIGINT AUTO_INCREMENT  PRIMARY KEY,
  label VARCHAR(25)
);

CREATE TABLE BOOKS (
  id BIGINT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250),
  status VARCHAR(25)
);

INSERT INTO ROLES (id, label) VALUES
  (1, 'MEMBER'),
  (2, 'LIBRARIAN');


INSERT INTO USERS (user_id, name, password, user_role_id ) VALUES
('member', 'john', '$2a$10$dF3qaRkmChI5fNedLRAeUeGC6Cnz1lGrg/ctjdwBEAvLIb1QZhRKy', 1),  
('librarian', 'nancy', '$2a$10$dF3qaRkmChI5fNedLRAeUeGC6Cnz1lGrg/ctjdwBEAvLIb1QZhRKy', 2),
('nonmember', 'jack', '$2a$10$dF3qaRkmChI5fNedLRAeUeGC6Cnz1lGrg/ctjdwBEAvLIb1QZhRKy', null);


INSERT INTO BOOKS (id, name, status ) VALUES
(1, 'story-one', 'AVAILABLE'),  
(3, 'story-two', 'AVAILABLE');