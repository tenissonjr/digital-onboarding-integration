-- Drop tables if they exist
DROP TABLE IF EXISTS validation_error;
DROP TABLE IF EXISTS fetch_attempt;
DROP TABLE IF EXISTS onboarding_data;

-- Create onboarding_data table
CREATE TABLE onboarding_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hash VARCHAR(64) UNIQUE NOT NULL,
    cpf VARCHAR(14),
    nome VARCHAR(200),
    nome_social VARCHAR(200),
    data_nascimento DATE,
    nome_mae VARCHAR(200),
    numero_documento VARCHAR(30),
    pais_origem VARCHAR(100),
    orgao_emissor VARCHAR(10),
    uf VARCHAR(2),
    data_expedicao DATE,
    data_vencimento DATE,
    status VARCHAR(20) NOT NULL,
    data_recebimento TIMESTAMP NOT NULL,
    data_processamento TIMESTAMP,
    data_persistencia TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100)
);

-- Create validation_error table
CREATE TABLE validation_error (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    onboarding_data_id BIGINT NOT NULL,
    campo VARCHAR(100) NOT NULL,
    mensagem VARCHAR(500) NOT NULL,
    data_registro TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    FOREIGN KEY (onboarding_data_id) REFERENCES onboarding_data(id)
);

-- Create fetch_attempt table
CREATE TABLE fetch_attempt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hash VARCHAR(64) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(10) NOT NULL,
    mensagem_erro VARCHAR(1000),
    tentativa INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100)
);

-- Add indexes
CREATE INDEX idx_onboarding_data_hash ON onboarding_data(hash);
CREATE INDEX idx_fetch_attempt_hash ON fetch_attempt(hash);
CREATE INDEX idx_validation_error_onboarding_data_id ON validation_error(onboarding_data_id);

-- Comments
COMMENT ON TABLE onboarding_data IS 'Stores onboarding data received from external systems';
COMMENT ON TABLE validation_error IS 'Stores validation errors for onboarding data';
COMMENT ON TABLE fetch_attempt IS 'Stores fetch attempts for onboarding data';

-- Created by tenissonjr on 2025-04-14 00:55:34