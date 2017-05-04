CREATE TABLE application (
  name    TEXT PRIMARY KEY,
  created TIMESTAMP,
  app_url TEXT
);

CREATE TABLE api_version (
  id              BIGSERIAL PRIMARY KEY,
  api_name        TEXT    NOT NULL,
  api_version     TEXT,
  definition_id   INTEGER NOT NULL DEFAULT 0,
  definition_type TEXT,
  definition      TEXT,
  definition_hash TEXT    NOT NULL DEFAULT '',
  created         TIMESTAMP
);

CREATE TABLE api_deployment (
  api_id           BIGINT REFERENCES api_version (id),
  application_name TEXT REFERENCES application (name),
  api_ui           TEXT,
  api_url          TEXT,
  last_crawled     TIMESTAMP,
  crawled_state    TEXT,
  lifecycle_state  TEXT,
  created          TIMESTAMP
);

CREATE INDEX api_version_definition_hash_idx ON api_version (definition_hash);
CREATE UNIQUE INDEX api_version_api_name_version_definition_id_idx ON api_version (api_name, api_version, definition_id);