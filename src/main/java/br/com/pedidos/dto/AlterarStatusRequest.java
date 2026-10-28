package br.com.pedidos.dto;

import br.com.pedidos.model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusRequest(@NotNull(message = "é obrigatório") StatusPedido status) {
}
