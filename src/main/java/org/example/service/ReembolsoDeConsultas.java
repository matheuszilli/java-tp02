package org.example.service;

public class ReembolsoDeConsultas {
    public double calcular(double valorConsulta, double percentualReembolso) {
        return valorConsulta * (percentualReembolso / 100);
    }
}
