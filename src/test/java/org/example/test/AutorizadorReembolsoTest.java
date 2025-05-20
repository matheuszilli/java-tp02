package org.example.test;

import org.example.exception.ReembolsoNaoAutorizadoException;
import org.example.model.Paciente;
import org.example.service.AutorizadorReembolso;
import org.example.service.CalculadoraReembolso;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public class AutorizadorReembolsoTest {

    @Test
    public void deveCalcularReembolsoQuandoAutorizado() {
        Paciente paciente = new Paciente("Paciente Teste", "123.456.789-00", "teste@email.com");

        AutorizadorReembolso autorizadorMock = Mockito.mock(AutorizadorReembolso.class);

        when(autorizadorMock.autorizar(any(Paciente.class), anyDouble(), anyDouble()))
                .thenReturn(true);

        CalculadoraReembolso calculadora = new CalculadoraReembolso(autorizadorMock);

        double resultado = calculadora.calcular(paciente, 100.0, 50.0, "Consulta de rotina");

        assertEquals(50.0, resultado, 0.01);
    }

    @Test(expected = ReembolsoNaoAutorizadoException.class)
    public void deveLancarExcecaoQuandoNaoAutorizado() {
        Paciente paciente = new Paciente("Paciente Teste", "123.456.789-00", "teste@email.com");

        AutorizadorReembolso autorizadorMock = Mockito.mock(AutorizadorReembolso.class);

        when(autorizadorMock.autorizar(any(Paciente.class), anyDouble(), anyDouble()))
                .thenReturn(false);

        CalculadoraReembolso calculadora = new CalculadoraReembolso(autorizadorMock);

        calculadora.calcular(paciente, 100.0, 50.0, "Consulta de rotina");
    }
}