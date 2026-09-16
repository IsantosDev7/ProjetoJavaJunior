CREATE TABLE solicitacao(

                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            titulo VARCHAR(200) NOT NULL,
                            descricao VARCHAR(1000) NOT NULL,
                            funcionario_id UUID NOT NULL,
                            prioridade VARCHAR(50) DEFAULT 'LOW' NOT NULL,
                            status_solicitacao BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_solicitacao_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
)