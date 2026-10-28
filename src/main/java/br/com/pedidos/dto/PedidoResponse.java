package br.com.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.pedidos.model.Pedido;
import br.com.pedidos.model.StatusPedido;

public record PedidoResponse(Long id, ClienteResponse cliente, LocalDateTime dataCriacao,
        StatusPedido status, List<ItemPedidoResponse> itens, BigDecimal total) {

    public static PedidoResponse de(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                ClienteResponse.de(pedido.getCliente()),
                pedido.getDataCriacao(),
                pedido.getStatus(),
                pedido.getItens().stream().map(ItemPedidoResponse::de).toList(),
                pedido.getTotal());
    }
}
