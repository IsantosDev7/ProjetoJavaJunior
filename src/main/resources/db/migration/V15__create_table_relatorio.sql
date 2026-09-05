CREATE TABLE relatorio(
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         aula_id UUID NOT NULL UNIQUE,
                         professor_id UUID NOT NULL,
                         texto VARCHAR(3000) NOT NULL,
                         status BOOLEAN NOT NULL DEFAULT FALSE,

                         CONSTRAINT fk_relatorio_aula FOREIGN KEY (aula_id) REFERENCES aula(id),
                         CONSTRAINT fk_relatorio_professor FOREIGN KEY (professor_id) REFERENCES funcionario(id)
);