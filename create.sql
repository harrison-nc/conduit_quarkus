create schema if not exists conduit;

create table if not exists conduit.logins (
    "id" bigint not null generated always as identity,
    email varchar(255) not null,
    password_hash varchar(255) not null,
    primary key(id)
);

create sequence if not exists conduit.login_id_seq as bigint
 increment by 1
 minvalue 100
 start with 100
 owned by conduit.logins."id";

alter table conduit.logins add constraint logins_email_unique unique(email);
