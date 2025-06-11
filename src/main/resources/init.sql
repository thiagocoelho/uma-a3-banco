-- Script de criação do banco de dados e tabelas
CREATE DATABASE IF NOT EXISTS banco_simulado;
USE banco_simulado;

-- Tabela de contas
CREATE TABLE contas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('PF', 'PJ') NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE,
    score INT DEFAULT 60,
    saldo DECIMAL(10, 2) DEFAULT 0.00,
    agencia VARCHAR(10) NOT NULL,
    numero_conta VARCHAR(20) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Tabela de chaves PIX
CREATE TABLE chaves_pix (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conta_id INT NOT NULL,
    chave VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY (conta_id) REFERENCES contas(id)
);

-- Tabela de transferências
CREATE TABLE transferencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conta_origem_id INT NOT NULL,
    conta_destino_id INT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data_transferencia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(10) NOT NULL,
    FOREIGN KEY (conta_origem_id) REFERENCES contas(id),
    FOREIGN KEY (conta_destino_id) REFERENCES contas(id)
);

-- Tabela de denúncias
CREATE TABLE denuncias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transferencia_id INT NOT NULL,
    motivo TEXT NOT NULL,
    data_denuncia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transferencia_id) REFERENCES transferencias(id)
);

-- Inserção de dados iniciais
INSERT INTO contas (tipo, nome, cpf_cnpj, score, saldo, agencia, numero_conta, senha) VALUES
('PF', 'João Silva', '12345678900', 60, 100, '0001', '123456', 'teste'),
('PJ', 'Empresa XYZ', '12345678000199', 60, 100, '0001', '987654', 'teste'),
('PF', 'Thiago', '33333333366', 60, 100, '0001', '987655', 'teste');
