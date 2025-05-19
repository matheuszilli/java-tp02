package org.example.repository;
import org.example.model.Paciente;

import java.util.List;

public interface HistoricoConsultas {
    void registrarConsulta(Paciente paciente, double valorConsulta, double percentualReembolso, String observacao);
    List<String> listarConsultas();
}
