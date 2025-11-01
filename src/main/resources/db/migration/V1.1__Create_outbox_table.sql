-- Migration V1.1: Create Outbox Table
-- Description: Creates the outbox table for transactional outbox pattern
-- Author: @MelSardes
-- Date: 2025-10-17
-- Version: 1.0.0

CREATE TABLE IF NOT EXISTS outbox(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP WITH TIME ZONE,
    error TEXT
);

-- Add indexes for better query performance
CREATE INDEX idx_outbox_unprocessed ON outbox(created_at) WHERE processed = FALSE;
CREATE INDEX idx_outbox_aggregate ON outbox(aggregate_id, aggregate_type);
