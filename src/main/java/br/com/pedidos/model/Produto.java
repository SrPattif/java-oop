package br.com.pedidos.model;

import java.math.BigDecimal;

import br.com.pedidos.exception.EstoqueInsuficienteException;
import br.com.pedidos.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Produto vendido pela empresa, com controle de estoque (contexto de ERP).
 */
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private int estoque;

    protected Produto() {
        // exigido pelo JPA
    }

    public Produto(String nome, String descricao, BigDecimal preco, int estoque) {
        atualizarDados(nome, descricao, preco, estoque);
    }

    public void atualizarDados(String nome, String descricao, BigDecimal preco, int estoque) {
        if (preco == null || preco.signum() < 0) {
            throw new RegraNegocioException("O preço do produto não pode ser negativo.");
        }
        if (estoque < 0) {
            throw new RegraNegocioException("O estoque do produto não pode ser negativo.");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
    }

    /**
     * Retira itens do estoque. O próprio produto garante que o estoque nunca fique negativo.
     */
    public void baixarEstoque(int quantidade) {
        validarQuantidade(quantidade);
        if (quantidade > estoque) {
            throw new EstoqueInsuficienteException(nome, estoque, quantidade);
        }
        estoque -= quantidade;
    }

    public void reporEstoque(int quantidade) {
        validarQuantidade(quantidade);
        estoque += quantidade;
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getEstoque() {
        return estoque;
    }
}
