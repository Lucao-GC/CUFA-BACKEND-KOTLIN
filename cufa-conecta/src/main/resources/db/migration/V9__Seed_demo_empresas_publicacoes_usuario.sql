-- Dados mínimos para desenvolvimento: empresas com coordenadas, publicações com dt_expiracao futura,
-- usuário demo (senha: password — hash BCrypt compatível com Spring).
-- Só popula quando ainda não existe nenhuma publicação.

INSERT INTO cadastro_empresa (nome, email, senha, cep, endereco, numero, cnpj, area, biografia, dt_cadastro, latitude, longitude)
SELECT 'CUFA Tech Soluções', 'contato@cufa-tech.demo', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '01310100', 'Av. Paulista', 1578, '12.345.678/0001-90', 'Tecnologia', 'Empresa demo — vagas TI.', CURDATE(), -23.561200, -46.655100
FROM DUAL
WHERE (SELECT COUNT(*) FROM publicacao) = 0;

INSERT INTO cadastro_empresa (nome, email, senha, cep, endereco, numero, cnpj, area, biografia, dt_cadastro, latitude, longitude)
SELECT 'Restaurante Bom Sabor', 'rh@bomsabor.demo', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '01310200', 'Rua Augusta', 500, '98.765.432/0001-10', 'Alimentação', 'Empresa demo — operacional.', CURDATE(), -23.548900, -46.638800
FROM DUAL
WHERE (SELECT COUNT(*) FROM publicacao) = 0;

INSERT INTO cadastro_empresa (nome, email, senha, cep, endereco, numero, cnpj, area, biografia, dt_cadastro, latitude, longitude)
SELECT 'Logística Rápida SP', 'vagas@lograpida.demo', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '03115000', 'Rua da Consolação', 2300, '11.222.333/0001-44', 'Logística', 'Empresa demo — logística.', CURDATE(), -23.555000, -46.662000
FROM DUAL
WHERE (SELECT COUNT(*) FROM publicacao) = 0;

INSERT INTO publicacao (id_empresa, titulo, descricao, tipo_contrato, dt_expiracao, dt_publicacao)
SELECT id_empresa, titulo, descricao, tipo_contrato, dt_expiracao, dt_publicacao
FROM (
  SELECT e.id_empresa, 'Desenvolvedor Backend Jr' AS titulo, 'Kotlin, Spring Boot e MySQL. Trabalho híbrido.' AS descricao, 'CLT' AS tipo_contrato, TIMESTAMP '2028-06-30 23:59:59' AS dt_expiracao, NOW() AS dt_publicacao
  FROM cadastro_empresa e WHERE e.email = 'contato@cufa-tech.demo'
  UNION ALL
  SELECT e.id_empresa, 'Analista de QA Pleno', 'Testes de API REST e regressão.', 'CLT', TIMESTAMP '2028-08-15 23:59:59', NOW()
  FROM cadastro_empresa e WHERE e.email = 'contato@cufa-tech.demo'
  UNION ALL
  SELECT e.id_empresa, 'Dev Mobile React Native', 'Expo, TypeScript, integração REST.', 'CLT', TIMESTAMP '2028-12-01 23:59:59', NOW()
  FROM cadastro_empresa e WHERE e.email = 'contato@cufa-tech.demo'
  UNION ALL
  SELECT e.id_empresa, 'Auxiliar de Cozinha', 'Experiência em cozinha industrial.', 'CLT', TIMESTAMP '2028-05-20 23:59:59', NOW()
  FROM cadastro_empresa e WHERE e.email = 'rh@bomsabor.demo'
  UNION ALL
  SELECT e.id_empresa, 'Atendente de Balcão', 'Atendimento e operação de caixa.', 'PJ', TIMESTAMP '2028-07-01 23:59:59', NOW()
  FROM cadastro_empresa e WHERE e.email = 'rh@bomsabor.demo'
  UNION ALL
  SELECT e.id_empresa, 'Motorista entregas moto', 'CNH A. Rotas na capital.', 'PJ', TIMESTAMP '2028-08-09 23:59:59', NOW()
  FROM cadastro_empresa e WHERE e.email = 'vagas@lograpida.demo'
  UNION ALL
  SELECT e.id_empresa, 'Estágio em Logística', 'Cursando administração ou logística.', 'Estagio', TIMESTAMP '2028-04-30 23:59:59', NOW()
  FROM cadastro_empresa e WHERE e.email = 'vagas@lograpida.demo'
) AS vagas
WHERE (SELECT COUNT(*) FROM publicacao) = 0;

INSERT INTO cadastro_usuario (nome, email, senha, cpf, telefone, escolaridade, dt_nascimento, estado_civil, estado, cidade, biografia, curriculo_url, latitude, longitude)
SELECT 'Usuário Demo', 'usuario.demo@cufa.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '52998224725', '11999990000', 'Superior', '1998-03-15', 'Solteiro', 'SP', 'São Paulo', 'Conta criada pela migração V9.', NULL, -23.550520, -46.633308
FROM DUAL
WHERE (SELECT COUNT(*) FROM cadastro_usuario) = 0;
