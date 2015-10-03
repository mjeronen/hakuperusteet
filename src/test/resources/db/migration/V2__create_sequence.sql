CREATE SEQUENCE ordernumber START WITH 1000001;

ALTER TABLE "payment" ADD COLUMN paym_call_id character varying(255) NOT NULL;