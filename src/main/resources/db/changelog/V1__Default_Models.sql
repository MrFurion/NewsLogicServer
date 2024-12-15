create table News(
                     id uuid PRIMARY KEY,
                     time DATE,
                     text VARCHAR(5000) not null,
                     title varchar(50) not null unique
);

create table Comment(
                        id uuid PRIMARY KEY,
                        time DATE,
                        text VARCHAR(2500) not null unique,
                        username VARCHAR(25) not null unique,
                        news_id uuid REFERENCES news(id) on delete cascade
);