CREATE TABLE account_category_lock (
    account_from VARCHAR(10) NOT NULL,
    category     VARCHAR(16) NOT NULL,
    PRIMARY KEY (account_from, category)
);

CREATE TABLE expense_limit (
    id            BIGSERIAL PRIMARY KEY,
    account_from  VARCHAR(10)  NOT NULL,
    category      VARCHAR(16)  NOT NULL,
    limit_sum     NUMERIC(19, 2) NOT NULL,
    currency      VARCHAR(3)   NOT NULL DEFAULT 'USD',
    effective_at  TIMESTAMPTZ  NOT NULL,
    version_no    INTEGER      NOT NULL,
    CONSTRAINT chk_expense_limit_sum CHECK (limit_sum > 0)
);

CREATE UNIQUE INDEX uq_expense_limit_version
    ON expense_limit (account_from, category, effective_at, version_no);

CREATE INDEX idx_expense_limit_lookup
    ON expense_limit (account_from, category, effective_at DESC);

CREATE TABLE exchange_rate (
    id             BIGSERIAL PRIMARY KEY,
    provider       VARCHAR(64)     NOT NULL,
    currency       VARCHAR(3)      NOT NULL,
    quote_currency VARCHAR(3)      NOT NULL DEFAULT 'USD',
    rate_date      DATE            NOT NULL,
    close_rate     NUMERIC(20, 10) NOT NULL,
    fetched_at     TIMESTAMPTZ     NOT NULL,
    CONSTRAINT uq_exchange_rate UNIQUE (provider, currency, quote_currency, rate_date)
);

CREATE TABLE expense_transaction (
    id               BIGSERIAL PRIMARY KEY,
    account_from     VARCHAR(10)     NOT NULL,
    account_to       VARCHAR(10)     NOT NULL,
    currency_shortname VARCHAR(3)    NOT NULL,
    original_sum     NUMERIC(19, 2)  NOT NULL,
    category         VARCHAR(16)     NOT NULL,
    occurred_at      TIMESTAMPTZ     NOT NULL,
    original_offset  VARCHAR(18)     NOT NULL,
    received_at      TIMESTAMPTZ     NOT NULL,
    month_start      DATE            NOT NULL,
    sum_usd          NUMERIC(19, 2),
    status           VARCHAR(32)     NOT NULL,
    limit_exceeded   BOOLEAN,
    applied_limit_id BIGINT REFERENCES expense_limit (id),
    limit_sum_usd    NUMERIC(19, 2),
    limit_datetime   TIMESTAMPTZ,
    limit_currency   VARCHAR(3),
    exchange_rate_id BIGINT REFERENCES exchange_rate (id)
);

CREATE INDEX idx_expense_tx_month
    ON expense_transaction (account_from, category, month_start, status);

CREATE INDEX idx_expense_tx_exceeded
    ON expense_transaction (account_from, limit_exceeded);
