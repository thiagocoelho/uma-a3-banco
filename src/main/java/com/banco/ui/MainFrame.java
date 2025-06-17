package com.banco.ui;

import com.banco.model.Conta;
import com.banco.service.ContaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private Conta conta;
    private ContaService contaService;
    private JLabel saldoLabel; // Mover a declaração da saldoLabel para a classe

    public MainFrame(Conta conta) {
        this.conta = conta;
        this.contaService = new ContaService();

        setTitle("Banco - Tela Principal");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel nomeLabel = new JLabel("Nome: " + conta.getNome());
        nomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo

        JLabel agenciaLabel = new JLabel("Agência: " + conta.getAgencia());
        agenciaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adiciona espaçamento ao rótulo

        JLabel numeroContaLabel = new JLabel("Número da Conta: " + conta.getNumeroConta());
        saldoLabel = new JLabel("Saldo: R$ " + conta.getSaldo()); // Inicializa a saldoLabel

        // Estilizando os rótulos para destaque
        nomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        agenciaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        numeroContaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton cadastrarPixButton = new JButton("Cadastrar Chave PIX");
        cadastrarPixButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CadastrarPixFrame cadastrarPixFrame = new CadastrarPixFrame(conta.getId());
                cadastrarPixFrame.setVisible(true);
            }
        });

        JButton transferenciaPixButton = new JButton("Transferência via PIX");
        transferenciaPixButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransferenciaPixFrame transferenciaPixFrame = new TransferenciaPixFrame(MainFrame.this, conta.getId());
                transferenciaPixFrame.setVisible(true);
            }
        });

        JButton transferenciaAgenciaButton = new JButton("Transferência via Agência/Conta");
        transferenciaAgenciaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TransferenciaAgenciaFrame transferenciaAgenciaFrame = new TransferenciaAgenciaFrame(MainFrame.this, conta.getId());
                transferenciaAgenciaFrame.setVisible(true);
            }
        });

        JButton listarTransferenciasButton = new JButton("Listar Transferências");
        listarTransferenciasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListarTransferenciasFrame listarTransferenciasFrame = new ListarTransferenciasFrame(conta.getId());
                listarTransferenciasFrame.setVisible(true);
            }
        });

        JButton sairButton = new JButton("Sair");
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                dispose();
            }
        });

        JButton depositarButton = new JButton("Depositar");
        depositarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String valorStr = JOptionPane.showInputDialog("Digite o valor para depósito:");
                try {
                    double valor = Double.parseDouble(valorStr);
                    if (contaService.depositarSaldo(conta.getAgencia(), conta.getNumeroConta(), valor)) {
                        JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!");
                        atualizarSaldo(); // Atualiza o saldo após o depósito
                    } else {
                        JOptionPane.showMessageDialog(null, "Falha ao realizar depósito.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Valor inválido.");
                }
            }
        });

        JButton sacarButton = new JButton("Sacar");
        sacarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String valorStr = JOptionPane.showInputDialog("Digite o valor para saque:");
                try {
                    double valor = Double.parseDouble(valorStr);
                    if (contaService.sacarSaldo(conta.getAgencia(), conta.getNumeroConta(), valor)) {
                        JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!");
                        atualizarSaldo(); // Atualiza o saldo após o saque
                    } else {
                        JOptionPane.showMessageDialog(null, "Falha ao realizar saque.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Valor inválido.");
                }
            }
        });

        panel.add(nomeLabel);
        panel.add(agenciaLabel);
        panel.add(numeroContaLabel);
        panel.add(saldoLabel);
        panel.add(cadastrarPixButton);
        panel.add(transferenciaPixButton);
        panel.add(transferenciaAgenciaButton);
        panel.add(listarTransferenciasButton);
        panel.add(depositarButton);
        panel.add(sacarButton);
        panel.add(sairButton);

        add(panel);
    }

    public void atualizarSaldo() {
        conta = contaService.buscarConta(conta.getAgencia(), conta.getNumeroConta());
        saldoLabel.setText("Saldo: R$ " + conta.getSaldo());
    }
}
