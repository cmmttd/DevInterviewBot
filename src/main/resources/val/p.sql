create database botdb;
\c botdb
create table users (Id serial primary key, chat_id bigint, name varchar(32));
insert into users (chat_id, name) values (1234567890, 'TestUser');
