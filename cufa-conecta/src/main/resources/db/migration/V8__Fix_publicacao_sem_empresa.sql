-- Associa publicações órfãs à primeira empresa cadastrada (evita app sem id_empresa na listagem).
UPDATE publicacao p
SET p.id_empresa = (SELECT MIN(e.id_empresa) FROM cadastro_empresa e)
WHERE p.id_empresa IS NULL
  AND EXISTS (SELECT 1 FROM cadastro_empresa);
