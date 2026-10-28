package br.com.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequest(
        @NotNull(message = "é obrigatório") Long produtoId,
        @NotNull(message = "é obrigatório") @Min(value = 1, message = "deve ser no mínimo 1") Integer quantidade) {
}
