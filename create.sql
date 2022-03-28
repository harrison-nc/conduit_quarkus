create schema if not exists conduit;

create sequence conduit.login_id_seq as bigint
    increment by 1
    minvalue 100
    start with 100;

create table if not exists conduit.logins (
    "id" bigint not null default nextval('login_id_seq'),
    email varchar(255) not null,
    password_hash varchar(255) not null,
    primary key(id)
);

alter sequence conduit.login_id_seq owned by conduit.logins."id";

alter table conduit.logins add constraint logins_email_unique unique(email);
