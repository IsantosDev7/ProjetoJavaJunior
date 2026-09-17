ALTER TABLE solicitacao DROP COLUMN titulo;
ALTER TABLE solicitacao DROP COLUMN descricao;
ALTER TABLE solicitacao DROP COLUMN prioridade;

ALTER TABLE solicitacao ADD COLUMN tipo VARCHAR(50) NOT NULL;
ALTER TABLE solicitacao ADD COLUMN funcionario_alvo_id UUID NOT NULL;
ALTER TABLE solicitacao ADD COLUMN motivo VARCHAR(100) NOT NULL;

ALTER TABLE solicitacao RENAME COLUMN status_solicitacao TO status;
ALTER TABLE solicitacao ALTER COLUMN status DROP DEFAULT;
ALTER TABLE solicitacao ALTER COLUMN status TYPE VARCHAR(20)
    USING (CASE WHEN status::boolean = TRUE THEN 'APROVADA' ELSE 'PENDENTE' END);
ALTER TABLE solicitacao ALTER COLUMN status SET DEFAULT 'PENDENTE';