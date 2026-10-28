package br.com.pedidos.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Corpo padronizado das respostas de erro da API.
 */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        List<String> detalhes) {

    public ErroResponse(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, List.of());
    }

    public ErroResponse(int status, String erro, String mensagem, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, detalhes);
    }
}
