DROP SCHEMA public CASCADE;

DROP ROLE IF EXISTS oph;

CREATE ROLE "oph" WITH SUPERUSER;

CREATE SCHEMA public AUTHORIZATION oph;

GRANT ALL ON SCHEMA public TO oph;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE "user"
(
  id serial PRIMARY KEY,
  henkilo_oid character varying(255),
  email character varying(255) NOT NULL,
  firstname character varying(255) NOT NULL,
  lastname character varying(255) NOT NULL,
  gender character varying(255) NOT NULL,
  birthdate date NOT NULL,
  personid character varying(255),
  nationality character varying(255) NOT NULL,
  education_level character varying(255) NOT NULL,
  education_country character varying(255) NOT NULL,

  CONSTRAINT user_email UNIQUE (email)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "user" OWNER TO oph;
