package org.example.service;

import org.example.model.Paciente;
import org.example.model.PlanoSaude;

public class CalculadoraReembolso {
    public double calcular(Paciente paciente, double valorConsulta, double percentualReembolso) {
        return valorConsulta * (percentualReembolso / 100);
    }

    public double calcularPlanoDeSaude(Paciente paciente, double valorConsulta, PlanoSaude planoSaude) {
        double percentualReembolso = planoSaude.getPercentualCobertura();
        return valorConsulta * (percentualReembolso / 100);
    }
}
