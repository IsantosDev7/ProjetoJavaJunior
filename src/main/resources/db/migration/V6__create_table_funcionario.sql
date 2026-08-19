CREATE TABLE funcionario (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             usuario_id UUID NOT NULL UNIQUE REFERENCES app_user(id),
                             name VARCHAR(255) NOT NULL,
                             birthdate DATE NOT NULL,
                             cpf VARCHAR(11) UNIQUE,
                             phone VARCHAR(50) NOT NULL,
                             address VARCHAR(255) NOT NULL,
                             city VARCHAR(50) NOT NULL,
                             state VARCHAR(50) NOT NULL,
                             cep VARCHAR(8) NOT NULL,
                             country VARCHAR(50) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE funcionario_cargo (
                                   funcionario_id UUID NOT NULL REFERENCES funcionario(id),
                                   cargo_id UUID NOT NULL REFERENCES cargo(id),
                                   PRIMARY KEY (funcionario_id, cargo_id)
);