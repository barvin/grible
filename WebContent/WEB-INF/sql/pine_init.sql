--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.8
-- Dumped by pg_dump version 9.1.8
-- Started on 2013-03-09 22:20:19 EET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 161 (class 1259 OID 16944)
-- Dependencies: 6
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE categories (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    productid integer NOT NULL,
    parentid integer,
    type integer NOT NULL
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 162 (class 1259 OID 16947)
-- Dependencies: 161 6
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.categories_id_seq OWNER TO postgres;

--
-- TOC entry 2009 (class 0 OID 0)
-- Dependencies: 162
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE categories_id_seq OWNED BY categories.id;


--
-- TOC entry 163 (class 1259 OID 16949)
-- Dependencies: 6
-- Name: keys; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE keys (
    id integer NOT NULL,
    tableid integer NOT NULL,
    name character varying(255) NOT NULL,
    "order" integer NOT NULL,
    reftable integer
);


ALTER TABLE public.keys OWNER TO postgres;

--
-- TOC entry 164 (class 1259 OID 16952)
-- Dependencies: 163 6
-- Name: keys_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE keys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.keys_id_seq OWNER TO postgres;

--
-- TOC entry 2010 (class 0 OID 0)
-- Dependencies: 164
-- Name: keys_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE keys_id_seq OWNED BY keys.id;


--
-- TOC entry 165 (class 1259 OID 16954)
-- Dependencies: 6
-- Name: products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE products (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.products OWNER TO postgres;

--
-- TOC entry 166 (class 1259 OID 16957)
-- Dependencies: 6 165
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_id_seq OWNER TO postgres;

--
-- TOC entry 2011 (class 0 OID 0)
-- Dependencies: 166
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE products_id_seq OWNED BY products.id;


--
-- TOC entry 167 (class 1259 OID 16959)
-- Dependencies: 6
-- Name: rows; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rows (
    id integer NOT NULL,
    tableid integer NOT NULL,
    "order" integer NOT NULL
);


ALTER TABLE public.rows OWNER TO postgres;

--
-- TOC entry 168 (class 1259 OID 16962)
-- Dependencies: 167 6
-- Name: rows_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE rows_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rows_id_seq OWNER TO postgres;

--
-- TOC entry 2012 (class 0 OID 0)
-- Dependencies: 168
-- Name: rows_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE rows_id_seq OWNED BY rows.id;


--
-- TOC entry 169 (class 1259 OID 16964)
-- Dependencies: 6
-- Name: tables; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tables (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    type integer NOT NULL,
    categoryid integer,
    parentid integer,
    classname character varying(255),
    showusage boolean
);


ALTER TABLE public.tables OWNER TO postgres;

--
-- TOC entry 170 (class 1259 OID 16970)
-- Dependencies: 6 169
-- Name: tables_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tables_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tables_id_seq OWNER TO postgres;

--
-- TOC entry 2013 (class 0 OID 0)
-- Dependencies: 170
-- Name: tables_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tables_id_seq OWNED BY tables.id;


--
-- TOC entry 171 (class 1259 OID 16972)
-- Dependencies: 6
-- Name: tabletypes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tabletypes (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.tabletypes OWNER TO postgres;

--
-- TOC entry 172 (class 1259 OID 16975)
-- Dependencies: 6 171
-- Name: tabletypes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tabletypes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tabletypes_id_seq OWNER TO postgres;

--
-- TOC entry 2014 (class 0 OID 0)
-- Dependencies: 172
-- Name: tabletypes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tabletypes_id_seq OWNED BY tabletypes.id;


--
-- TOC entry 173 (class 1259 OID 16977)
-- Dependencies: 1946 6
-- Name: userpermissions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userpermissions (
    id integer NOT NULL,
    userid integer NOT NULL,
    productid integer NOT NULL,
    writeaccess boolean DEFAULT true NOT NULL
);


ALTER TABLE public.userpermissions OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 16981)
-- Dependencies: 6 173
-- Name: userpermissions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE userpermissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.userpermissions_id_seq OWNER TO postgres;

--
-- TOC entry 2015 (class 0 OID 0)
-- Dependencies: 174
-- Name: userpermissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE userpermissions_id_seq OWNED BY userpermissions.id;


--
-- TOC entry 175 (class 1259 OID 16983)
-- Dependencies: 1948 6
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    login character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    isadmin boolean DEFAULT false NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 16987)
-- Dependencies: 175 6
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 2016 (class 0 OID 0)
-- Dependencies: 176
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 177 (class 1259 OID 16989)
-- Dependencies: 1950 6
-- Name: values; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "values" (
    id integer NOT NULL,
    rowid integer NOT NULL,
    keyid integer NOT NULL,
    value character varying(2000) NOT NULL,
    isstorage boolean DEFAULT false NOT NULL,
    storagerows integer[]
);


ALTER TABLE public."values" OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 16996)
-- Dependencies: 6 177
-- Name: values_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE values_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.values_id_seq OWNER TO postgres;

--
-- TOC entry 2017 (class 0 OID 0)
-- Dependencies: 178
-- Name: values_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE values_id_seq OWNED BY "values".id;


--
-- TOC entry 1940 (class 2604 OID 16998)
-- Dependencies: 162 161
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY categories ALTER COLUMN id SET DEFAULT nextval('categories_id_seq'::regclass);


--
-- TOC entry 1941 (class 2604 OID 16999)
-- Dependencies: 164 163
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY keys ALTER COLUMN id SET DEFAULT nextval('keys_id_seq'::regclass);


--
-- TOC entry 1942 (class 2604 OID 17000)
-- Dependencies: 166 165
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products ALTER COLUMN id SET DEFAULT nextval('products_id_seq'::regclass);


--
-- TOC entry 1943 (class 2604 OID 17001)
-- Dependencies: 168 167
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rows ALTER COLUMN id SET DEFAULT nextval('rows_id_seq'::regclass);


--
-- TOC entry 1944 (class 2604 OID 17002)
-- Dependencies: 170 169
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tables ALTER COLUMN id SET DEFAULT nextval('tables_id_seq'::regclass);


--
-- TOC entry 1945 (class 2604 OID 17003)
-- Dependencies: 172 171
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tabletypes ALTER COLUMN id SET DEFAULT nextval('tabletypes_id_seq'::regclass);


--
-- TOC entry 1947 (class 2604 OID 17004)
-- Dependencies: 174 173
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY userpermissions ALTER COLUMN id SET DEFAULT nextval('userpermissions_id_seq'::regclass);


--
-- TOC entry 1949 (class 2604 OID 17005)
-- Dependencies: 176 175
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 1951 (class 2604 OID 17006)
-- Dependencies: 178 177
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "values" ALTER COLUMN id SET DEFAULT nextval('values_id_seq'::regclass);


INSERT INTO tabletypes(name) VALUES('storage');
INSERT INTO tabletypes(name) VALUES('table');
INSERT INTO tabletypes(name) VALUES('precondition');
INSERT INTO tabletypes(name) VALUES('postcondition');


