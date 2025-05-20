package org.example.service;

import org.example.model.Paciente;
import org.example.model.PlanoSaude;

public class CalculadoraReembolso {

    private Auditoria auditoria;

    public CalculadoraReembolso() {
        this.auditoria = null;
    }

    public CalculadoraReembolso(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public double calcular(Paciente paciente, double valorConsulta, double percentualReembolso, String observacao) {
        double valorReembolso = valorConsulta * (percentualReembolso / 100);

        if (auditoria != null) {
            auditoria.registrarConsulta(paciente, valorConsulta, valorReembolso, observacao);
        }
        return valorReembolso;
    }

    public double calcularPlanoDeSaude(Paciente paciente, double valorConsulta, PlanoSaude planoSaude, String observacao) {
        double percentualReembolso = planoSaude.getPercentualCobertura();
        double valorReembolso = valorConsulta * (percentualReembolso / 100);

        if (auditoria != null) {
            auditoria.registrarConsulta(paciente, valorConsulta, valorReembolso, observacao);
        }
        return valorReembolso;
    }
}