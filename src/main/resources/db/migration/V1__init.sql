CREATE TABLE line_channel_credential (
  channel_credential_id VARCHAR(50)  PRIMARY KEY,
  channel_id            VARCHAR(20)  NOT NULL,
  channel_secret        VARCHAR(60)  NOT NULL,
  created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  revoked_at            DATETIME,

  INDEX (channel_id, created_at, revoked_at)
);

CREATE TABLE line_channel_token (
  channel_token_id      VARCHAR(50)  PRIMARY KEY,
  channel_id            VARCHAR(20)  NOT NULL,
  token                 TEXT         NOT NULL,
  expires_at            DATETIME     NOT NULL,
  created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  revoked_at            DATETIME,

  INDEX (channel_id, created_at, revoked_at)
);

CREATE TABLE line_message_template (
  template_id           VARCHAR(50)  PRIMARY KEY,
  payload_type          VARCHAR(10)  NOT NULL,
  payload               TEXT         NOT NULL,
  created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE line_push_message (
  push_message_id       VARCHAR(50)  PRIMARY KEY,
  channel_id            VARCHAR(20)  NOT NULL,
  target_type           VARCHAR(15),
  target                VARCHAR(100),
  template_id           VARCHAR(50),
  tag                   VARCHAR(50),
  created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  sent_at               DATETIME,
  error_at              DATETIME,

  INDEX (channel_id, tag),
  INDEX (created_at),
  FOREIGN KEY (template_id) REFERENCES line_message_template(template_id)
);
