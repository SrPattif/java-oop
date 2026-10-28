package br.com.pedidos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "é obrigatório") @Size(max = 100) String nome,
        @NotBlank(message = "é obrigatório") @Email(message = "deve ser um e-mail válido") String email,
        @Size(max = 20) String telefone) {
}
