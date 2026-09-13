CREATE TABLE suporte(
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          titulo VARCHAR(200) NOT NULL,
                          descricao VARCHAR(1000) NOT NULL,
                          aluno_id UUID NOT NULL,
                          prioridade VARCHAR(50) DEFAULT 'LOW' NOT NULL,
                          status_chamado BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_suporte_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id)
);