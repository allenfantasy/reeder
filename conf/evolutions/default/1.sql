# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table article (
  id                        bigint not null,
  description               text,
  title                     varchar(255),
  link                      varchar(255),
  author                    varchar(255),
  pub_date                  varchar(255),
  guid                      varchar(255),
  is_readed                 boolean,
  is_starred                boolean,
  feed_id                   bigint,
  constraint pk_article primary key (id))
;

create table feed (
  id                        bigint not null,
  title                     varchar(255),
  link                      varchar(255),
  source_url                varchar(255),
  pub_date                  varchar(255),
  description               varchar(255),
  language                  varchar(255),
  type                      varchar(255),
  version                   varchar(255),
  user_id                   bigint,
  constraint pk_feed primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  created_at                varchar(255),
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id))
;

create sequence article_seq;

create sequence feed_seq;

create sequence users_seq;

alter table article add constraint fk_article_feed_1 foreign key (feed_id) references feed (id);
create index ix_article_feed_1 on article (feed_id);
alter table feed add constraint fk_feed_user_2 foreign key (user_id) references users (id);
create index ix_feed_user_2 on feed (user_id);



# --- !Downs

drop table if exists article cascade;

drop table if exists feed cascade;

drop table if exists users cascade;

drop sequence if exists article_seq;

drop sequence if exists feed_seq;

drop sequence if exists users_seq;

