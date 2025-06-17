package com.banco.ui;

import com.banco.model.Transferencia;
import com.banco.service.DenunciaService;
import com.banco.service.TransferenciaService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListarTransferenciasFrame extends JFrame {

    private TransferenciaService transferenciaService;
    private DenunciaService denunciaService;

    public ListarTransferenciasFrame(int clienteId) {
        transferenciaService = new TransferenciaService();
        denunciaService = new DenunciaService();

        setTitle("Transferências Realizadas");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona padding ao painel principal

        JLabel titleLabel = new JLabel("Transferências Realizadas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Adicionar uma mensagem acima da tabela
        // Destacar a mensagem sobre denúncias
        JLabel instructionLabel = new JLabel("Para efetuar uma denúncia, selecione uma transferência e clique no botão DENUNCIAR abaixo.", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Aumenta o tamanho e deixa em negrito
        instructionLabel.setForeground(Color.BLUE); // Define a cor do texto como azul
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Adiciona espaçamento ao redor da mensagem
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

        // Pré-processar transferências denunciadas para melhorar o desempenho
        Set<Integer> transferenciasDenunciadas = new HashSet<>();
        for (Transferencia t : transferencias) {
            if (denunciaService.isDenunciada(t.getId())) {
                transferenciasDenunciadas.add(t.getId());
            }
        }

        JTable table = new JTable(data, columnNames);
        table.getColumnModel().getColumn(0).setMinWidth(0); // Oculta a coluna ID
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Oculta a coluna ID
        table.getColumnModel().getColumn(0).setWidth(0); // Oculta a coluna ID

        // Adiciona um TableCellRenderer para destacar linhas denunciadas
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int transferenciaId = (int) table.getValueAt(row, 0); // Obtém o ID da transferência
                if (transferenciasDenunciadas.contains(transferenciaId)) { // Verifica se a transferência foi denunciada
                    cell.setBackground(Color.RED);
                    cell.setForeground(Color.WHITE);
                } else {
                    cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    cell.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return cell;
            }
        });

        // Adicionar espaçamento entre a tabela e o botão
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Espaçamento abaixo da tabela
        panel.add(tablePanel, BorderLayout.CENTER);

        // Centralizar o botão 'Denunciar' abaixo da tabela
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton denunciarButton = new JButton("Denunciar");
        denunciarButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int transferenciaId = (int) table.getValueAt(selectedRow, 0); // Usa o ID da transferência

                    // Verificar se a transferência já foi denunciada
                    if (denunciaService.isDenunciada(transferenciaId)) {
                        JOptionPane.showMessageDialog(ListarTransferenciasFrame.this, "Denúncia já efetuada para esta transação!", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return; // Interrompe o fluxo para evitar nova denúncia
                    }

                    denunciarTransferencia(transferenciaId);
                } else {
                    JOptionPane.showMessageDialog(ListarTransferenciasFrame.this, "Por favor, selecione uma transferência para denunciar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(denunciarButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

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
