--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: hakuperusteet; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE hakuperusteet WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE hakuperusteet OWNER TO postgres;

\connect hakuperusteet

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: application_object; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE application_object (
    id integer NOT NULL,
    henkilo_oid character varying(255) NOT NULL,
    hakukohde_oid character varying(255) NOT NULL,
    education_level character varying(255) NOT NULL,
    education_country character varying(255) NOT NULL,
    haku_oid character varying(255) NOT NULL
);


ALTER TABLE application_object OWNER TO oph;

--
-- Name: education_id_seq; Type: SEQUENCE; Schema: public; Owner: oph
--

CREATE SEQUENCE education_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE education_id_seq OWNER TO oph;

--
-- Name: education_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: oph
--

ALTER SEQUENCE education_id_seq OWNED BY application_object.id;


--
-- Name: jettysessionids; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jettysessionids (
    id character varying(120) NOT NULL
);


ALTER TABLE jettysessionids OWNER TO postgres;

--
-- Name: jettysessions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jettysessions (
    rowid character varying(120) NOT NULL,
    sessionid character varying(120),
    contextpath character varying(60),
    virtualhost character varying(60),
    lastnode character varying(60),
    accesstime bigint,
    lastaccesstime bigint,
    createtime bigint,
    cookietime bigint,
    lastsavedtime bigint,
    expirytime bigint,
    maxinterval bigint,
    map bytea
);


ALTER TABLE jettysessions OWNER TO postgres;

--
-- Name: ordernumber; Type: SEQUENCE; Schema: public; Owner: oph
--

CREATE SEQUENCE ordernumber
    START WITH 1000001
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ordernumber OWNER TO oph;

--
-- Name: payment; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE payment (
    id integer NOT NULL,
    henkilo_oid character varying(255) NOT NULL,
    tstamp timestamp with time zone NOT NULL,
    reference character varying(255) NOT NULL,
    order_number character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    paym_call_id character varying(255) NOT NULL
);


ALTER TABLE payment OWNER TO oph;

--
-- Name: payment_id_seq; Type: SEQUENCE; Schema: public; Owner: oph
--

CREATE SEQUENCE payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE payment_id_seq OWNER TO oph;

--
-- Name: payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: oph
--

ALTER SEQUENCE payment_id_seq OWNED BY payment.id;


--
-- Name: schema_version; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE schema_version (
    version_rank integer NOT NULL,
    installed_rank integer NOT NULL,
    version character varying(50) NOT NULL,
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE schema_version OWNER TO postgres;

--
-- Name: synchronization; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE synchronization (
    id integer NOT NULL,
    created timestamp without time zone NOT NULL,
    henkilo_oid character varying(255) NOT NULL,
    haku_oid character varying(255) NOT NULL,
    hakukohde_oid character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    updated timestamp without time zone
);


ALTER TABLE synchronization OWNER TO oph;

--
-- Name: synchronization_id_seq; Type: SEQUENCE; Schema: public; Owner: oph
--

CREATE SEQUENCE synchronization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE synchronization_id_seq OWNER TO oph;

--
-- Name: synchronization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: oph
--

ALTER SEQUENCE synchronization_id_seq OWNED BY synchronization.id;


--
-- Name: user; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE "user" (
    id integer NOT NULL,
    henkilo_oid character varying(255),
    email character varying(255) NOT NULL,
    idpentityid character varying(255) NOT NULL,
    firstname character varying(255) NOT NULL,
    lastname character varying(255) NOT NULL,
    gender character varying(255) NOT NULL,
    birthdate date NOT NULL,
    personid character varying(255),
    native_language character varying(255) NOT NULL,
    nationality character varying(255) NOT NULL
);


ALTER TABLE "user" OWNER TO oph;

--
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: oph
--

CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_id_seq OWNER TO oph;

--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: oph
--

ALTER SEQUENCE user_id_seq OWNED BY "user".id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: oph
--

ALTER TABLE ONLY application_object ALTER COLUMN id SET DEFAULT nextval('education_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: oph
--

ALTER TABLE ONLY payment ALTER COLUMN id SET DEFAULT nextval('payment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: oph
--

ALTER TABLE ONLY synchronization ALTER COLUMN id SET DEFAULT nextval('synchronization_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: oph
--

ALTER TABLE ONLY "user" ALTER COLUMN id SET DEFAULT nextval('user_id_seq'::regclass);


--
-- Name: education_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY application_object
    ADD CONSTRAINT education_pkey PRIMARY KEY (id);


--
-- Name: henkilo_oid; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT henkilo_oid UNIQUE (henkilo_oid);


--
-- Name: jettysessionids_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jettysessionids
    ADD CONSTRAINT jettysessionids_pkey PRIMARY KEY (id);


--
-- Name: jettysessions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jettysessions
    ADD CONSTRAINT jettysessions_pkey PRIMARY KEY (rowid);


--
-- Name: payment_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- Name: schema_version_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY schema_version
    ADD CONSTRAINT schema_version_pk PRIMARY KEY (version);


--
-- Name: synchronization_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY synchronization
    ADD CONSTRAINT synchronization_pkey PRIMARY KEY (id);


--
-- Name: user_email; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_email UNIQUE (email);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: education_henkilo_oid_hakukohde_oid_idx; Type: INDEX; Schema: public; Owner: oph; Tablespace: 
--

CREATE INDEX education_henkilo_oid_hakukohde_oid_idx ON application_object USING btree (henkilo_oid, hakukohde_oid);


--
-- Name: idx_jettysessions_expiry; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_jettysessions_expiry ON jettysessions USING btree (expirytime);


--
-- Name: idx_jettysessions_session; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX idx_jettysessions_session ON jettysessions USING btree (sessionid, contextpath);


--
-- Name: payment_henkilo_oid_idx; Type: INDEX; Schema: public; Owner: oph; Tablespace: 
--

CREATE INDEX payment_henkilo_oid_idx ON payment USING btree (henkilo_oid);


--
-- Name: payment_henkilo_oid_order_number_idx; Type: INDEX; Schema: public; Owner: oph; Tablespace: 
--

CREATE INDEX payment_henkilo_oid_order_number_idx ON payment USING btree (henkilo_oid, order_number);


--
-- Name: payment_reference_idx; Type: INDEX; Schema: public; Owner: oph; Tablespace: 
--

CREATE INDEX payment_reference_idx ON payment USING btree (reference);


--
-- Name: schema_version_ir_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX schema_version_ir_idx ON schema_version USING btree (installed_rank);


--
-- Name: schema_version_s_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX schema_version_s_idx ON schema_version USING btree (success);


--
-- Name: schema_version_vr_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX schema_version_vr_idx ON schema_version USING btree (version_rank);


--
-- Name: user_email_idx; Type: INDEX; Schema: public; Owner: oph; Tablespace: 
--

CREATE INDEX user_email_idx ON "user" USING btree (email);


--
-- Name: education_henkilo_oid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY application_object
    ADD CONSTRAINT education_henkilo_oid_fkey FOREIGN KEY (henkilo_oid) REFERENCES "user"(henkilo_oid);


--
-- Name: payment_henkilo_oid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_henkilo_oid_fkey FOREIGN KEY (henkilo_oid) REFERENCES "user"(henkilo_oid);


--
-- Name: synchronization_henkilo_oid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY synchronization
    ADD CONSTRAINT synchronization_henkilo_oid_fkey FOREIGN KEY (henkilo_oid) REFERENCES "user"(henkilo_oid);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

