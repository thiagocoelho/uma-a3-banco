package com.banco.ui;

import com.banco.model.Conta;
import com.banco.service.ContaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField agenciaField;
    private JTextField numeroContaField;
    private JPasswordField senhaField;
    private ContaService contaService;

    public LoginFrame() {
        contaService = new ContaService();

        setTitle("Login - Banco");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel agenciaLabel = new JLabel("Agência:");
        agenciaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        agenciaField = new JTextField("0001");
        agenciaField.setEditable(false);

        JLabel numeroContaLabel = new JLabel("Número da Conta:");
        numeroContaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo
        numeroContaField = new JTextField();

        JLabel senhaLabel = new JLabel("Senha:");
        senhaField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginActionListener());

        JButton criarContaButton = new JButton("Criar Nova Conta");
        criarContaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CriarContaFrame criarContaFrame = new CriarContaFrame();
                criarContaFrame.setVisible(true);
            }
        });

        panel.add(agenciaLabel);
        panel.add(agenciaField);
        panel.add(numeroContaLabel);
        panel.add(numeroContaField);
        panel.add(senhaLabel);
        panel.add(senhaField);
        panel.add(loginButton);
        panel.add(criarContaButton);

        add(panel);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String agencia = agenciaField.getText();
            String numeroConta = numeroContaField.getText();
            String senha = new String(senhaField.getPassword());

            boolean isAuthenticated = contaService.login(agencia, numeroConta, senha);

            if (isAuthenticated) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                Conta conta = contaService.buscarConta(agencia, numeroConta);
                if (conta != null) {
                    MainFrame mainFrame = new MainFrame(conta);
                    mainFrame.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Erro ao carregar os dados da conta.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this, "Credenciais inválidas!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
