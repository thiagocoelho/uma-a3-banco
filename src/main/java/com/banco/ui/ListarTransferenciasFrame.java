package com.banco.ui;

import com.banco.model.Transferencia;
import com.banco.service.DenunciaService;
import com.banco.service.TransferenciaService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ListarTransferenciasFrame extends JFrame {

    private TransferenciaService transferenciaService;
    private DenunciaService denunciaService;

    public ListarTransferenciasFrame(int clienteId) {
        transferenciaService = new TransferenciaService();
        denunciaService = new DenunciaService();

        setTitle("Transferências Realizadas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel titleLabel = new JLabel("Transferências Realizadas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Adicionar uma mensagem acima da tabela
        JLabel instructionLabel = new JLabel("Para efetuar uma denúncia, selecione uma transferência e clique no botão DENUNCIAR abaixo.", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Adiciona espaçamento ao rótulo de instruções
        panel.add(instructionLabel, BorderLayout.NORTH);

        List<Transferencia> transferencias = transferenciaService.listarTransferenciasPorCliente(clienteId);
        String[] columnNames = {"ID", "Destino", "Valor", "Tipo", "Detalhes do Destino"};
        Object[][] data = new Object[transferencias.size()][5];

        for (int i = 0; i < transferencias.size(); i++) {
            Transferencia t = transferencias.get(i);
            data[i][0] = t.getId(); // Adiciona o ID da transferência
            data[i][1] = t.getNomeDestinatario(); // Nome do destinatário
            data[i][2] = t.getValor();
            data[i][3] = t.getTipo();
            data[i][4] = t.getDetalhesDestino(); // Detalhes do destino
        }

        JTable table = new JTable(data, columnNames);
        table.getColumnModel().getColumn(0).setMinWidth(0); // Oculta a coluna ID
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Oculta a coluna ID
        table.getColumnModel().getColumn(0).setWidth(0); // Oculta a coluna ID

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Adicionar botão 'Denunciar' abaixo da tabela
        JButton denunciarButton = new JButton("Denunciar");
        denunciarButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int transferenciaId = (int) table.getValueAt(selectedRow, 0); // Usa o ID da transferência
                    denunciarTransferencia(transferenciaId);
                } else {
                    JOptionPane.showMessageDialog(ListarTransferenciasFrame.this, "Por favor, selecione uma transferência para denunciar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        panel.add(denunciarButton, BorderLayout.SOUTH);

        add(panel);
    }

    // Corrigir o método de denúncia para lidar com retorno void
    private void denunciarTransferencia(int transferenciaId) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Por favor, insira o motivo da denúncia:");
        JTextField motivoField = new JTextField();
        panel.add(label, BorderLayout.NORTH);
        panel.add(motivoField, BorderLayout.CENTER);

        int confirm = JOptionPane.showConfirmDialog(this, panel, "Confirmar Denúncia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.OK_OPTION) {
            String motivo = motivoField.getText().trim();
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O motivo da denúncia não pode estar vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    denunciaService.registrarDenuncia(transferenciaId, motivo);
                    JOptionPane.showMessageDialog(this, "Transferência denunciada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, "Denúncia já efetuada para esta transação!", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
}
