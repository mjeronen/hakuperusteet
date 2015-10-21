ALTER TABLE "application_object" ADD UNIQUE (henkilo_oid, hakukohde_oid);

CREATE TABLE "synchronization"
(
  id serial PRIMARY KEY,
  created timestamp NOT NULL,
  henkilo_oid character varying(255) NOT NULL REFERENCES "user"(henkilo_oid),
  haku_oid character varying(255) NOT NULL,
  hakukohde_oid character varying(255) NOT NULL,
  status character varying(255) NOT NULL,
  updated timestamp,
  FOREIGN KEY(henkilo_oid, hakukohde_oid) REFERENCES "application_object"(henkilo_oid, hakukohde_oid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "synchronization" OWNER TO oph;

