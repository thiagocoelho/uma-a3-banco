package com.banco.model;

import java.util.Date;

public class Transferencia {
    private int id;
    private String origem;
    private String destino;
    private double valor;
    private Date data;
    private String tipo;
    private String nomeDestinatario;
    private String detalhesDestino;

    public Transferencia(int id, String origem, String destino, double valor, Date data, String tipo) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
    }

    public Transferencia(int id, String nomeDestinatario, String detalhesDestino, double valor, String tipo) {
        this.id = id;
        this.nomeDestinatario = nomeDestinatario;
        this.detalhesDestino = detalhesDestino;
        this.valor = valor;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public double getValor() {
        return valor;
    }

    public Date getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public String getDetalhesDestino() {
        return detalhesDestino;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
