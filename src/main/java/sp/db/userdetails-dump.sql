-- dump data 

use userdetails;

DELETE FROM users;
INSERT INTO users VALUES ('admin', 'admin', true, 3),
        ('user', 'user', true, 2),
        ('anonymous', 'anonymous', true, 1);

DELETE FROM authorities;
INSERT INTO authorities VALUES ('admin', 'ROLE_ADMIN'),
        ('admin', 'ROLE_USER'),
        ('user', 'ROLE_USER'),
        ('anonymous', 'ROLE_ANONYMOUS');
