package br.com.pedidos.exception;

public class EstoqueInsuficienteException extends RegraNegocioException {

    public EstoqueInsuficienteException(String produto, int disponivel, int solicitado) {
        super("Estoque insuficiente para o produto '" + produto + "'. Disponível: "
                + disponivel + ", solicitado: " + solicitado + ".");
    }
}
