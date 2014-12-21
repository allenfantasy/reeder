# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table article (
  id                        bigint auto_increment not null,
  description               text,
  title                     varchar(255),
  link                      varchar(255),
  author                    varchar(255),
  pub_date                  varchar(255),
  guid                      varchar(255),
  is_readed                 boolean,
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
  constraint pk_feed primary key (id))
;

create sequence feed_seq;

alter table article add constraint fk_article_feed_1 foreign key (feed_id) references feed (id) on delete restrict on update restrict;
create index ix_article_feed_1 on article (feed_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists article;

drop table if exists feed;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists feed_seq;

