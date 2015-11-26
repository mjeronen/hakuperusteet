
INSERT INTO "user_details" (id, firstname, lastname, gender, birthdate, personid, native_language, nationality)
SELECT id, firstname, lastname, gender, birthdate, personid, native_language, nationality FROM "user";

ALTER TABLE "user" DROP COLUMN firstname;
ALTER TABLE "user" DROP COLUMN lastname;
ALTER TABLE "user" DROP COLUMN gender;
ALTER TABLE "user" DROP COLUMN birthdate;
ALTER TABLE "user" DROP COLUMN personid;
ALTER TABLE "user" DROP COLUMN native_language;
ALTER TABLE "user" DROP COLUMN nationality;
