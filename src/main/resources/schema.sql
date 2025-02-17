CREATE TABLE IF NOT EXISTS user
(
    id         INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    first_name VARCHAR(255)       NOT NULL,
    last_name  VARCHAR(255)       NOT NULL,
    email      VARCHAR(255)       NOT NULL UNIQUE,
    adress     VARCHAR(255)       NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    balance    DECIMAL(10, 2)     NOT NULL DEFAULT 100.00
);

CREATE TABLE IF NOT EXISTS user_credentials
(
    id         INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    username   VARCHAR(255)       NOT NULL UNIQUE,
    password   VARCHAR(255)       NOT NULL,
    user_id    INT                NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS transactions
(
    id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    sender_id   INT                NOT NULL,
    receiver_id INT                NOT NULL,
    amount      DECIMAL(10, 2)     NOT NULL,
    timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES user (id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES user (id)
);
