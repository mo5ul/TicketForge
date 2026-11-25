CREATE TABLE payments (
    id                  BIGSERIAL PRIMARY KEY,
    seat_id             BIGINT       NOT NULL REFERENCES event_seats(id),
    user_id             BIGINT       NOT NULL REFERENCES users(id),
    amount_paise        BIGINT       NOT NULL CHECK (amount_paise > 0),
    provider_order_id   VARCHAR(100) NOT NULL UNIQUE,
    provider_payment_id VARCHAR(100) UNIQUE,
    status              VARCHAR(20)  NOT NULL DEFAULT 'CREATED',  -- CREATED | PAID | FAILED
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    paid_at             TIMESTAMPTZ
);
CREATE INDEX idx_payments_seat ON payments(seat_id);