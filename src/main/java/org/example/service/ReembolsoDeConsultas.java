package org.example.service;
import java.util.Scanner;

public class ReembolsoDeConsultas {
    public double calcular(double valorConsulta, double percentualReembolso) {
        return valorConsulta * (percentualReembolso / 100);
    }
}
