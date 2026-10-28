package br.com.pedidos.dto;

import java.math.BigDecimal;

import br.com.pedidos.model.Produto;

public record ProdutoResponse(Long id, String nome, String descricao, BigDecimal preco, int estoque) {

    public static ProdutoResponse de(Produto produto) {
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getDescricao(),
                produto.getPreco(), produto.getEstoque());
    }
}
