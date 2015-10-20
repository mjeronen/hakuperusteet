CREATE TABLE "synchronization"
(
    id serial PRIMARY KEY,
    created timestamp NOT NULL,
    henkilo_oid character varying(255) NOT NULL REFERENCES "user"(henkilo_oid),
    haku_oid character varying(255) NOT NULL,
    hakukohde_oid character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    updated timestamp
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "synchronization" OWNER TO oph;

