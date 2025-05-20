package org.example.service;

import org.example.model.Paciente;

public interface Auditoria {
    void registrarConsulta(Paciente paciente, double valorConsulta, double valorReembolso, String observacao);
}
