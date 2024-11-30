CREATE TABLE payment_method (
    id UUID PRIMARY KEY,
    payment_method_type VARCHAR(255),
    price_modifier_min DECIMAL(19, 2),
    price_modifier_max DECIMAL(19, 2),
    points_multiplier DECIMAL(19, 2)
);

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO payment_method (id, payment_method_type, price_modifier_min, price_modifier_max, points_multiplier) VALUES
    (gen_random_uuid(), 'CASH', 0.9, 1, 0.05),
    (gen_random_uuid(), 'CASH_ON_DELIVERY', 1, 1.02, 0.05),
    (gen_random_uuid(), 'VISA', 0.95, 1, 0.03),
    (gen_random_uuid(), 'MASTERCARD', 0.95, 1, 0.03),
    (gen_random_uuid(), 'AMEX', 0.98, 1.01, 0.02),
    (gen_random_uuid(), 'JCB', 0.95, 1, 0.05),
    (gen_random_uuid(), 'LINE_PAY', 1, 1, 0.01),
    (gen_random_uuid(), 'PAYPAY', 1, 1, 0.01),
    (gen_random_uuid(), 'POINTS', 1, 1, 0),
    (gen_random_uuid(), 'GRAB_PAY', 1, 1, 0.01),
    (gen_random_uuid(), 'BANK_TRANSFER', 1, 1, 0),
    (gen_random_uuid(), 'CHEQUE', 0.9, 1, 0);



