CREATE TABLE funcionarios (
      id_funcionario BIGINT PRIMARY KEY AUTO_INCREMENT,
      id_empresa BIGINT,
      nome VARCHAR(45),
      email VARCHAR(225),
      senha VARCHAR(100),
      cargo VARCHAR(40)
);
