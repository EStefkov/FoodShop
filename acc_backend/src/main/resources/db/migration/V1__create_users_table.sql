CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       role VARCHAR(100) NOT NULL,
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
