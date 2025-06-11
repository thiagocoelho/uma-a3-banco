package com.banco.service;

import com.banco.model.Transferencia;
import com.banco.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransferenciaService {

    // Consolidated method for updating balances and verifying score
    private boolean verificarScoreEAtualizarSaldo(Connection connection, int contaOrigemId, int contaDestinoId, double valor, boolean ignorarScore) throws SQLException {
        String verificarScoreSql = "SELECT score FROM contas WHERE id = ?";
        String verificarSaldoOrigemSql = "SELECT saldo FROM contas WHERE id = ?";
        String atualizarSaldoOrigemSql = "UPDATE contas SET saldo = saldo - ? WHERE id = ?";
        String atualizarSaldoDestinoSql = "UPDATE contas SET saldo = saldo + ? WHERE id = ?";

        // Verificar o score da conta de destino
        if (!ignorarScore) {
            System.out.println("VERIFICANDO SCORE");
            try (PreparedStatement verificarScoreStmt = connection.prepareStatement(verificarScoreSql)) {
                verificarScoreStmt.setInt(1, contaDestinoId);
                ResultSet resultSet = verificarScoreStmt.executeQuery();

                if (resultSet.next()) {
                    int score = resultSet.getInt("score");
                    System.out.println("SCORE CONTA DESTINO: " + score);
                    if (score <= 50) {
                        throw new IllegalArgumentException("A conta de destino não é segura. Deseja continuar?");
                    }
                } else {
                    throw new IllegalArgumentException("Conta de destino não encontrada.");
                }
            }
        }

        // Verificar o saldo da conta de origem
        try (PreparedStatement verificarSaldoOrigemStmt = connection.prepareStatement(verificarSaldoOrigemSql)) {
            verificarSaldoOrigemStmt.setInt(1, contaOrigemId);
            ResultSet resultSet = verificarSaldoOrigemStmt.executeQuery();

            if (resultSet.next()) {
                double saldoOrigem = resultSet.getDouble("saldo");
                if (saldoOrigem < valor) {
                    throw new IllegalArgumentException("Saldo insuficiente na conta de origem.");
                }
            } else {
                throw new IllegalArgumentException("Conta de origem não encontrada.");
            }
        }

        // Atualizar o saldo da conta de origem
        try (PreparedStatement atualizarSaldoOrigemStmt = connection.prepareStatement(atualizarSaldoOrigemSql)) {
            atualizarSaldoOrigemStmt.setDouble(1, valor);
            atualizarSaldoOrigemStmt.setInt(2, contaOrigemId);
            atualizarSaldoOrigemStmt.executeUpdate();
        }

        // Atualizar o saldo da conta de destino
        try (PreparedStatement atualizarSaldoDestinoStmt = connection.prepareStatement(atualizarSaldoDestinoSql)) {
            atualizarSaldoDestinoStmt.setDouble(1, valor);
            atualizarSaldoDestinoStmt.setInt(2, contaDestinoId);
            atualizarSaldoDestinoStmt.executeUpdate();
        }

        return true;
    }

    public void transferirViaPix(String chavePix, int contaOrigemId, double valor, boolean ignorarScore) {
        String sqlBuscarContaDestino = "SELECT conta_id FROM chaves_pix WHERE chave = ?";
        String sqlInserirTransferencia = "INSERT INTO transferencias (conta_origem_id, conta_destino_id, valor, tipo) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement buscarContaDestinoStmt = connection.prepareStatement(sqlBuscarContaDestino);
                 PreparedStatement inserirTransferenciaStmt = connection.prepareStatement(sqlInserirTransferencia)) {

                // Buscar conta destino pelo chave PIX
                buscarContaDestinoStmt.setString(1, chavePix);
                try (ResultSet resultSet = buscarContaDestinoStmt.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new IllegalArgumentException("Chave PIX não encontrada.");
                    }
                    int contaDestinoId = resultSet.getInt("conta_id");

                    // Verificar score e atualizar saldo
                    verificarScoreEAtualizarSaldo(connection, contaOrigemId, contaDestinoId, valor, ignorarScore);

                    // Inserir transferência no banco de dados
                    inserirTransferenciaStmt.setInt(1, contaOrigemId);
                    inserirTransferenciaStmt.setInt(2, contaDestinoId);
                    inserirTransferenciaStmt.setDouble(3, valor);
                    inserirTransferenciaStmt.setString(4, "PIX");
                    inserirTransferenciaStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferirViaAgenciaConta(String agencia, String numeroConta, int contaOrigemId, double valor, boolean ignorarScore) {
        System.out.println(agencia + " " + numeroConta + " " + contaOrigemId + " " + valor + " " + ignorarScore);
        String buscarContaDestinoSql = "SELECT id FROM contas WHERE agencia = ? AND numero_conta = ?";
        String registrarTransferenciaSql = "INSERT INTO transferencias (conta_origem_id, conta_destino_id, valor, tipo) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            int contaDestinoId;

            // Buscar a conta de destino
            try (PreparedStatement buscarContaDestinoStmt = connection.prepareStatement(buscarContaDestinoSql)) {
                buscarContaDestinoStmt.setString(1, agencia);
                buscarContaDestinoStmt.setString(2, numeroConta);
                ResultSet resultSet = buscarContaDestinoStmt.executeQuery();

                if (resultSet.next()) {
                    contaDestinoId = resultSet.getInt("id");
                } else {
                    throw new IllegalArgumentException("Conta de destino não encontrada.");
                }
            }

            // Verificar score e atualizar saldo
            verificarScoreEAtualizarSaldo(connection, contaOrigemId, contaDestinoId, valor, ignorarScore);

            // Registrar a transferência
            try (PreparedStatement registrarTransferenciaStmt = connection.prepareStatement(registrarTransferenciaSql)) {
                registrarTransferenciaStmt.setInt(1, contaOrigemId);
                registrarTransferenciaStmt.setInt(2, contaDestinoId);
                registrarTransferenciaStmt.setDouble(3, valor);
                registrarTransferenciaStmt.setString(4, "DOC");
                registrarTransferenciaStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transferencia> listarTransferenciasPorCliente(int clienteId) {
        List<Transferencia> transferencias = new ArrayList<>();
        String sql = "SELECT * FROM transferencias WHERE conta_origem_id = ? OR conta_destino_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clienteId);
            statement.setInt(2, clienteId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String origem = resultSet.getString("conta_origem_id");
                    String destino = resultSet.getString("conta_destino_id");
                    double valor = resultSet.getDouble("valor");
                    Date data = resultSet.getTimestamp("data_transferencia");
                    String tipo = resultSet.getString("tipo");
                    transferencias.add(new Transferencia(id, origem, destino, valor, data, tipo));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transferencias;
    }
}
