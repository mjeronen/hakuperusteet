ALTER TABLE "user" DROP COLUMN education_level;
ALTER TABLE "user" DROP COLUMN education_country;

CREATE TABLE "education"
(
  id serial PRIMARY KEY,
  henkilo_oid character varying(255) NOT NULL REFERENCES "user"(henkilo_oid),
  hakukohde_oid character varying(255) NOT NULL,
  education_level character varying(255) NOT NULL,
  education_country character varying(255) NOT NULL
);
CREATE INDEX "education_henkilo_oid_hakukohde_oid_idx" ON "education"(henkilo_oid,hakukohde_oid);