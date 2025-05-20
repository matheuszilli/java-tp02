package org.example.service;

import org.example.model.Paciente;

public interface AutorizadorReembolso {
    boolean autorizar(Paciente paciente, double valorConsulta, double percentualReembolso);
}