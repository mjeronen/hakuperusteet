
ALTER TABLE "synchronization" ADD hakemus_oid character varying(255) NULL;
ALTER TABLE "synchronization" ALTER COLUMN haku_oid DROP NOT NULL;
ALTER TABLE "synchronization" ALTER COLUMN hakukohde_oid DROP NOT NULL;