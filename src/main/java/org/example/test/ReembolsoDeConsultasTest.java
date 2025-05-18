package org.example.test;

import org.example.service.ReembolsoDeConsultas;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReembolsoDeConsultasTest {


    @Test
    public void calcularReembolsoDeConsultaCom70PorCentoDeCobertura() {
        ReembolsoDeConsultas reembolso = new ReembolsoDeConsultas();
        double resultado = reembolso.calcular(200.0, 70.0);
        double esperado = 140.0;
        assertEquals(esperado, resultado, 0.01);

    }
}
