package org.example.test;

import org.example.model.Consulta;
import org.example.model.Paciente;
import org.example.model.PlanoSaude;
import org.example.service.Auditoria;
import org.example.service.AutorizadorReembolso;
import org.example.service.CalculadoraReembolso;
import org.example.repository.HistoricoConsultas;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReembolsoDeConsultasTest {

    private void assertValoresIguaisComMargemDeErro(double esperado, double atual) {
        assertEquals(esperado, atual, 0.01);
    }

    @Test
    public void testeIntegracaoComVariosDoublesDeveCalcularReembolsoCorreto() {

        Consulta consulta = criarConsulta(250.0, 80.0, "Consulta especializada");

        PlanoSaude planoSaudeStub = criarPlanoSaudeStub80PorCento();

        AutorizadorReembolso autorizadorMock = Mockito.mock(AutorizadorReembolso.class);
        when(autorizadorMock.autorizar(any(Paciente.class), anyDouble(), anyDouble()))
                .thenReturn(true);

        AuditoriaSpy auditoriaSpy = new AuditoriaSpy();

        HistoricoConsultas historicoConsultasDummy = criarHistoricoConsultasDummy();

        CalculadoraReembolso calculadora = new CalculadoraReembolso() {
            private Auditoria auditoria = auditoriaSpy;
            private AutorizadorReembolso autorizador = autorizadorMock;

            @Override
            public double calcularPlanoDeSaude(Paciente paciente, double valorConsulta, PlanoSaude planoSaude, String observacao) {
                if (autorizador != null) {
                    boolean autorizado = autorizador.autorizar(paciente, valorConsulta, planoSaude.getPercentualCobertura());
                    if (!autorizado) {
                        throw new ReembolsoNaoAutorizadoException("Reembolso não autorizado");
                    }
                }

                double percentualReembolso = planoSaude.getPercentualCobertura();
                double valorReembolso = valorConsulta * (percentualReembolso / 100);

                if (valorReembolso > 150.0) {
                    valorReembolso = 150.0;
                }

                if (auditoria != null) {
                    auditoria.registrarConsulta(paciente, valorConsulta, valorReembolso, observacao);
                }

                return valorReembolso;
            }
        };

        double resultado = calculadora.calcularPlanoDeSaude(
                consulta.getPaciente(),
                consulta.getValor(),
                planoSaudeStub,
                consulta.getObservacao()
        );

        historicoConsultasDummy.registrarConsulta(
                consulta.getPaciente(),
                consulta.getValor(),
                resultado,
                consulta.getObservacao()
        );

        assertValoresIguaisComMargemDeErro(150.0, resultado);

        assertTrue("O método de auditoria deveria ter sido chamado", auditoriaSpy.foiChamado());

        assertEquals(1, historicoConsultasDummy.listarConsultas().size());

        verify(autorizadorMock).autorizar(
                consulta.getPaciente(),
                consulta.getValor(),
                planoSaudeStub.getPercentualCobertura()
        );
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

        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void calcularComPlanoDeSaudeStub80PorCento() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        PlanoSaude planoSaude = criarPlanoSaudeStub80PorCento();

        double valorConsulta = 200.0;
        double resultado = reembolso.calcularPlanoDeSaude(paciente, valorConsulta, planoSaude, "Consulta de rotina");
        double reembolsoEsperado = 150.0;

        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void deveRegistrarNaAuditoria() {
        Paciente paciente = criarPacienteDummy();
        AuditoriaSpy auditoriaSpy = new AuditoriaSpy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso(auditoriaSpy);

        reembolso.calcular(paciente, 200.0, 70.0, "Consulta de rotina");

        assertTrue("O método de auditoria deveria ter sido chamado", auditoriaSpy.foiChamado());
    }

    @Test
    public void calcularReembolsoDeConsultaCom70PorCentoDeCobertura() {
        Consulta consulta = criarConsultaPadrao();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();

        double resultado = reembolso.calcular(
                consulta.getPaciente(),
                consulta.getValor(),
                consulta.getPercentualReembolso(),
                consulta.getObservacao()
        );

        double reembolsoEsperado = 140.00;
        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void calcularReembolsoDeConsultaCom0PorCentoDeCobertura() {
        Consulta consulta = criarConsulta(100.0, 0.0, "Consulta de rotina");
        CalculadoraReembolso reembolso = new CalculadoraReembolso();

        double resultado = reembolso.calcular(
                consulta.getPaciente(),
                consulta.getValor(),
                consulta.getPercentualReembolso(),
                consulta.getObservacao()
        );

        double reembolsoEsperado = 0.00;
        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void calcularReembolsoDeConsultaCom100PorCentoDeCobertura() {
        Consulta consulta = criarConsulta(100.0, 100.0, "Consulta de rotina");
        CalculadoraReembolso reembolso = new CalculadoraReembolso();

        double resultado = reembolso.calcular(
                consulta.getPaciente(),
                consulta.getValor(),
                consulta.getPercentualReembolso(),
                consulta.getObservacao()
        );

        double reembolsoEsperado = 100.0;
        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void deveAplicarTetoDeReembolsoQuandoValorCalculadoExcederLimite() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();

        double valorConsulta = 300.0;
        double percentualReembolso = 80.0;

        double resultado = reembolso.calcular(paciente, valorConsulta, percentualReembolso, "Consulta especializada");
        double reembolsoEsperado = 150.0;

        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void naoDeveAplicarTetoDeReembolsoQuandoValorCalculadoEstiverDentroDoLimite() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();

        double valorConsulta = 200.0;
        double percentualReembolso = 70.0;

        double resultado = reembolso.calcular(paciente, valorConsulta, percentualReembolso, "Consulta padrão");
        double reembolsoEsperado = 140.0;

        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test
    public void deveAplicarTetoDeReembolsoComPlanoSaude() {
        Paciente paciente = criarPacienteDummy();
        CalculadoraReembolso reembolso = new CalculadoraReembolso();
        PlanoSaude planoSaude = criarPlanoSaude100PorCento();

        double valorConsulta = 200.0;

        double resultado = reembolso.calcularPlanoDeSaude(paciente, valorConsulta, planoSaude, "Consulta com plano premium");
        double reembolsoEsperado = 150.0;

        assertValoresIguaisComMargemDeErro(reembolsoEsperado, resultado);
    }

    @Test(expected = ReembolsoNaoAutorizadoException.class)
    public void reembolsoNaoAutorizadoDeveLancarExcecao() {
        Paciente paciente = criarPacienteDummy();
        AutorizadorReembolso autorizadorMock = Mockito.mock(AutorizadorReembolso.class);
        when(autorizadorMock.autorizar(any(Paciente.class), anyDouble(), anyDouble()))
                .thenReturn(false);

        CalculadoraReembolso reembolso = new CalculadoraReembolso(autorizadorMock);

        reembolso.calcular(paciente, 200.0, 70.0, "Consulta de rotina");
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

    private PlanoSaude criarPlanoSaude100PorCento() {
        return new PlanoSaude() {
            @Override
            public double getPercentualCobertura() {
                return 100.0;
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

    private Consulta criarConsultaPadrao() {
        Paciente paciente = criarPacienteDummy();
        double valor = 200.0;
        double percentualReembolso = 70.0;
        String observacao = "Consulta de rotina";

        return new Consulta(paciente, valor, percentualReembolso, observacao);
    }

    private Consulta criarConsulta(double valor, double percentualReembolso, String observacao) {
        Paciente paciente = criarPacienteDummy();
        return new Consulta(paciente, valor, percentualReembolso, observacao);
    }

    public class ReembolsoNaoAutorizadoException extends RuntimeException {
        public ReembolsoNaoAutorizadoException(String message) {
            super(message);
        }
    }

    public class AuditoriaSpy implements Auditoria {
        private boolean foiChamado = false;

        @Override
        public void registrarConsulta(Paciente paciente, double valorConsulta, double valorReembolso, String observacao) {
            foiChamado = true;
        }

        public boolean foiChamado() {
            return foiChamado;
        }
    }
}