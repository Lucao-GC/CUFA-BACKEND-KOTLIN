CREATE TABLE candidatura (
     id_candidatura BIGINT PRIMARY KEY AUTO_INCREMENT,
     id_usuario BIGINT,
     id_publicacao BIGINT,
     id_empresa BIGINT,
     dt_candidatura DATE
);