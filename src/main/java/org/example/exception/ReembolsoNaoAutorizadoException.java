package org.example.exception;

public class ReembolsoNaoAutorizadoException extends RuntimeException {
    public ReembolsoNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}