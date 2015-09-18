CREATE SEQUENCE ordernumber START 1000001;
ALTER TABLE "ordernumber" OWNER TO oph;

ALTER TABLE "payment" ADD COLUMN paym_call_id character varying(255) NOT NULL;