package br.com.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.pedidos.exception.RegraNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (status != StatusPedido.PENDENTE) {
            throw new RegraNegocioException("Só é possível adicionar itens a pedidos pendentes.");
        }
        produto.baixarEstoque(quantidade);
        itens.add(new ItemPedido(this, produto, quantidade));
    }

    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void alterarStatus(StatusPedido novoStatus) {
        if (!status.podeMudarPara(novoStatus)) {
            throw new RegraNegocioException(
                    "Não é possível alterar o status do pedido de " + status + " para " + novoStatus + ".");
        }
        if (novoStatus == StatusPedido.CANCELADO) {
            itens.forEach(item -> item.getProduto().reporEstoque(item.getQuantidade()));
        }
        this.status = novoStatus;
    }

    public boolean podeSerExcluido() {
        return status == StatusPedido.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }
}
