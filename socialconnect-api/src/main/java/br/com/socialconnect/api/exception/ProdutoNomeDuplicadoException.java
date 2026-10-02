package br.com.socialconnect.api.exception;

public class ProdutoNomeDuplicadoException extends RuntimeException {
    private final String nome;

    public ProdutoNomeDuplicadoException(String nome) {
        super("O produto " + nome + " já está cadastrado no sistema.");
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
