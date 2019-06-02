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
 lastActivity timestamp,
 secret integer not null,

 constraint answers_topics_id_fkey foreign key (topicID)
 references topics(id)
 ON UPDATE CASCADE ON DELETE CASCADE
 );
