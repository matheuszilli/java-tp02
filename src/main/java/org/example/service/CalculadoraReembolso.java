package org.example.service;

public class CalculadoraReembolso {
    public double calcular(double valorConsulta, double percentualReembolso) {
        return valorConsulta * (percentualReembolso / 100);
    }
}
