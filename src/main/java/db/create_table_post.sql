CREATE TABLE post
(
    id      serial primary key,
    name    varchar(255) UNIQUE,
    text    text,
    link    varchar(255),
    created timestamp(0)
);