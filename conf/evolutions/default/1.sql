# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table t_competitions (
  id                        bigint not null,
  name                      varchar(255),
  number_of_questions       integer,
  number_of_players         integer,
  current_question          integer,
  end_time                  time,
  constraint pk_t_competitions primary key (id))
;

create table t_questions (
  id                        bigint not null,
  level                     integer,
  time                      integer,
  content                   varchar(255),
  result                    varchar(255),
  constraint pk_t_questions primary key (id))
;

create table t_users (
  id                        bigint not null,
  name                      varchar(255),
  competition_id            bigint,
  current_question          integer,
  points                    integer,
  constraint pk_t_users primary key (id))
;


create table t_competitions_questions (
  t_competitions_id              bigint not null,
  t_questions_id                 bigint not null,
  constraint pk_t_competitions_questions primary key (t_competitions_id, t_questions_id))
;
create sequence t_competitions_seq;

create sequence t_questions_seq;

create sequence t_users_seq;

alter table t_users add constraint fk_t_users_competition_1 foreign key (competition_id) references t_competitions (id) on delete restrict on update restrict;
create index ix_t_users_competition_1 on t_users (competition_id);



alter table t_competitions_questions add constraint fk_t_competitions_questions_t_01 foreign key (t_competitions_id) references t_competitions (id) on delete restrict on update restrict;

alter table t_competitions_questions add constraint fk_t_competitions_questions_t_02 foreign key (t_questions_id) references t_questions (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists t_competitions;

drop table if exists t_competitions_questions;

drop table if exists t_questions;

drop table if exists t_users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists t_competitions_seq;

drop sequence if exists t_questions_seq;

drop sequence if exists t_users_seq;

