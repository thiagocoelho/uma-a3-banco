package com.banco.model;

public class ContaPF extends Conta {

    private double saldo;

    public ContaPF(int id, String nome, String cpf, int score, String agencia, String numeroConta) {
        super(id, nome, cpf, score, agencia, numeroConta);
    }

    @Override
    public String getTipo() {
        return "PF";
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
