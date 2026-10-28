package br.com.pedidos.dto;

import java.math.BigDecimal;

import br.com.pedidos.model.ItemPedido;

public record ItemPedidoResponse(Long produtoId, String produto, int quantidade,
        BigDecimal precoUnitario, BigDecimal subtotal) {

    public static ItemPedidoResponse de(ItemPedido item) {
        return new ItemPedidoResponse(item.getProduto().getId(), item.getProduto().getNome(),
                item.getQuantidade(), item.getPrecoUnitario(), item.getSubtotal());
    }
}
