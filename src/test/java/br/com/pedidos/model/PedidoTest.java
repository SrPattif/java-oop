package br.com.pedidos.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.pedidos.exception.EstoqueInsuficienteException;
import br.com.pedidos.exception.RegraNegocioException;

/**
 * Testes unitários das regras de negócio do domínio (sem Spring e sem banco).
 */
class PedidoTest {

    private Produto notebook;
    private Produto mouse;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        notebook = new Produto("Notebook", "15 polegadas", new BigDecimal("3500.00"), 5);
        mouse = new Produto("Mouse", "Sem fio", new BigDecimal("80.00"), 10);
        pedido = new Pedido(new Cliente("Maria", "maria@email.com", null));
    }

    @Test
    void deveCalcularTotalDoPedidoEBaixarEstoque() {
        pedido.adicionarItem(notebook, 1);
        pedido.adicionarItem(mouse, 2);

        assertThat(pedido.getTotal()).isEqualByComparingTo("3660.00");
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PENDENTE);
        assertThat(notebook.getEstoque()).isEqualTo(4);
        assertThat(mouse.getEstoque()).isEqualTo(8);
    }

    @Test
    void naoDevePermitirItemComQuantidadeMaiorQueOEstoque() {
        assertThatThrownBy(() -> pedido.adicionarItem(notebook, 6))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("Notebook");

        assertThat(notebook.getEstoque()).isEqualTo(5);
        assertThat(pedido.getItens()).isEmpty();
    }

    @Test
    void cancelarPedidoDeveDevolverItensAoEstoque() {
        pedido.adicionarItem(notebook, 3);

        pedido.alterarStatus(StatusPedido.CANCELADO);

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        assertThat(notebook.getEstoque()).isEqualTo(5);
    }

    @Test
    void naoDevePermitirTransicaoDeStatusInvalida() {
        pedido.adicionarItem(mouse, 1);
        pedido.alterarStatus(StatusPedido.PAGO);
        pedido.alterarStatus(StatusPedido.ENVIADO);

        assertThatThrownBy(() -> pedido.alterarStatus(StatusPedido.CANCELADO))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("ENVIADO");
    }
}
