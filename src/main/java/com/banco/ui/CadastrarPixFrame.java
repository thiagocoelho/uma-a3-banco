package com.banco.ui;

import com.banco.service.PixService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastrarPixFrame extends JFrame {

    private JTextField chavePixField;
    private PixService pixService;
    private int contaId;

    public CadastrarPixFrame(int contaId) {
        this.contaId = contaId;
        pixService = new PixService();

        setTitle("Cadastrar Chave PIX");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel chavePixLabel = new JLabel("Chave PIX:");
        chavePixLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        chavePixField = new JTextField();

        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarChavePix();
            }
        });

        panel.add(chavePixLabel);
        panel.add(chavePixField);
        panel.add(new JLabel()); // Placeholder
        panel.add(cadastrarButton);

        add(panel);
    }

    private void cadastrarChavePix() {
        String chavePix = chavePixField.getText();

        if (chavePix.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A chave PIX não pode estar vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            pixService.cadastrarChavePix(contaId, chavePix);
            JOptionPane.showMessageDialog(this, "Chave PIX cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
