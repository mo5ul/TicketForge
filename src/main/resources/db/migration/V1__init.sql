CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE events (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    venue      VARCHAR(200) NOT NULL,
    starts_at  TIMESTAMPTZ  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE event_seats (
    id              BIGSERIAL PRIMARY KEY,
    event_id        BIGINT      NOT NULL REFERENCES events(id),
    row_label       VARCHAR(5)  NOT NULL,
    seat_number     INT         NOT NULL,
    price_paise     BIGINT      NOT NULL CHECK (price_paise > 0),
    status          VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    held_by         BIGINT REFERENCES users(id),
    hold_expires_at TIMESTAMPTZ,
    version         BIGINT      NOT NULL DEFAULT 0,
    UNIQUE (event_id, row_label, seat_number)
);

CREATE INDEX idx_event_seats_event_status ON event_seats(event_id, status);
CREATE INDEX idx_event_seats_hold_expiry ON event_seats(hold_expires_at) WHERE status = 'HELD';