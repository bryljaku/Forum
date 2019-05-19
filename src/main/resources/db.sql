 create table topics (
 id serial primary key,
 nickname varchar(255) not null,
 topic varchar(511) unique not null,
 content varchar(1023) not null,
 lastActivity timestamp,
 secret integer not null
 );
 
 create table answers (
 id serial primary key,
 nickname varchar(255) not null,
 topicID integer not null,
 content varchar(1023) not null,
 createdOn timestamp,
 secret integer not null,

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
order by t.lastActivity, a.createdOn desc;