CREATE TABLE cargo (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO cargo (name) VALUES ('Professor'), ('Secretário'), ('Coordenador');