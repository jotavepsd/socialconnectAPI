package br.com.socialconnect.api.exception;

public class EstoqueNegativoException extends RuntimeException {
    public EstoqueNegativoException() {
        super("O estoque atual não pode ser negativo.");
    }
}
