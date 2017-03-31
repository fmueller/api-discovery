ALTER TABLE application
  DROP COLUMN last_crawled;

ALTER TABLE application
  DROP COLUMN crawled_state;

ALTER TABLE application
  DROP COLUMN service_url;

ALTER TABLE application
  ADD COLUMN app_url TEXT;

CREATE TABLE api_deployment (
  api_id              BIGINT REFERENCES api_version(id),
  application_name    TEXT REFERENCES application(name),
  api_ui              TEXT,
  api_url             TEXT,
  last_crawled        TIMESTAMP,
  crawled_state       TEXT,
  lifecycle_state     TEXT,
  created             TIMESTAMP
);

DROP INDEX api_version_lifecycle_state_idx;

ALTER TABLE api_version
  DROP COLUMN application_name;

ALTER TABLE api_version
  DROP COLUMN lifecycle_state;

ALTER TABLE api_version
  DROP COLUMN url;

ALTER TABLE api_version
  DROP COLUMN ui;

ALTER TABLE api_version
  DROP COLUMN last_content_change;