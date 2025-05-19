package org.example.test;

import org.example.service.CalculadoraReembolso;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReembolsoDeConsultasTest {


    @Test
    public void calcularReembolsoDeConsultaCom70PorCentoDeCobertura() {
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(200.0, 70.0);
        double reembolsoEsperado = 140.00;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularReembolsoDeConsultaCom0PorCentoDeCobertura() {
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(100.0, 0.0);
        double reembolsoEsperado = 0.00;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularReembolsoDeConsultaCom100PorCentoDeCobertura() {
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(100.0, 100.0);
        double reembolsoEsperado = 100.0;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }
}
