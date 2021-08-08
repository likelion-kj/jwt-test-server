DROP TABLE IF EXISTS member;

CREATE TABLE member (
  userId VARCHAR(100)  PRIMARY KEY,
  salt VARCHAR(200) NOT NULL,
  hashedPassword VARCHAR(500) NOT NULL,
  name VARCHAR(100) DEFAULT NULL
);

INSERT INTO member (userId, salt, hashedPassword, name) VALUES
  ('testId1', 'salt1', '71ac908eac6850f89b0495195f6b39c8cd5c20592ebcc4767642c61cd310ba26', 'kim'),
  ('testId2', 'salt2', '5a3550206dd21e45d3043bfdddcae80d04aa655b7970cec48bb1100cdff5a3e6', 'lee'),
  ('testId3', 'salt3', '9ed018fd5617edb623823f94d5f35c2cb68f28faa010d151d2645fe24a7052d2', 'seo');