

 create table topics (
 id uuid primary key,
 nickname varchar not null,
 mail varchar not null,
 topic varchar not null,
 content varchar not null,
 last_activity timestamp without time zone,
 secret integer not null
 );

 create table answers (
 id uuid primary key,
 nickname varchar not null,
 mail varchar not null,
 topic_id uuid not null,
 content varchar not null,
 last_activity timestamp without time zone,
 secret integer not null,

 constraint answers_topics_id_fkey foreign key (topic_id)
 references topics(id)
 ON UPDATE CASCADE ON DELETE CASCADE
 );
