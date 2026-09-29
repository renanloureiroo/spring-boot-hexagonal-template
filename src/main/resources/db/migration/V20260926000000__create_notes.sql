create table notes (
  id varchar(36) primary key,
  title varchar(120) not null,
  created_at timestamp with time zone not null
);
