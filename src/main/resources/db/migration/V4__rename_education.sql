ALTER TABLE "education" RENAME TO "application_object";

ALTER TABLE "application_object" ADD COLUMN form_id character varying(255);
UPDATE "application_object" SET form_id = 'aalto';
ALTER TABLE "application_object" ALTER COLUMN form_id SET NOT NULL;