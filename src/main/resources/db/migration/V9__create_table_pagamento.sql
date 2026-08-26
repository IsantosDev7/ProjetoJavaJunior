CREATE TABLE pagamento (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           valor NUMERIC(15, 2) NOT NULL,
                           data TIMESTAMP WITH TIME ZONE NOT NULL
);