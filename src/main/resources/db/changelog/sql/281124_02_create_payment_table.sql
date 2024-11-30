CREATE TABLE payment (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) UNIQUE,
    payment_method VARCHAR(255),
    price DECIMAL(19, 2),
    price_modifier DECIMAL(19, 2),
    final_price DECIMAL(19, 2),
    points DECIMAL(19, 2),
    datetime TIMESTAMP,
    additional_item JSONB
);

CREATE INDEX idx_payment_datetime ON payment (datetime);
