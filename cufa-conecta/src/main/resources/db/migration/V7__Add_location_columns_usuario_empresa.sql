-- Localização: usuário (PATCH /usuarios/localizacao) e empresa (vagas próximas).
-- Idempotente: não falha se as colunas já existirem (ex.: banco criado pelo script seed ou ALTER manual).

SET @exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cadastro_usuario' AND COLUMN_NAME = 'latitude'
);
SET @stmt := IF(@exists = 0, 'ALTER TABLE cadastro_usuario ADD COLUMN latitude DOUBLE NULL', 'SELECT 1');
PREPARE p FROM @stmt;
EXECUTE p;
DEALLOCATE PREPARE p;

SET @exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cadastro_usuario' AND COLUMN_NAME = 'longitude'
);
SET @stmt := IF(@exists = 0, 'ALTER TABLE cadastro_usuario ADD COLUMN longitude DOUBLE NULL', 'SELECT 1');
PREPARE p FROM @stmt;
EXECUTE p;
DEALLOCATE PREPARE p;

SET @exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cadastro_empresa' AND COLUMN_NAME = 'latitude'
);
SET @stmt := IF(@exists = 0, 'ALTER TABLE cadastro_empresa ADD COLUMN latitude DOUBLE NULL', 'SELECT 1');
PREPARE p FROM @stmt;
EXECUTE p;
DEALLOCATE PREPARE p;

SET @exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cadastro_empresa' AND COLUMN_NAME = 'longitude'
);
SET @stmt := IF(@exists = 0, 'ALTER TABLE cadastro_empresa ADD COLUMN longitude DOUBLE NULL', 'SELECT 1');
PREPARE p FROM @stmt;
EXECUTE p;
DEALLOCATE PREPARE p;
