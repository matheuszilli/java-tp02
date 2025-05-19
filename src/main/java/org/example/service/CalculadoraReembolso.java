package org.example.service;

import org.example.model.Paciente;

public class CalculadoraReembolso {
    public double calcular(Paciente paciente, double valorConsulta, double percentualReembolso) {
        return valorConsulta * (percentualReembolso / 100);
    }
}
