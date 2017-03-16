CREATE TABLE application (
  name                TEXT PRIMARY KEY,
  service_url         TEXT,
  last_crawled        TIMESTAMP,
  crawled_state      TEXT,
  created             TIMESTAMP
);

CREATE TABLE api_version (
  id                  BIGSERIAL PRIMARY KEY,
  application_name    TEXT REFERENCES application(name),
  api_name            TEXT NOT NULL,
  api_version         TEXT,
  definition          TEXT,
  lifecycle_state    TEXT,
  url                 TEXT,
  ui                  TEXT,
  last_content_change TIMESTAMP,
  created             TIMESTAMP
);

CREATE INDEX api_version_lifecycle_state_idx ON api_version(lifecycle_state);
CREATE INDEX api_version_api_name_idx ON api_version(api_name);
CREATE INDEX api_version_api_version_idx ON api_version(api_version);

