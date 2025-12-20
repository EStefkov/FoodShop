CREATE TABLE  users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       number VARCHAR(50),
                       address VARCHAR(255),
                       city VARCHAR(100),
                       country VARCHAR(100),
                       postal_code VARCHAR(20),
                       profile_picture VARCHAR(255),
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (

                       id BIGSERIAL PRIMARY KEY,
                       role VARCHAR(100)

);

-- Добавяме уникален constraint върху role
ALTER TABLE roles ADD CONSTRAINT unique_role UNIQUE(role);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,

                             PRIMARY KEY (user_id, role_id),
                             CONSTRAINT fk_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES users(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_role
                                 FOREIGN KEY (role_id)
                                     REFERENCES roles(id)
                                     ON DELETE CASCADE
);