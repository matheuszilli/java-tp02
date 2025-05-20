package org.example.model;

public class Consulta {
    private Paciente paciente;
    private double valor;
    private double percentualReembolso;
    private String observacao;

    public Consulta(Paciente paciente, double valor, double percentualReembolso, String observacao) {
        this.paciente = paciente;
        this.valor = valor;
        this.percentualReembolso = percentualReembolso;
        this.observacao = observacao;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public double getValor() {
        return valor;
    }

    public double getPercentualReembolso() {
        return percentualReembolso;
    }

    public String getObservacao() {
        return observacao;
    }
}