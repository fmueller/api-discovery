ALTER TABLE api_version ADD COLUMN definition_id INTEGER NOT NULL DEFAULT 0;
ALTER TABLE api_version ADD COLUMN definition_hash TEXT NOT NULL DEFAULT '';

DROP INDEX api_version_api_name_idx;
DROP INDEX api_version_api_version_idx;

CREATE INDEX api_version_definition_hash_idx ON api_version(definition_hash);
CREATE UNIQUE INDEX api_version_api_name_version_definition_id_idx ON api_version(api_name, api_version, definition_id);