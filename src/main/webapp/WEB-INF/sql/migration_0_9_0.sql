ALTER TABLE ONLY tables DROP COLUMN showusage;

BEGIN
	ALTER TABLE ONLY tables ADD COLUMN keys character varying;
	ALTER TABLE ONLY tables ADD COLUMN "values" character varying;
EXCEPTION
	WHEN duplicate_column THEN 
END;

CREATE TABLE version
(
  dbversion character varying(24)
)
ALTER TABLE version OWNER TO postgres;
INSERT INTO version VALUES ('0.9.0');