ALTER TABLE ONLY tables DROP COLUMN showusage;

ALTER TABLE ONLY tables ADD COLUMN keys character varying;
ALTER TABLE ONLY tables ADD COLUMN "values" character varying;

CREATE TABLE version
(
  dbversion character varying(24)
);
ALTER TABLE version OWNER TO postgres;
INSERT INTO version VALUES ('0.9.0');