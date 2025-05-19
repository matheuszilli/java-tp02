package org.example.test;

import org.example.model.Paciente;
import org.example.service.CalculadoraReembolso;
import org.example.repository.HistoricoConsultas;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReembolsoDeConsultasTest {


    @Test
    public void calcularReembolsoDeConsultaCom70PorCentoDeCobertura() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(paciente, 200.0, 70.0);
        double reembolsoEsperado = 140.00;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularReembolsoDeConsultaCom0PorCentoDeCobertura() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(paciente, 100.0, 0.0);
        double reembolsoEsperado = 0.00;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularReembolsoDeConsultaCom100PorCentoDeCobertura() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(paciente, 100.0, 100.0);
        double reembolsoEsperado = 100.0;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void deveRegistrarConsultaNoHistorico() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        HistoricoConsultas historico = criarHistoricoConsultasDummy();

        double valor = 150.0;
        double percentualReembolso = 50;
        double resultado = reembolso.calcular(paciente, valor, percentualReembolso);

        historico.registrarConsulta(paciente, valor, resultado, "Consulta de rotina");

        List<String> consultas = historico.listarConsultas();

        assertEquals(1, consultas.size());
        System.out.println((consultas.get(0)));
    }

    private Paciente criarPacienteDummy() {
        return new Paciente("Matheus Dummy", "123456789-70", "matheus.zilli@al.infnet.edu.br");
    }

    private HistoricoConsultas criarHistoricoConsultasDummy() {
        return new HistoricoConsultas() {
            private List<String> consultas = new ArrayList<>();

            @Override
            public void registrarConsulta(Paciente paciente, double valorConsulta, double percentualReembolso, String observacao) {
                String registro = String.format("Paciente: %s, Valor: %.2f, Cobertura: %.2f%%, Obs: %s",
                        paciente.getNome(), valorConsulta, percentualReembolso, observacao);
                consultas.add(registro);
            }

            @Override
            public List<String> listarConsultas() {
                return consultas;
            }
        };
    }
}
