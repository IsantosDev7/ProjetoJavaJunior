CREATE TABLE aluno (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       responsavel_id UUID NULL REFERENCES responsavel(id),
                       name VARCHAR(255) NOT NULL,
                       cpf VARCHAR(11) UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       phone VARCHAR(50) NOT NULL,
                       birthdate DATE NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       cep VARCHAR(8) NOT NULL,
                       city VARCHAR(50) NOT NULL,
                       state VARCHAR(50) NOT NULL,
                       country VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);