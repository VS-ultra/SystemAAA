create table users
(
    id       bigserial    primary key,
    username varchar(50)  not null unique,
    email    varchar(100) not null unique,
    password varchar(100) not null
)