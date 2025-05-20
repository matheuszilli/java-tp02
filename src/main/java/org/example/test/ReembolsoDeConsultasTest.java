package org.example.test;

import org.example.model.Paciente;
import org.example.model.PlanoSaude;
import org.example.service.CalculadoraReembolso;
import org.example.repository.HistoricoConsultas;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReembolsoDeConsultasTest {


    @Test
    public void calcularReembolsoDeConsultaCom70PorCentoDeCobertura() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(paciente, 200.0, 70.0, "Consulta de rotina");
        double reembolsoEsperado = 140.00;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularReembolsoDeConsultaCom0PorCentoDeCobertura() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(paciente, 100.0, 0.0, "Consulta de rotina");
        double reembolsoEsperado = 0.00;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularReembolsoDeConsultaCom100PorCentoDeCobertura() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        double resultado = reembolso.calcular(paciente, 100.0, 100.0, "Consulta de rotina");
        double reembolsoEsperado = 100.0;
        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void deveRegistrarConsultaNoHistorico() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        HistoricoConsultas historico = criarHistoricoConsultasDummy();
        PlanoSaude planoSaude = criarPlanoSaudeStub50PorCento();

        double valor = 150.0;
        double resultado = reembolso.calcular(paciente, valor, 50.0, "Consulta de rotina");

        historico.registrarConsulta(paciente, valor, resultado, "Consulta de rotina");

        List<String> consultas = historico.listarConsultas();

        assertEquals(1, consultas.size());
        System.out.println((consultas.get(0)));
    }

    @Test
    public void calcularComPlanoDeSaudeStub50PorCento() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        PlanoSaude planoSaude = criarPlanoSaudeStub50PorCento();

        double valorConsulta = 200.0;
        double resultado = reembolso.calcularPlanoDeSaude(paciente, valorConsulta, planoSaude, "Consulta de rotina");
        double reembolsoEsperado = 100.0;

        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void calcularComPlanoDeSaudeStub80PorCento() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        PlanoSaude planoSaude = criarPlanoSaudeStub80PorCento();

        double valorConsulta = 200.0;
        double resultado = reembolso.calcularPlanoDeSaude(paciente, valorConsulta, planoSaude, "Consulta de rotina");
        double reembolsoEsperado = 160.0;

        assertEquals(reembolsoEsperado, resultado, 0.01);
    }

    @Test
    public void deveRegistrarNaAuditoria() {
        Paciente paciente = criarPacienteDummy();
        AuditoriaSpy auditoriaSpy = new AuditoriaSpy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso(auditoriaSpy);

        reembolso.calcular(paciente, 200.0, 70.0, "Consulta de rotina");

        assertTrue("O método de auditoria deveria ter sido chamado", auditoriaSpy.foiChamado());
    }

    private Paciente criarPacienteDummy() {
        return new Paciente("Matheus Dummy", "123456789-70", "matheus.zilli@al.infnet.edu.br");
    }

    private PlanoSaude criarPlanoSaudeStub50PorCento() {
        return new PlanoSaude() {
            @Override
            public double getPercentualCobertura() {
                return 50.0;
            }
        };
    }

    private PlanoSaude criarPlanoSaudeStub80PorCento() {
        return new PlanoSaude() {
            @Override
            public double getPercentualCobertura() {
                return 80.0;
            }
        };
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
