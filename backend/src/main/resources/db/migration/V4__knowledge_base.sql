-- =========================================================
-- ÉLANOR RAG KNOWLEDGE BASE SCHEMA V4
-- =========================================================

CREATE TABLE IF NOT EXISTS knowledge_chunks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_type VARCHAR(50) NOT NULL,
    source_id VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    metadata_json TEXT,
    embedding_json TEXT,
    content_hash VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_knowledge_source ON knowledge_chunks(source_type, source_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_hash ON knowledge_chunks(content_hash);
