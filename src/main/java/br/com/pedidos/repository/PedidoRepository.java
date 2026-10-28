package br.com.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.pedidos.model.Pedido;
import br.com.pedidos.model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(StatusPedido status);

    boolean existsByClienteId(Long clienteId);

    @Query("select count(i) > 0 from ItemPedido i where i.produto.id = :produtoId")
    boolean existeItemComProduto(Long produtoId);
}
