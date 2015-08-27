CREATE ROLE "oph" WITH SUPERUSER;

CREATE TABLE "user"
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  henkilo_oid character varying(255),
  firstname character varying(255) NOT NULL,
  lastname character varying(255) NOT NULL,
  syntymaaika date NOT NULL,
  hetu character varying(255),
  nationality character varying(255) NOT NULL,
  education_level character varying(255),
  education_country character varying(255),

  CONSTRAINT user_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "user" OWNER TO oph;
