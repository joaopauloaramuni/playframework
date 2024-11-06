# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table agenda (
  nome                      varchar(255),
  email                     varchar(255),
  username                  varchar(255),
  telefone                  varchar(255),
  endereco                  varchar(255),
  facebook                  varchar(255))
;

create table email (
  sender                    varchar(255),
  recipient                 varchar(255),
  subject                   varchar(255),
  message                   varchar(255))
;

create table usuario (
  nome                      varchar(255),
  email                     varchar(255),
  username                  varchar(255),
  password                  varchar(255))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table agenda;

drop table email;

drop table usuario;

SET FOREIGN_KEY_CHECKS=1;

