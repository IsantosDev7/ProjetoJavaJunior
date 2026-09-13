CREATE TABLE token_convite (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               token VARCHAR(255) NOT NULL UNIQUE,
                               user_id UUID NOT NULL UNIQUE REFERENCES app_user(id),
                               created_at TIMESTAMP NOT NULL DEFAULT now()
);