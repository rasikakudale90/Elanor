-- =========================================================
-- ÉLANOR PLATFORM MIGRATION V3 - MARKETING CONSENT & UNSUBSCRIBE STATUS
-- =========================================================

ALTER TABLE users 
ADD COLUMN IF NOT EXISTS marketing_consent BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS marketing_consent_updated_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS unsubscribe_status BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_users_marketing_consent ON users(marketing_consent, unsubscribe_status);
