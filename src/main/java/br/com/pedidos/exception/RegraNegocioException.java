package br.com.pedidos.exception;

/**
 * Violação de uma regra de negócio (ex.: estoque insuficiente, status inválido).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
