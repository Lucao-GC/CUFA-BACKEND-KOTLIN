-- A busca por vagas próximas ignora empresas sem coordenadas.
-- Preenche (uma vez) com ponto de referência em SP para empresas que já têm publicação e lat/lon nulos.
-- Ajuste manualmente em produção se necessário.

UPDATE cadastro_empresa e
INNER JOIN publicacao p ON p.id_empresa = e.id_empresa
SET e.latitude = -23.5505,
    e.longitude = -46.6333
WHERE e.latitude IS NULL
   OR e.longitude IS NULL;
