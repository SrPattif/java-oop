package br.com.pedidos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(
        @NotBlank(message = "é obrigatório") @Size(max = 100) String nome,
        @Size(max = 255) String descricao,
        @NotNull(message = "é obrigatório") @DecimalMin(value = "0.0", message = "não pode ser negativo") BigDecimal preco,
        @NotNull(message = "é obrigatório") @Min(value = 0, message = "não pode ser negativo") Integer estoque) {
}
