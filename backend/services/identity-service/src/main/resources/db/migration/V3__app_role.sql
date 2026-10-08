DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'nexusgesti_app') THEN
        CREATE ROLE nexusgesti_app;
    END IF;
END $$;
GRANT USAGE ON SCHEMA identity TO nexusgesti_app;
