package com.banco.service;

import com.banco.model.Conta;
import com.banco.model.ContaPF;
import com.banco.model.ContaPJ;
import com.banco.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContaService {

    public String cadastrarContaPF(String nome, String cpf, String senha) {
        String numeroConta = gerarNumeroConta();
        String sql = "INSERT INTO contas (tipo, nome, cpf_cnpj, score, agencia, numero_conta, saldo, senha) VALUES ('PF', ?, ?, 60, '0001', ?, 0.00, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, cpf);
            statement.setString(3, numeroConta);
            statement.setString(4, senha);
            statement.executeUpdate();
            System.out.println("Conta PF cadastrada com sucesso: " + nome);
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return "Erro: CPF já cadastrado.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao cadastrar conta.";
        }
        return numeroConta;
    }

    public String cadastrarContaPJ(String nome, String cnpj, String senha) {
        String numeroConta = gerarNumeroConta();
        String sql = "INSERT INTO contas (tipo, nome, cpf_cnpj, score, agencia, numero_conta, saldo, senha) VALUES ('PJ', ?, ?, 60, '0001', ?, 0.00, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, cnpj);
            statement.setString(3, numeroConta);
            statement.setString(4, senha);
            statement.executeUpdate();
            System.out.println("Conta PJ cadastrada com sucesso: " + nome);
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return "Erro: CNPJ já cadastrado.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao cadastrar conta.";
        }
        return numeroConta;
    }

    public boolean login(String agencia, String numeroConta, String senha) {
        String sql = "SELECT * FROM contas WHERE agencia = ? AND numero_conta = ? AND senha = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, agencia);
            statement.setString(2, numeroConta);
            statement.setString(3, senha);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Retorna true se encontrar uma conta correspondente
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Conta> listarContas() {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String tipo = resultSet.getString("tipo");
                String nome = resultSet.getString("nome");
                String cpfCnpj = resultSet.getString("cpf_cnpj");
                int score = resultSet.getInt("score");
                String agencia = resultSet.getString("agencia");
                String numeroConta = resultSet.getString("numero_conta");

                Conta conta = tipo.equals("PF") ? new ContaPF(id, nome, cpfCnpj, score, agencia, numeroConta) : new ContaPJ(id, nome, cpfCnpj, score, agencia, numeroConta);
                contas.add(conta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contas;
    }

    public Conta buscarConta(String agencia, String numeroConta) {
        String sql = "SELECT * FROM contas WHERE agencia = ? AND numero_conta = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, agencia);
            statement.setString(2, numeroConta);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String tipo = resultSet.getString("tipo");
                    String nome = resultSet.getString("nome");
                    String cpfCnpj = resultSet.getString("cpf_cnpj");
                    int score = resultSet.getInt("score");
                    double saldo = resultSet.getDouble("saldo");

                    if ("PF".equals(tipo)) {
                        ContaPF contaPF = new ContaPF(resultSet.getInt("id"), nome, cpfCnpj, score, agencia, numeroConta);
                        contaPF.setSaldo(saldo);
                        return contaPF;
                    } else {
                        ContaPJ contaPJ = new ContaPJ(resultSet.getInt("id"), nome, cpfCnpj, score, agencia, numeroConta);
                        contaPJ.setSaldo(saldo);
                        return contaPJ;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean depositarSaldo(String agencia, String numeroConta, double valor) {
        Conta conta = buscarConta(agencia, numeroConta);
        if (conta != null && valor > 0) {
            if (conta instanceof ContaPF) {
                ((ContaPF) conta).depositar(valor);
            } else if (conta instanceof ContaPJ) {
                ((ContaPJ) conta).depositar(valor);
            }
            atualizarSaldoNoBanco(conta);
            return true;
        }
        return false;
    }

    public boolean sacarSaldo(String agencia, String numeroConta, double valor) {
        Conta conta = buscarConta(agencia, numeroConta);
        if (conta != null && valor > 0) {
            boolean sucesso;
            if (conta instanceof ContaPF) {
                sucesso = ((ContaPF) conta).sacar(valor);
            } else {
                sucesso = ((ContaPJ) conta).sacar(valor);
            }
            if (sucesso) {
                atualizarSaldoNoBanco(conta);
                return true;
            }
        }
        return false;
    }

    private void atualizarSaldoNoBanco(Conta conta) {
        String sql = "UPDATE contas SET saldo = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, conta.getSaldo());
            statement.setInt(2, conta.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String gerarNumeroConta() {
        String sql = "SELECT MAX(CAST(numero_conta AS UNSIGNED)) AS max_numero FROM contas";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int maxNumero = resultSet.getInt("max_numero");
                return String.format("%06d", maxNumero + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "000001"; // Retorna o número inicial se não houver contas
    }
}
