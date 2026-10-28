package br.com.pedidos.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte as exceções da aplicação em respostas HTTP com status e mensagem adequados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos na requisição.", detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado.", List.of());
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem, List<String> detalhes) {
        ErroResponse corpo = new ErroResponse(status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
