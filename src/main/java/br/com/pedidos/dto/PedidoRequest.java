package br.com.pedidos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(
        @NotNull(message = "é obrigatório") Long clienteId,
        @NotEmpty(message = "o pedido deve ter pelo menos um item") List<@Valid ItemPedidoRequest> itens) {
}
