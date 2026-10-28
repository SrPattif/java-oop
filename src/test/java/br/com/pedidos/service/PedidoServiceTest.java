package br.com.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pedidos.dto.ItemPedidoRequest;
import br.com.pedidos.dto.PedidoRequest;
import br.com.pedidos.dto.PedidoResponse;
import br.com.pedidos.exception.RecursoNaoEncontradoException;
import br.com.pedidos.exception.RegraNegocioException;
import br.com.pedidos.model.Cliente;
import br.com.pedidos.model.Pedido;
import br.com.pedidos.model.Produto;
import br.com.pedidos.repository.PedidoRepository;

/**
 * Testes da camada de serviço isolada com Mockito (sem subir o Spring).
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void deveCriarPedidoComOsItensInformados() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", null);
        Produto teclado = new Produto("Teclado", null, new BigDecimal("250.00"), 10);
        when(clienteService.buscarEntidade(1L)).thenReturn(cliente);
        when(produtoService.buscarEntidade(2L)).thenReturn(teclado);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse resposta = pedidoService.criar(
                new PedidoRequest(1L, List.of(new ItemPedidoRequest(2L, 2))));

        assertThat(resposta.total()).isEqualByComparingTo("500.00");
        assertThat(resposta.itens()).hasSize(1);
        assertThat(teclado.getEstoque()).isEqualTo(8);
    }

    @Test
    void deveLancarErroQuandoClienteNaoExiste() {
        when(clienteService.buscarEntidade(99L)).thenThrow(new RecursoNaoEncontradoException("Cliente", 99L));

        assertThatThrownBy(() -> pedidoService.criar(
                new PedidoRequest(99L, List.of(new ItemPedidoRequest(1L, 1)))))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente com id 99 não encontrado.");

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void naoDeveExcluirPedidoQueNaoFoiCancelado() {
        Pedido pedido = new Pedido(new Cliente("Maria", "maria@email.com", null));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.excluir(1L))
                .isInstanceOf(RegraNegocioException.class);

        verify(pedidoRepository, never()).delete(any());
    }
}
