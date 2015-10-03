CREATE TABLE "session"
(
  id serial PRIMARY KEY,
  email character varying(255) NOT NULL,
  token text NOT NULL,
  idpentityid character varying(255) NOT NULL,

  CONSTRAINT session_email UNIQUE (email)
);
CREATE INDEX "session_email" ON "session"(email);

CREATE TABLE "user"
(
  id serial PRIMARY KEY,
  henkilo_oid character varying(255),
  email character varying(255) NOT NULL,
  idpentityid character varying(255) NOT NULL,
  firstname character varying(255) NOT NULL,
  lastname character varying(255) NOT NULL,
  gender character varying(255) NOT NULL,
  birthdate date NOT NULL,
  personid character varying(255),
  native_language character varying(255) NOT NULL,
  nationality character varying(255) NOT NULL,
  education_level character varying(255) NOT NULL,
  education_country character varying(255) NOT NULL,

  CONSTRAINT user_email UNIQUE (email),
  CONSTRAINT henkilo_oid UNIQUE (henkilo_oid)
);
CREATE INDEX "user_email" ON "user"(email);

CREATE TABLE "payment"
(
  id serial PRIMARY KEY,
  henkilo_oid character varying(255) NOT NULL REFERENCES "user"(henkilo_oid),
  tstamp timestamp with time zone NOT NULL,
  reference character varying(255) NOT NULL,
  order_number character varying(255) NOT NULL,
  status character varying(255) NOT NULL
);
CREATE INDEX "payment_henkilo_oid" ON "payment"(henkilo_oid);
CREATE INDEX "payment_reference" ON "payment"(reference);
CREATE INDEX "payment_henkilo_oid_order_number" ON "payment"(henkilo_oid,order_number);