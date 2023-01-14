DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    255
) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY
(
    id
),
    CONSTRAINT UQ_USER_EMAIL UNIQUE
(
    email
)
    );

create table IF NOT EXISTS items
(
    id
    bigint
    auto_increment
    primary
    key,
    name
    VARCHAR,
    description
    VARCHAR,
    available
    BOOLEAN,
    owner_id
    BIGINT
    not
    null,
    request_id
    int,
    constraint
    ITEMS_USERS_ID_FK
    foreign
    key
(
    owner_id
) references USERS
(
    id
)
    on delete cascade
    );

create table IF NOT EXISTS bookings
(
    id
    bigint
    auto_increment
    primary
    key,
    start_time
    TIMESTAMP
    WITHOUT
    TIME
    ZONE,
    end_time
    TIMESTAMP
    WITHOUT
    TIME
    ZONE,
    item_id
    bigint,
    booker_id
    bigint,
    status
    varchar,
    constraint
    BOOKING_USERS_ID_FK
    foreign
    key
(
    booker_id
) references USERS
(
    id
)
    on delete cascade,
    constraint BOOKING_ITEMS_ID_FK
    foreign key
(
    item_id
) references ITEMS
(
    id
)
    on delete cascade
    );


create table IF NOT EXISTS comments
(
    id
    int
    auto_increment
    primary
    key,
    text
    varchar,
    author_id
    bigint,
    item_id
    bigint,
    constraint
    COMMENTS_ITEMS_ID_FK
    foreign
    key
(
    item_id
) references ITEMS
    on delete cascade,
    constraint COMMENTS_USERS_ID_FK
    foreign key
(
    author_id
) references USERS
    on delete cascade
    );

create table IF NOT EXISTS requests
(
    id
    int
    auto_increment
    primary
    key,
    description
    varchar,
    requestor_id
    bigint,
    created
    TIMESTAMP
    WITHOUT
    TIME
    ZONE,
    constraint
    REQUESTS_USERS_ID_FK
    foreign
    key
(
    requestor_id
) references USERS
    on delete cascade
    );





