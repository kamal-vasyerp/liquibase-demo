--liquibase formatted sql

--changeset kamaljain@vasyerp.com:V2-8754 failOnError:false
--comment: testing for multiple DB
INSERT INTO category (category_id, category_name) VALUES (192,'school');

--changeset kamaljain@vasyerp.com:V2-8755 failOnError:false
--comment: testing for multiple DB
INSERT INTO hotels ("hotel-id", about, "hotel-name", location) VALUES (120,'a very poor hotel','from hell','delhi');
