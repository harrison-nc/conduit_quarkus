create schema if not exists conduit;

create sequence if not exists conduit.user_id_seq as bigint
    increment by 1
    minvalue 100
    start with 100;

create table if not exists conduit.users (
    "id" bigint not null default nextval('user_id_seq'),
    email varchar(255) not null,
    username varchar(255) not null,
    bio varchar(255),
    image varchar(255),
    password_hash varchar(255),
    primary key("id")
);

alter sequence conduit.user_id_seq owned by conduit.users."id";
alter table conduit.users add constraint user_email_unique unique(email);
alter table conduit.users add constraint user_username_unique unique(username);
