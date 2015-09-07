DROP SCHEMA public CASCADE;

DROP ROLE IF EXISTS oph;

CREATE ROLE "oph" WITH SUPERUSER;

CREATE SCHEMA public AUTHORIZATION oph;

GRANT ALL ON SCHEMA public TO oph;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE "session"
(
  id serial PRIMARY KEY,
  email character varying(255) NOT NULL,
  token text NOT NULL,
  idpentityid character varying(255) NOT NULL,

  CONSTRAINT session_email UNIQUE (email)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "session" OWNER TO oph;
CREATE INDEX ON "session"(email);

CREATE TABLE "user"
(
  id serial PRIMARY KEY,
  henkilo_oid character varying(255),
  email character varying(255) NOT NULL,
  idpentityid character varying(255) NOT NULL,
  firstname character varying(255) NOT NULL,
  lastname character varying(255) NOT NULL,
  gender character varying(255) NOT NULL,
  birthdate date NOT NULL,
  personid character varying(255),
  nationality character varying(255) NOT NULL,
  education_level character varying(255) NOT NULL,
  education_country character varying(255) NOT NULL,

  CONSTRAINT user_email UNIQUE (email),
  CONSTRAINT henkilo_oid UNIQUE (henkilo_oid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "user" OWNER TO oph;
CREATE INDEX ON "user"(email);

CREATE TABLE "payment"
(
  id serial PRIMARY KEY,
  henkilo_oid character varying(255) NOT NULL REFERENCES "user"(henkilo_oid),
  tstamp timestamp with time zone NOT NULL,
  reference character varying(255) NOT NULL,
  order_number character varying(255) NOT NULL,
  status character varying(255) NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "payment" OWNER TO oph;
CREATE INDEX ON "payment"(henkilo_oid);
CREATE INDEX ON "payment"(reference);
CREATE INDEX ON "payment"(henkilo_oid,order_number);