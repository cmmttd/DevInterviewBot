create table users
(
    id           serial primary key,
    chat_id      bigint UNIQUE NOT NULL,
    name         varchar(32)   NOT NULL,
    current_q_id int default 0,
    history      text default ''
);
insert into users (chat_id, name)
values (1234567890, 'TestUser');

create table questions
(
    id          serial primary key,
    question    text,
    description text
);
insert into questions (question, description)
values ('What is Java?', 'https://www.java.com/ru/');
insert into questions (question, description)
values ('What is the name of the most important branch in git??',
        'https://lurkmore.to/%D0%92%D0%B0%D1%88_%D0%BF%D0%BE%D1%81%D1%82_%D0%BE%D0%B3%D0%BE%D1%80%D1%87%D0%B0%D0%B5%D1%82_%D0%BD%D0%B5%D0%B3%D1%80%D0%BE%D0%B2');
insert into questions (question, description)
values ('1+1?', '2');
insert into questions (question, description)
values ('7/2?', 'hz');
