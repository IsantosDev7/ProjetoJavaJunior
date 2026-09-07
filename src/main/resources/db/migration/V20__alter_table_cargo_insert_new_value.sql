INSERT INTO cargo (name) VALUES ('Administrador')
    ON CONFLICT (name) DO NOTHING;