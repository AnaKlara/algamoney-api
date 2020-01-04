CREATE TABLE IF NOT EXISTS pessoa (
  codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(50) NOT NULL,
  ativo BOOLEAN NOT NULL,
  logradouro VARCHAR(50) NULL,
  numero INT NULL,
  complemento VARCHAR(50) NULL,
  bairro VARCHAR(50) NULL,
  cep VARCHAR(20) NULL,
  cidade VARCHAR(20) NULL,
  estado VARCHAR(20) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO pessoa (nome,ativo,logradouro,numero,complemento,bairro,cep,cidade,estado) VALUES ("Ana Clara Correa da Silva",TRUE,"Rua Antônio de Oliveira Carvalho",24,"Portão de Grade","Cabuçu","26291455","Nova Iguaçu","Rio de Janeiro");
