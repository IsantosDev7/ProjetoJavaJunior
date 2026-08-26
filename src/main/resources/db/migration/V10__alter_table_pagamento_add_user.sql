ALTER TABLE pagamento ADD COLUMN usuario_id UUID;

ALTER TABLE pagamento ADD CONSTRAINT fk_pagamento_usuario FOREIGN KEY (usuario_id) REFERENCES app_user(id);