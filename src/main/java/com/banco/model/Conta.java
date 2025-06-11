package com.banco.model;

public abstract class Conta {
    private int id;
    private String nome;
    private String cpfCnpj;
    private int score;
    private String agencia;
    private String numeroConta;

    public Conta(int id, String nome, String cpfCnpj, int score, String agencia, String numeroConta) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.score = score;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public int getScore() {
        return score;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void reduzirScore(int pontos) {
        this.score = Math.max(0, this.score - pontos);
    }

    public abstract String getTipo();
    public abstract double getSaldo();
}
