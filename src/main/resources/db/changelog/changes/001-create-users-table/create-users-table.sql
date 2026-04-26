create table users
(
                       id serial primary key,
                       username varchar(30) not null,
                       email varchar(30) not null,
                       password varchar(30) not null
)