use wakutabi;
create table user(
	id bigint auto_increment primary key,
    username varchar(50) not null unique,
    password varchar(100) not null,
    name varchar(50) not null,
    email varchar(100) not null unique,
    phone varchar(20),
    gender enum('mail','female','other') not null default 'orther',
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);