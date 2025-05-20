package org.example.test;

import org.example.model.Paciente;
import org.example.service.Auditoria;

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