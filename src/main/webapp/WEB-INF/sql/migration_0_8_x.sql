BEGIN
	ALTER TABLE tables ADD COLUMN showwarning boolean NOT NULL DEFAULT true;
	ALTER TABLE tables ADD COLUMN modifiedtime timestamp without time zone NOT NULL DEFAULT '2013-01-01 00:00:00';
	ALTER TABLE users ADD COLUMN tooltiponclick boolean NOT NULL DEFAULT false;
EXCEPTION
	WHEN duplicate_column THEN 
END;