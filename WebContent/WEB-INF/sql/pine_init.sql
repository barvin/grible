-- Pine database dump
-- Pine version 0.8

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET search_path = public, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE categories (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    productid integer NOT NULL,
    parentid integer,
    type integer NOT NULL
);
ALTER TABLE public.categories OWNER TO postgres;

CREATE SEQUENCE categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.categories_id_seq OWNER TO postgres;

ALTER SEQUENCE categories_id_seq OWNED BY categories.id;


CREATE TABLE keys (
    id integer NOT NULL,
    tableid integer NOT NULL,
    name character varying(255) NOT NULL,
    "order" integer NOT NULL,
    reftable integer
);
ALTER TABLE public.keys OWNER TO postgres;

CREATE SEQUENCE keys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.keys_id_seq OWNER TO postgres;

ALTER SEQUENCE keys_id_seq OWNED BY keys.id;


CREATE TABLE products (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);
ALTER TABLE public.products OWNER TO postgres;

CREATE SEQUENCE products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.products_id_seq OWNER TO postgres;

ALTER SEQUENCE products_id_seq OWNED BY products.id;


CREATE TABLE rows (
    id integer NOT NULL,
    tableid integer NOT NULL,
    "order" integer NOT NULL
);
ALTER TABLE public.rows OWNER TO postgres;

CREATE SEQUENCE rows_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.rows_id_seq OWNER TO postgres;

ALTER SEQUENCE rows_id_seq OWNED BY rows.id;


CREATE TABLE tables (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    type integer NOT NULL,
    categoryid integer,
    parentid integer,
    classname character varying(255),
    showusage boolean,
    showwarning boolean NOT NULL DEFAULT true,
  	modifiedtime timestamp without time zone NOT NULL DEFAULT '2013-01-01 00:00:00'::timestamp without time zone
);
ALTER TABLE public.tables OWNER TO postgres;

CREATE SEQUENCE tables_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.tables_id_seq OWNER TO postgres;

ALTER SEQUENCE tables_id_seq OWNED BY tables.id;


CREATE TABLE tabletypes (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);
ALTER TABLE public.tabletypes OWNER TO postgres;

CREATE SEQUENCE tabletypes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.tabletypes_id_seq OWNER TO postgres;

ALTER SEQUENCE tabletypes_id_seq OWNED BY tabletypes.id;


CREATE TABLE userpermissions (
    id integer NOT NULL,
    userid integer NOT NULL,
    productid integer NOT NULL,
    writeaccess boolean DEFAULT true NOT NULL
);
ALTER TABLE public.userpermissions OWNER TO postgres;

CREATE SEQUENCE userpermissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.userpermissions_id_seq OWNER TO postgres;

ALTER SEQUENCE userpermissions_id_seq OWNED BY userpermissions.id;


CREATE TABLE users (
    id integer NOT NULL,
    login character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    isadmin boolean DEFAULT false NOT NULL,
    tooltiponclick boolean DEFAULT false NOT NULL
);
ALTER TABLE public.users OWNER TO postgres;

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.users_id_seq OWNER TO postgres;

ALTER SEQUENCE users_id_seq OWNED BY users.id;


CREATE TABLE "values" (
    id integer NOT NULL,
    rowid integer NOT NULL,
    keyid integer NOT NULL,
    value character varying(5000) NOT NULL,
    isstorage boolean DEFAULT false NOT NULL,
    storagerows integer[]
);
ALTER TABLE public."values" OWNER TO postgres;

CREATE SEQUENCE values_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.values_id_seq OWNER TO postgres;

ALTER SEQUENCE values_id_seq OWNED BY "values".id;


ALTER TABLE ONLY categories ALTER COLUMN id SET DEFAULT nextval('categories_id_seq'::regclass);


ALTER TABLE ONLY keys ALTER COLUMN id SET DEFAULT nextval('keys_id_seq'::regclass);


ALTER TABLE ONLY products ALTER COLUMN id SET DEFAULT nextval('products_id_seq'::regclass);


ALTER TABLE ONLY rows ALTER COLUMN id SET DEFAULT nextval('rows_id_seq'::regclass);


ALTER TABLE ONLY tables ALTER COLUMN id SET DEFAULT nextval('tables_id_seq'::regclass);


ALTER TABLE ONLY tabletypes ALTER COLUMN id SET DEFAULT nextval('tabletypes_id_seq'::regclass);


ALTER TABLE ONLY userpermissions ALTER COLUMN id SET DEFAULT nextval('userpermissions_id_seq'::regclass);


ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


ALTER TABLE ONLY "values" ALTER COLUMN id SET DEFAULT nextval('values_id_seq'::regclass);


INSERT INTO tabletypes VALUES (1, 'storage');
INSERT INTO tabletypes VALUES (2, 'table');
INSERT INTO tabletypes VALUES (3, 'precondition');
INSERT INTO tabletypes VALUES (4, 'postcondition');
INSERT INTO tabletypes VALUES (5, 'enumearion');


ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


ALTER TABLE ONLY keys
    ADD CONSTRAINT keys_pkey PRIMARY KEY (id);


ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


ALTER TABLE ONLY rows
    ADD CONSTRAINT rows_pkey PRIMARY KEY (id);


ALTER TABLE ONLY tables
    ADD CONSTRAINT tables_pkey PRIMARY KEY (id);


ALTER TABLE ONLY tabletypes
    ADD CONSTRAINT tabletypes_pkey PRIMARY KEY (id);


ALTER TABLE ONLY userpermissions
    ADD CONSTRAINT userpermissions_pkey PRIMARY KEY (id);


ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


ALTER TABLE ONLY "values"
    ADD CONSTRAINT values_pkey PRIMARY KEY (id);


ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_parentid_fkey FOREIGN KEY (parentid) REFERENCES categories(id);


ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);

ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_type_fkey FOREIGN KEY (type) REFERENCES tabletypes(id);


ALTER TABLE ONLY keys
    ADD CONSTRAINT keys_reftable_fkey FOREIGN KEY (reftable) REFERENCES tables(id);


ALTER TABLE ONLY keys
    ADD CONSTRAINT keys_tableid_fkey FOREIGN KEY (tableid) REFERENCES tables(id);

ALTER TABLE ONLY keys
    ADD CONSTRAINT keys_tableid_order_key UNIQUE (tableid, "order");

    
ALTER TABLE ONLY rows
    ADD CONSTRAINT rows_tableid_fkey FOREIGN KEY (tableid) REFERENCES tables(id);

ALTER TABLE ONLY rows
    ADD CONSTRAINT rows_tableid_order_key UNIQUE (tableid, "order");


ALTER TABLE ONLY tables
    ADD CONSTRAINT tables_categoryid_fkey FOREIGN KEY (categoryid) REFERENCES categories(id);


ALTER TABLE ONLY tables
    ADD CONSTRAINT tables_parentid_fkey FOREIGN KEY (parentid) REFERENCES tables(id);


ALTER TABLE ONLY tables
    ADD CONSTRAINT tables_type_fkey FOREIGN KEY (type) REFERENCES tabletypes(id);


ALTER TABLE ONLY userpermissions
    ADD CONSTRAINT userpermissions_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


ALTER TABLE ONLY userpermissions
    ADD CONSTRAINT userpermissions_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


ALTER TABLE ONLY "values"
    ADD CONSTRAINT values_keyid_fkey FOREIGN KEY (keyid) REFERENCES keys(id);


ALTER TABLE ONLY "values"
    ADD CONSTRAINT values_rowid_fkey FOREIGN KEY (rowid) REFERENCES rows(id);

ALTER TABLE ONLY "values"
    ADD CONSTRAINT values_rowid_keyid_key UNIQUE (rowid, keyid);


REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
