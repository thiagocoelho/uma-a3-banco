package com.banco.ui;

import com.banco.service.TransferenciaService;

import javax.swing.*;
import java.awt.*;

public class TransferenciaPixFrame extends JFrame {

    private JTextField chavePixField;
    private JTextField valorField;
    private TransferenciaService transferenciaService;
    private int contaOrigemId;

    public TransferenciaPixFrame(int contaOrigemId) {
        this.contaOrigemId = contaOrigemId;
        transferenciaService = new TransferenciaService();

        setTitle("Transferência via PIX");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel chavePixLabel = new JLabel("Chave PIX do Destinatário:");
        chavePixLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        chavePixField = new JTextField();

        JLabel valorLabel = new JLabel("Valor:");
        valorLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        valorField = new JTextField();

        JButton transferirButton = new JButton("Transferir");
        transferirButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                realizarTransferencia();
            }
        });

        panel.add(chavePixLabel);
        panel.add(chavePixField);
        panel.add(valorLabel);
        panel.add(valorField);
        panel.add(new JLabel()); // Placeholder
        panel.add(transferirButton);

        add(panel);
    }

    private void realizarTransferencia() {
        String chavePix = chavePixField.getText();
        String valorText = valorField.getText();

        if (chavePix.isEmpty() || valorText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double valor = Double.parseDouble(valorText);
            boolean ignorarScore = false;
            try {
                transferenciaService.transferirViaPix(chavePix, contaOrigemId, valor, ignorarScore);
            } catch (IllegalArgumentException ex) {
                if (ex.getMessage().contains("Deseja continuar?")) {
                    int escolha = JOptionPane.showConfirmDialog(this, ex.getMessage(), "Aviso", JOptionPane.YES_NO_OPTION);
                    if (escolha == JOptionPane.YES_OPTION) {
                        ignorarScore = true;
                        transferenciaService.transferirViaPix(chavePix, contaOrigemId, valor, ignorarScore);
                    } else {
                        return;
                    }
                } else {
                    throw ex;
                }
            }
            JOptionPane.showMessageDialog(this, "Transferência realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
