package com.banco.service;

import com.banco.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PixService {

    public void cadastrarChavePix(int contaId, String chave) {
        String sql = "INSERT INTO chaves_pix (conta_id, chave) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, contaId);
            statement.setString(2, chave);
            statement.executeUpdate();
            System.out.println("Chave PIX cadastrada com sucesso para a conta ID: " + contaId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listarChavesPix(int contaId) {
        String sql = "SELECT chave FROM chaves_pix WHERE conta_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, contaId);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Chaves PIX para a conta ID: " + contaId);
            while (resultSet.next()) {
                System.out.println("- " + resultSet.getString("chave"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
