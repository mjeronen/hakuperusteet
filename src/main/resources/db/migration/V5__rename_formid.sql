ALTER TABLE "application_object" RENAME COLUMN form_id TO haku_oid;
UPDATE "application_object" SET haku_oid = '1.2.246.562.29.80171652938';
