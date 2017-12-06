# --- !Ups

create table "address_contact" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"street" VARCHAR,"state" VARCHAR,"country" VARCHAR,"city" VARCHAR,"zipCode" VARCHAR);
create table "contacts_book" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"owner_id" BIGINT NOT NULL,"create_date" BIGINT NOT NULL);
create table "contacts" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"contacts_book_id" BIGINT NOT NULL,"create_date" BIGINT NOT NULL,"skype_id" VARCHAR,"fax" VARCHAR,"company" VARCHAR,"job_position" VARCHAR,"address_id" BIGINT,"time_zone" VARCHAR,"language" VARCHAR,"contact_type" INTEGER,"note" VARCHAR,"advertiser" BIGINT);
create table "phone_contact" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"type" INTEGER NOT NULL,"phone" VARCHAR NOT NULL,"contact_id" BIGINT NOT NULL);
create table "email_contact" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"type" INTEGER NOT NULL,"email" VARCHAR NOT NULL,"contact_id" BIGINT NOT NULL);
create table "groups_contact" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"contacts_book_id" BIGINT NOT NULL,"create_date" BIGINT NOT NULL);
create table "group_to_contacts" ("group_id" BIGINT NOT NULL,"contact_id" BIGINT NOT NULL);
alter table "group_to_contacts" add constraint "pk" primary key("group_id","contact_id");
create table "profiles" ("user_id" BIGINT NOT NULL,"nickname" VARCHAR NOT NULL,"email" VARCHAR NOT NULL,"first_name" VARCHAR,"last_name" VARCHAR,"avatar_url" VARCHAR,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "login_info" ("providerID" VARCHAR NOT NULL,"providerKey" VARCHAR NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "password_info" ("hasher" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"salt" VARCHAR,"login_info_id" BIGINT NOT NULL);
create table "recover_password_info" ("email" VARCHAR NOT NULL,"host" VARCHAR NOT NULL,"user_id" BIGINT NOT NULL,"expired_date" BIGINT NOT NULL,"recover_id" VARCHAR NOT NULL,"status" INTEGER NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "users" ("login_info_id" BIGINT NOT NULL,"company_id" BIGINT NOT NULL,"role_id" BIGINT NOT NULL,"state" INTEGER NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "companies" ("name" VARCHAR NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "permissions" ("action" VARCHAR NOT NULL,"permission_state" INTEGER NOT NULL,"role_id" BIGINT NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "roles" ("name" VARCHAR NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
create table "invite" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"roleId" BIGINT NOT NULL,"companyId" BIGINT NOT NULL,"expiredDate" BIGINT NOT NULL,"status" INTEGER NOT NULL,"hash" VARCHAR NOT NULL,"invitedBy" BIGINT NOT NULL);
create table "jwt_authenticators" ("authenticator" VARCHAR NOT NULL,"identifier" VARCHAR NOT NULL,"id" BIGSERIAL NOT NULL PRIMARY KEY);
alter table "users" add constraint "login_info" foreign key("login_info_id") references "login_info"("id") on update CASCADE on delete CASCADE;


# --- !Downs

alter table "group_to_contacts" drop constraint "pk";
drop table "group_to_contacts";
drop table "groups_contact";
drop table "email_contact";
drop table "phone_contact";
drop table "contacts";
drop table "contacts_book";
drop table "address_contact";
drop table "profiles";
alter table "users" drop constraint "login_info";
drop table "jwt_authenticators";
drop table "invite";
drop table "roles";
drop table "permissions";
drop table "companies";
drop table "users";
drop table "recover_password_info";
drop table "password_info";
drop table "login_info";
