create schema if not exists conduit;

create sequence if not exists conduit.login_id_seq as bigint
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

create sequence if not exists conduit.user_id_seq as bigint
    increment by 1
    minvalue 100
    start with 100;

create table if not exists conduit.users (
    "id" bigint not null default nextval('user_id_seq'),
    login_id bigint not null,
    username varchar(255) not null,
    primary key("id")
);

alter sequence conduit.user_id_seq owned by conduit.users."id";

alter table conduit.users add constraint user_username_unique unique(username);
alter table conduit.users add constraint user_login_id_unique unique(login_id);
alter table conduit.users add foreign key(login_id) references conduit.logins("id");
