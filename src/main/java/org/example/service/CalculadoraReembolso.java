package org.example.service;

import org.example.exception.ReembolsoNaoAutorizadoException;
import org.example.model.Paciente;
import org.example.model.PlanoSaude;

public class CalculadoraReembolso {

    private Auditoria auditoria;
    private AutorizadorReembolso autorizador;
    private static final double TETO_REEMBOLSO = 150.0; // Valor máximo de reembolso

    public CalculadoraReembolso() {
        this.auditoria = null;
        this.autorizador = null;
    }

    public CalculadoraReembolso(Auditoria auditoria) {
        this.auditoria = auditoria;
        this.autorizador = null;
    }

    public CalculadoraReembolso(AutorizadorReembolso autorizador) {
        this.auditoria = null;
        this.autorizador = autorizador;
    }

    public double calcular(Paciente paciente, double valorConsulta, double percentualReembolso, String observacao) {
        if (autorizador != null) {
            boolean autorizado = autorizador.autorizar(paciente, valorConsulta, percentualReembolso);
            if (!autorizado) {
                throw new ReembolsoNaoAutorizadoException("Reembolso não autorizado");
            }
        }

        double valorReembolso = valorConsulta * (percentualReembolso / 100);

        if (valorReembolso > TETO_REEMBOLSO) {
            valorReembolso = TETO_REEMBOLSO;
        }

        if (auditoria != null) {
            auditoria.registrarConsulta(paciente, valorConsulta, valorReembolso, observacao);
        }

        return valorReembolso;
    }

    public double calcularPlanoDeSaude(Paciente paciente, double valorConsulta, PlanoSaude planoSaude, String observacao) {
        double percentualReembolso = planoSaude.getPercentualCobertura();

        if (autorizador != null) {
            boolean autorizado = autorizador.autorizar(paciente, valorConsulta, percentualReembolso);
            if (!autorizado) {
                throw new ReembolsoNaoAutorizadoException("Reembolso não autorizado");
            }
        }

        double valorReembolso = valorConsulta * (percentualReembolso / 100);

        if (valorReembolso > TETO_REEMBOLSO) {
            valorReembolso = TETO_REEMBOLSO;
        }

        if (auditoria != null) {
            auditoria.registrarConsulta(paciente, valorConsulta, valorReembolso, observacao);
        }

        return valorReembolso;
    }
}