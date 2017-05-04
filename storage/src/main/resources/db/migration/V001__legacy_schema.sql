CREATE TABLE api (
  application_id  TEXT         NOT NULL,
  status          TEXT         NOT NULL,
  type            TEXT,
  name            TEXT,
  version         TEXT,
  url             TEXT,
  ui              TEXT,
  definition      TEXT,
  created         TIMESTAMP,
  last_changed    TIMESTAMP,
  last_persisted  TIMESTAMP,
  service_url     TEXT,
  lifecycle_state VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',

  PRIMARY KEY (application_id)
);

CREATE INDEX api_status_idx ON api (status);
CREATE INDEX api_lifecycle_state_idx ON api (lifecycle_state);