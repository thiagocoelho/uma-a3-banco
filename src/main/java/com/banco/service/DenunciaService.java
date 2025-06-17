package com.banco.service;

import com.banco.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DenunciaService {

    public void registrarDenuncia(int transferenciaId, String motivo) {
        String obterContaDestinoSql = "SELECT conta_destino_id FROM transferencias WHERE id = ?";
        String registrarDenunciaSql = "INSERT INTO denuncias (transferencia_id, motivo) VALUES (?, ?)";
        String atualizarScoreSql = "UPDATE contas SET score = score - 5 WHERE id = ?";
        String verificarDenunciaSql = "SELECT COUNT(*) FROM denuncias WHERE transferencia_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            int contaDestinoId;

            // Verificar se já existe uma denúncia para a transferência
            try (PreparedStatement verificarDenunciaStmt = connection.prepareStatement(verificarDenunciaSql)) {
                verificarDenunciaStmt.setInt(1, transferenciaId);
                ResultSet resultSet = verificarDenunciaStmt.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    throw new IllegalStateException("Denúncia já registrada para esta transferência.");
                }
            }

            // Obter a conta de destino da transferência
            try (PreparedStatement obterContaDestinoStmt = connection.prepareStatement(obterContaDestinoSql)) {
                obterContaDestinoStmt.setInt(1, transferenciaId);
                ResultSet resultSet = obterContaDestinoStmt.executeQuery();

                if (resultSet.next()) {
                    contaDestinoId = resultSet.getInt("conta_destino_id");
                } else {
                    System.out.println("Transferência não encontrada.");
                    return;
                }
            }

            // Registrar a denúncia
            try (PreparedStatement registrarDenunciaStmt = connection.prepareStatement(registrarDenunciaSql)) {
                registrarDenunciaStmt.setInt(1, transferenciaId);
                registrarDenunciaStmt.setString(2, motivo);
                registrarDenunciaStmt.executeUpdate();
                System.out.println("Denúncia registrada com sucesso!");
            }

            // Atualizar o score da conta de destino
            try (PreparedStatement atualizarScoreStmt = connection.prepareStatement(atualizarScoreSql)) {
                atualizarScoreStmt.setInt(1, contaDestinoId);
                atualizarScoreStmt.executeUpdate();
                System.out.println("Score da conta de destino atualizado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void denunciarTransferencia(int transferenciaId) {
        // Reutilizar o método registrarDenuncia para registrar no banco de dados
        String motivo = "Denúncia automática"; // Motivo padrão para denúncias
        registrarDenuncia(transferenciaId, motivo);
    }

    public boolean isDenunciada(int transferenciaId) {
        String verificarDenunciaSql = "SELECT COUNT(*) FROM denuncias WHERE transferencia_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement verificarDenunciaStmt = connection.prepareStatement(verificarDenunciaSql)) {

            verificarDenunciaStmt.setInt(1, transferenciaId);
            ResultSet resultSet = verificarDenunciaStmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
