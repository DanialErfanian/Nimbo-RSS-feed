CREATE TABLE IF NOT EXISTS RSSChannel
(
    id            int NOT NULL AUTO_INCREMENT,
    RSSLink       VARCHAR(600) UNIQUE,
    Title         TEXT,
    Link          TEXT,
    Description   TEXT,
    LastBuildDate DATETIME,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS News
(
    id            int     NOT NULL AUTO_INCREMENT,
    Title         TEXT,
    Link          TEXT,
    NewsText      TEXT,
    Description   TEXT,
    Author        TEXT,
    PublishedDate DATETIME,
    RSSLink       int(11) NOT NULL,
    FOREIGN KEY (RSSLink) REFERENCES RSSChannel (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  CHARSET = utf8mb4;