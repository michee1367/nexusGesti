-- Role applicatif : Flyway se connecte en nexusgesti (proprietaire, superuser)
-- pour poser le schema, le datasource de l'application se connecte en
-- nexusgesti_app. Un superuser est exempt de RLS meme avec FORCE : sans ce
-- decrochage les policies de V3 seraient inopexantes.
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'nexusgesti_app') THEN
        CREATE ROLE nexusgesti_app LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOBYPASSRLS;
        ALTER ROLE nexusgesti_app PASSWORD 'nexusgesti_secret';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA auth TO nexusgesti_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA auth TO nexusgesti_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA auth TO nexusgesti_app;
