CREATE TABLE aula (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      professor_id UUID NOT NULL,
                      aluno_id UUID NOT NULL,
                      duracao_aula INTEGER NOT NULL,
                      titulo VARCHAR(255) NOT NULL,
                      data_hora_aula TIMESTAMP WITH TIME ZONE NOT NULL,
                      modalidade VARCHAR(100) NOT NULL,
                      status_aula VARCHAR(100),

                      CONSTRAINT fk_aula_professor FOREIGN KEY (professor_id) REFERENCES funcionario(id),
                      CONSTRAINT fk_aula_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id)
);