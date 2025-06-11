package com.banco.ui;

import com.banco.service.ContaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CriarContaFrame extends JFrame {

    private JTextField nomeField;
    private JTextField cpfCnpjField;
    private JPasswordField senhaField;
    private JComboBox<String> tipoContaCombo;
    private ContaService contaService;

    public CriarContaFrame() {
        contaService = new ContaService();

        setTitle("Criar Nova Conta");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        nomeField = new JTextField();

        JLabel cpfCnpjLabel = new JLabel("CPF/CNPJ:");
        cpfCnpjLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        cpfCnpjField = new JTextField();

        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        senhaField = new JPasswordField();

        JLabel tipoContaLabel = new JLabel("Tipo de Conta:");
        tipoContaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        tipoContaCombo = new JComboBox<>(new String[]{"PF", "PJ"});

        JButton criarButton = new JButton("Criar Conta");
        criarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                criarConta();
            }
        });

        panel.add(nomeLabel);
        panel.add(nomeField);
        panel.add(cpfCnpjLabel);
        panel.add(cpfCnpjField);
        panel.add(senhaLabel);
        panel.add(senhaField);
        panel.add(tipoContaLabel);
        panel.add(tipoContaCombo);
        panel.add(new JLabel()); // Placeholder
        panel.add(criarButton);

        add(panel);
    }

    private void criarConta() {
        String nome = nomeField.getText();
        String cpfCnpj = cpfCnpjField.getText();
        String senha = new String(senhaField.getPassword());
        String tipoConta = (String) tipoContaCombo.getSelectedItem();

        if (nome.isEmpty() || cpfCnpj.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numeroConta;
        if (tipoConta.equals("PF")) {
            numeroConta = contaService.cadastrarContaPF(nome, cpfCnpj, senha);
        } else {
            numeroConta = contaService.cadastrarContaPJ(nome, cpfCnpj, senha);
        }

        JOptionPane.showMessageDialog(this, "Conta criada com sucesso!\nNúmero da Conta: " + numeroConta, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        dispose();
    }
}
