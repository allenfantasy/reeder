# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table article (
  id                        bigserial not null,
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
  id                        bigserial not null,
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

create table user (
  id                        bigserial not null,
  email                     varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  created_at                varchar(255),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create sequence feed_seq;

alter table article add constraint fk_article_feed_1 foreign key (feed_id) references feed (id) on delete restrict on update restrict;
create index ix_article_feed_1 on article (feed_id);
alter table feed add constraint fk_feed_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_feed_user_2 on feed (user_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists article;

drop table if exists feed;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists feed_seq;

