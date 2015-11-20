CREATE TABLE "user_details"
(
  id serial PRIMARY KEY,
  firstname character varying(255) NOT NULL,
  lastname character varying(255) NOT NULL,
  gender character varying(255) NOT NULL,
  birthdate date NOT NULL,
  personid character varying(255),
  native_language character varying(255) NOT NULL,
  nationality character varying(255) NOT NULL,
  foreign key(id) references "user"(id)
)
WITH (
  OIDS=FALSE
);