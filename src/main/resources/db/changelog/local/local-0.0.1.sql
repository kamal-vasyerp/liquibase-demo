--liquibase formatted sql

--changeset kamaljain@vasyerp.com:V2-9632 failOnError:false
--comment: testing for multiple DB
INSERT INTO category (category_id, category_name) VALUES (193,'school-local');

--changeset kamaljain@vasyerp.com:V2-9633 failOnError:false
--comment: testing for multiple DB
INSERT INTO hotels ("hotel-id", about, "hotel-name", location) VALUES (119,'a very poor hotel','from hell','delhi');

--changeset kamaljain@vasyerp.com:V2-9634 failOnError:false
--comment: testing for multiple DB
INSERT INTO hotels ("hotel-id", about, "name", location) VALUES (121,'a very crazy hotel','from hell','pune');