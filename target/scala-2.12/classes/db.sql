 create table users(
 nickname varchar(256) primary key,
 mail varchar(512) unique not null
 );
 
 create table topics (
 id serial primary key,
 nickname varchar(256) not null,
 subject varchar(512) unique not null,
 secret integer not null,
 lastActivity timestamp,
 constraint topics_users_nickname_fkey foreign key (nickname)
 references users(nickname)
 ON UPDATE CASCADE ON DELETE CASCADE
 );
 
 create table answers (
 id serial primary key,
 nickname varchar(256) not null,
 topicID integer not null,
 content varchar(1024) not null,
 created_on timestamp,
 secret integer not null,
 constraint answers_users_nickname_fkey foreign key (nickname)
 references users(nickname)
 ON UPDATE CASCADE ON DELETE CASCADE,
 constraint answers_topics_id_fkey foreign key (topicID)
 references topics(id)
 ON UPDATE CASCADE ON DELETE CASCADE
 );
 
create view orderedTopics as
select t.id as topicID, t.nickname, t.subject 
from topics t
order by t.lastActivity desc;

create view orderedAnswersForTopics as
select * from answers a
inner join topics t on t.id = a.topicID
order by t.lastActivity, a.created_on desc;