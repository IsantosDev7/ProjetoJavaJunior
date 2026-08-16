CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE responsavel (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             name VARCHAR(255) NOT NULL,
                             cpf VARCHAR(11) NOT NULL UNIQUE,
                             email VARCHAR(255) NOT NULL,
                             phone VARCHAR(50) NOT NULL,
                             birthdate DATE NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);