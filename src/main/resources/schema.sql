drop all OBJECTS;

create table IF NOT EXISTS MPA
(
    MPA_ID   INTEGER         not null
        primary key,
    MPA_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      BIGINT auto_increment
        primary key,
    FILM_NAME    CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING,
    RELEASE_DATE DATE              not null,
    DURATION     INTEGER           not null,
    RATE          INTEGER          ,
    MPA         CHARACTER VARYING  not null
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID   INTEGER         not null
        primary key,
    GENRE_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS FILMS_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null
);

create table IF NOT EXISTS USERS
(
    USER_ID BIGINT auto_increment
        primary key,
    EMAIL     CHARACTER VARYING not null,
    LOGIN     CHARACTER VARYING not null,
    BIRTHDAY  DATE              not null,
    USER_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS FRIENDSHIPS
(
    FIRST_USER_ID BIGINT not null,
    SECOND_USER_ID BIGINT not null
);

create table IF NOT EXISTS FRIENDS_REQUESTS
(
    REQUESTER_ID BIGINT           not null,
    RESPONSER_ID BIGINT           not null,
    REQUEST_STATUS CHARACTER VARYING not null
);

create table IF NOT EXISTS FILM_LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      BIGINT auto_increment
        primary key,
    FILM_NAME    CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING,
    RELEASE_DATE DATE              not null,
    DURATION     INTEGER           not null,
    RATE          INTEGER          ,
    MPA         CHARACTER VARYING  not null
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID   INTEGER auto_increment
        primary key,
    GENRE_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS FILMS_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null
);

create table IF NOT EXISTS USERS
(
    USER_ID BIGINT auto_increment
        primary key,
    EMAIL     CHARACTER VARYING not null,
    LOGIN     CHARACTER VARYING not null,
    BIRTHDAY  DATE              not null,
    USER_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS FRIENDSHIPS
(
    FIRST_USER_ID BIGINT not null,
    SECOND_USER_ID BIGINT not null
);

create table IF NOT EXISTS FRIENDS_REQUESTS
(
    REQUESTER_ID BIGINT           not null,
    RESPONSER_ID BIGINT           not null,
    REQUEST_STATUS CHARACTER VARYING not null
);

create table IF NOT EXISTS FILM_LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null
);

