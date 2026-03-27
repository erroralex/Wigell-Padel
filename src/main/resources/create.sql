DROP
    DATABASE IF EXISTS wigell_padel_db;
CREATE
    DATABASE wigell_padel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE
    wigell_padel_db;

CREATE TABLE address
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    street_name VARCHAR(100) NOT NULL,
    city        VARCHAR(50)  NOT NULL,
    zip_code    VARCHAR(10)  NOT NULL,
    country     VARCHAR(50)  NOT NULL
);

CREATE TABLE customer
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    role       VARCHAR(50)  NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    address_id BIGINT       NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address (id)
);

CREATE TABLE court
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(50)    NOT NULL UNIQUE,
    description        VARCHAR(255),
    is_indoor          BOOLEAN        NOT NULL DEFAULT TRUE,
    price_per_hour_sek DECIMAL(10, 2) NOT NULL
);

CREATE TABLE booking
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_date    DATE           NOT NULL,
    start_time      TIME           NOT NULL,
    players         INT            NOT NULL CHECK (players BETWEEN 1 AND 4),
    total_price_sek DECIMAL(10, 2) NOT NULL,
    total_price_eur DECIMAL(10, 2),
    customer_id     BIGINT         NOT NULL,
    court_id        BIGINT         NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer (id),
    FOREIGN KEY (court_id) REFERENCES court (id)
);

INSERT INTO address (street_name, city, zip_code, country)
VALUES ('Padelgatan 1', 'Stockholm', '11122', 'Sverige'),
       ('Tennisvägen 5', 'Göteborg', '41233', 'Sverige'),
       ('Sportgränd 9', 'Malmö', '21144', 'Sverige');

INSERT INTO customer (username, role, first_name, last_name, address_id)
VALUES ('admin', 'ROLE_ADMIN', 'Boss', 'Wigell', 1),
       ('anna99', 'ROLE_USER', 'Anna', 'Andersson', 1),
       ('bjorn_p', 'ROLE_USER', 'Björn', 'Borg', 2),
       ('cecilia', 'ROLE_USER', 'Cecilia', 'Lind', 3),
       ('david_d', 'ROLE_USER', 'David', 'Dalin', 2);

INSERT INTO court (name, description, is_indoor, price_per_hour_sek)
VALUES ('Court 1 - Panorama', 'Inomhusbana med panoramaglas', TRUE, 400.00),
       ('Court 2 - Standard', 'Standard inomhusbana', TRUE, 350.00),
       ('Court 3 - Standard', 'Standard inomhusbana', TRUE, 350.00),
       ('Court 4 - Outdoor', 'Utomhusbana utan tak', FALSE, 250.00),
       ('Court 5 - Singel', 'Mindre bana för singelspel (2 pers)', TRUE, 300.00);

INSERT INTO booking (booking_date, start_time, players, total_price_sek, total_price_eur, customer_id, court_id)
VALUES ('2026-04-14', '18:00:00', 4, 400.00, 36.50, 2, 1);

INSERT INTO booking (booking_date, start_time, players, total_price_sek, total_price_eur, customer_id, court_id)
VALUES ('2026-04-15', '10:00:00', 4, 250.00, 22.80, 3, 4);