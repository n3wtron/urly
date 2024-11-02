CREATE TABLE urls(id bigint auto_increment, short_url varchar(128) not null, long_url varchar(1024) not null);
CREATE UNIQUE INDEX urls_uniq_short_url ON urls (short_url);
CREATE UNIQUE INDEX urls_uniq_long_url ON urls (long_url);