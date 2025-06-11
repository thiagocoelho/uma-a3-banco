package com.banco.ui;

import com.banco.service.TransferenciaService;

import javax.swing.*;
import java.awt.*;

public class TransferenciaAgenciaFrame extends JFrame {

    private JTextField agenciaField;
    private JTextField numeroContaField;
    private JTextField valorField;
    private TransferenciaService transferenciaService;
    private int contaOrigemId;

    public TransferenciaAgenciaFrame(int contaOrigemId) {
        this.contaOrigemId = contaOrigemId;
        transferenciaService = new TransferenciaService();

        setTitle("Transferência via Agência/Conta");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel agenciaLabel = new JLabel("Agência do Destinatário:");
        agenciaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        agenciaField = new JTextField();

        JLabel numeroContaLabel = new JLabel("Número da Conta do Destinatário:");
        numeroContaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        numeroContaField = new JTextField();

        JLabel valorLabel = new JLabel("Valor:");
        valorField = new JTextField();

        JButton transferirButton = new JButton("Transferir");
        transferirButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                realizarTransferencia();
            }
        });

        panel.add(agenciaLabel);
        panel.add(agenciaField);
        panel.add(numeroContaLabel);
        panel.add(numeroContaField);
        panel.add(valorLabel);
        panel.add(valorField);
        panel.add(new JLabel()); // Placeholder
        panel.add(transferirButton);

        add(panel);
    }

    private void realizarTransferencia() {
        String agencia = agenciaField.getText();
        String numeroConta = numeroContaField.getText();
        String valorText = valorField.getText();

        if (agencia.isEmpty() || numeroConta.isEmpty() || valorText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double valor = Double.parseDouble(valorText);
            boolean ignorarScore = false;
            try {
                transferenciaService.transferirViaAgenciaConta(agencia, numeroConta, contaOrigemId, valor, ignorarScore);
            } catch (IllegalArgumentException ex) {
                if (ex.getMessage().contains("Deseja continuar?")) {
                    int escolha = JOptionPane.showConfirmDialog(this, ex.getMessage(), "Aviso", JOptionPane.YES_NO_OPTION);
                    if (escolha == JOptionPane.YES_OPTION) {
                        ignorarScore = true;
                        transferenciaService.transferirViaAgenciaConta(agencia, numeroConta, contaOrigemId, valor, ignorarScore);
                    } else {
                        return;
                    }
                } else {
                    throw ex;
                }
            }
            JOptionPane.showMessageDialog(this, "Transferência realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
