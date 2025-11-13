-- Migration V1: Create Partner Insurers Registry Schema
-- Description: Initial schema creation for Partner Insurers Registry Service
-- Author: @MelSardes
-- Date: 2025-10-17
-- Version: 1.0.0

BEGIN;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema for partner insurers service
-- CREATE SCHEMA IF NOT EXISTS partner_insurers_service_db;

-- Create ENUM types for better type safety
CREATE TYPE partner_insurers_status AS ENUM (
    'ONBOARDING',
    'ACTIVE',
    'SUSPENDED',
    'MAINTENANCE',
    'DEACTIVATED'
    );

CREATE TYPE agreement_status AS ENUM (
    'DRAFT',
    'ACTIVE',
    'EXPIRED',
    'TERMINATED'
    );

CREATE TYPE payment_frequency AS ENUM (
    'DAILY',
    'WEEKLY',
    'MONTHLY',
    'QUARTERLY',
    'SEMI_ANNUALLY',
    'ANNUALLY'
    );

-- Table: partner_insurer (main aggregate root)
CREATE TABLE partner_insurers
(
    id                        UUID PRIMARY KEY                  DEFAULT gen_random_uuid(),
    partner_insurer_code      VARCHAR(50)              NOT NULL UNIQUE,
    legal_name                VARCHAR(255)             NOT NULL,
    tax_identification_number VARCHAR(50)              NOT NULL,
    logo_url                  TEXT,
    address                   JSONB,
    status                    VARCHAR(20)  NOT NULL DEFAULT 'ONBOARDING',
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at                TIMESTAMP WITH TIME ZONE,
    deleted_by                UUID
);

-- Table: contact (value object collection)
CREATE TABLE partner_insurer_contacts
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner_insurer_id UUID                     NOT NULL,
    full_name          VARCHAR(255)             NOT NULL,
    email              VARCHAR(255)             NOT NULL,
    phone              VARCHAR(50)              NOT NULL,
    contact_role       VARCHAR(100)             NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at         TIMESTAMP WITH TIME ZONE,
    deleted_by         UUID,
    FOREIGN KEY (partner_insurer_id) REFERENCES partner_insurers (id) ON DELETE CASCADE
);

-- Table: broker_partner_agreement (entity)
CREATE TABLE broker_partner_insurer_agreements
(
    id                        UUID PRIMARY KEY                  DEFAULT gen_random_uuid(),
    partner_insurer_id        UUID                     NOT NULL,
    agreement_code            VARCHAR(100)             NOT NULL UNIQUE,
    agreement_title           VARCHAR(255)             NOT NULL,
    start_date                DATE                     NOT NULL,
    end_date                  DATE,
    status                    agreement_status         NOT NULL DEFAULT 'DRAFT',
    covered_branches          JSONB                    NOT NULL,

    commission_rate           DECIMAL(5, 4),

    -- JSON structure for payment method configuration
    -- The payment_method column stores a hierarchical JSON structure representing different payment methods.
    --
    -- Each payment method has common fields (currency, isAutomatic, requiresApproval) and
    -- specific fields depending on the method type.
    --
    -- Required fields for all types:
    --   - type: string (BankTransfer | MobileMoney | Manual)
    --   - currency: string (default: "XAF")
    --   - isAutomatic: boolean (default: true)
    --   - requiresApproval: boolean (default: false)
    --
    -- This structure maps to the Kotlin sealed class PaymentMethod with three variants:
    --   1. BankTransfer - Traditional bank payment via interbank transfer
    --   2. MobileMoney - Payment via mobile money operators (Airtel, MTN, etc.)
    --   3. Manual - Manual payment by check or cash
    --
    -- Type-specific fields are documented in payment_method_examples.json
    -- See the file: db/migration/examples/payment_method_examples.json for complete examples.
    payment_frequency         payment_frequency        NOT NULL DEFAULT 'MONTHLY',
    payment_method            JSONB                    NOT NULL,
    payment_delay_days        INTEGER                  NOT NULL,
    late_payment_penalty_rate DECIMAL(5, 4),

    document_ref_url          TEXT,
    signed_at                 DATE                     NOT NULL,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at                TIMESTAMP WITH TIME ZONE,
    deleted_by                UUID,
    FOREIGN KEY (partner_insurer_id) REFERENCES partner_insurers (id) ON DELETE CASCADE
);


-- Table: representatives (value object collection)
CREATE TABLE broker_partner_insurer_agreement_representatives
(
    id                                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    broker_partner_insurer_agreement_id UUID                     NOT NULL,
    full_name                           VARCHAR(255)             NOT NULL,
    email                               VARCHAR(255)             NOT NULL,
    phone                               VARCHAR(50)              NOT NULL,
    representative_role                 VARCHAR(100)             NOT NULL,
    created_at                          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                          TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at                          TIMESTAMP WITH TIME ZONE,
    deleted_by                          UUID,
    FOREIGN KEY (broker_partner_insurer_agreement_id) REFERENCES broker_partner_insurer_agreements (id) ON DELETE CASCADE
);

-- Indexes for performance optimization
CREATE INDEX idx_partner_insurer_status ON partner_insurers (status);
CREATE INDEX idx_partner_insurer_partner_insurer_code ON partner_insurers (partner_insurer_code);
CREATE INDEX idx_partner_insurer_legal_name ON partner_insurers (legal_name);
CREATE INDEX idx_partner_insurer_tax_id ON partner_insurers (tax_identification_number);

CREATE INDEX idx_contact_partner_insurer ON partner_insurer_contacts (partner_insurer_id);
CREATE INDEX idx_contact_email ON partner_insurer_contacts (email);

CREATE INDEX idx_representative_agreement ON broker_partner_insurer_agreement_representatives (broker_partner_insurer_agreement_id);
CREATE INDEX idx_representative_email ON broker_partner_insurer_agreement_representatives (email);

CREATE INDEX idx_agreement_partner_insurer ON broker_partner_insurer_agreements (partner_insurer_id);
CREATE INDEX idx_agreement_code ON broker_partner_insurer_agreements (agreement_code);
CREATE INDEX idx_agreement_status ON broker_partner_insurer_agreements (status);
CREATE INDEX idx_agreement_dates ON broker_partner_insurer_agreements (start_date, end_date);
CREATE INDEX idx_payment_method_type ON broker_partner_insurer_agreements USING GIN (payment_method);
CREATE INDEX idx_payment_method_type_field ON broker_partner_insurer_agreements ((payment_method ->> 'type'));

-- Constraints for data integrity
ALTER TABLE partner_insurers
    ADD CONSTRAINT chk_partner_code_not_empty CHECK (LENGTH(TRIM(partner_insurer_code)) > 0);

ALTER TABLE partner_insurers
    ADD CONSTRAINT chk_legal_name_not_empty CHECK (LENGTH(TRIM(legal_name)) > 0);

ALTER TABLE partner_insurer_contacts
    ADD CONSTRAINT chk_phone_not_empty CHECK (LENGTH(TRIM(phone)) > 0);

ALTER TABLE broker_partner_insurer_agreements
    ADD CONSTRAINT chk_end_date_after_start CHECK (end_date IS NULL OR end_date >= start_date);

ALTER TABLE broker_partner_insurer_agreements
    ADD CONSTRAINT chk_commission_rate_valid CHECK (commission_rate IS NULL OR (commission_rate >= 0 AND commission_rate <= 1));

ALTER TABLE broker_partner_insurer_agreements
    ADD CONSTRAINT chk_penalty_rate_valid CHECK (late_payment_penalty_rate IS NULL OR
                                                 (late_payment_penalty_rate >= 0 AND late_payment_penalty_rate <= 1));

ALTER TABLE partner_insurer_contacts
    ADD CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');

ALTER TABLE broker_partner_insurer_agreements
    ADD CONSTRAINT chk_payment_method_structure CHECK (
        jsonb_typeof(payment_method) = 'object' AND
        payment_method ? 'type' AND
        payment_method ? 'currency' AND
        payment_method ? 'isAutomatic' AND
        payment_method ? 'requiresApproval' AND
        payment_method ->> 'type' IN ('BANK_TRANSFER', 'MOBILE_MONEY', 'MANUAL')
        );

ALTER TABLE partner_insurers
    ADD COLUMN search_vector tsvector
        GENERATED ALWAYS AS (
            to_tsvector(
                    'french',
                    COALESCE(legal_name, '') || ' ' ||
                    COALESCE(tax_identification_number, '')
            )
            ) STORED;
CREATE INDEX idx_partner_insurers_search ON partner_insurers USING GIN (search_vector);

COMMENT ON COLUMN broker_partner_insurer_agreements.payment_method IS
    'Stores payment method configuration as JSON. Structure: {
       "type": "BANK_TRANSFER"|"MOBILE_MONEY"|"MANUAL",
       "currency": "string (ISO 4217)",
       "isAutomatic": boolean,
       "requiresApproval": boolean,
       "paymentDelayDays": integer,
       "bankDetails": {
         "bankName": "string",
         "accountNumber": "string",
         "accountName": "string",
         "swiftCode": "string"
       },
       "mobileMoneyDetails": {
         "provider": "string",
         "phoneNumber": "string"
       },
       "manualDetails": {
         "instructions": "string"
       }
     }';

COMMIT;