insert into conduit.logins(email, password_hash) values('john@mail.com', 'john_password');
insert into conduit.users(login_id, email, username)
    values((select id from conduit.logins where email = 'john@mail.com'), 'john@mail.com', 'john');
