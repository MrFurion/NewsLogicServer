create table News(
                     id uuid PRIMARY KEY,
                     time TIMESTAMP WITH TIME ZONE,
                     text VARCHAR(5000) not null,
                     title varchar(50) not null unique
);

create table Comment(
                        id uuid PRIMARY KEY,
                        time TIMESTAMP WITH TIME ZONE,
                        text VARCHAR(2500) not null,
                        username VARCHAR(25) not null,
                        news_id uuid REFERENCES news(id) on delete cascade
);

create table client_name (
                             client_id uuid not null,
                             username varchar(255),
                             primary key (client_id, username),
                             foreign key (client_id) references News(id) on delete cascade
);