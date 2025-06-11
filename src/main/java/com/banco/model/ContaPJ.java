package com.banco.model;

public class ContaPJ extends Conta {

    private double saldo;

    public ContaPJ(int id, String nome, String cnpj, int score, String agencia, String numeroConta) {
        super(id, nome, cnpj, score, agencia, numeroConta);
    }

    @Override
    public String getTipo() {
        return "PJ";
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
