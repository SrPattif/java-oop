package br.com.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.pedidos.dto.ItemPedidoRequest;
import br.com.pedidos.dto.PedidoRequest;
import br.com.pedidos.dto.PedidoResponse;
import br.com.pedidos.exception.RecursoNaoEncontradoException;
import br.com.pedidos.exception.RegraNegocioException;
import br.com.pedidos.model.Cliente;
import br.com.pedidos.model.Pedido;
import br.com.pedidos.model.Produto;
import br.com.pedidos.model.StatusPedido;
import br.com.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService,
            ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(Long clienteId, StatusPedido status) {
        List<Pedido> pedidos;
        if (clienteId != null) {
            pedidos = pedidoRepository.findByClienteId(clienteId);
        } else if (status != null) {
            pedidos = pedidoRepository.findByStatus(status);
        } else {
            pedidos = pedidoRepository.findAll();
        }
        return pedidos.stream().map(PedidoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return PedidoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        Cliente cliente = clienteService.buscarEntidade(request.clienteId());
        Pedido pedido = new Pedido(cliente);
        for (ItemPedidoRequest item : request.itens()) {
            Produto produto = produtoService.buscarEntidade(item.produtoId());
            pedido.adicionarItem(produto, item.quantidade());
        }
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse alterarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarEntidade(id);
        pedido.alterarStatus(novoStatus);
        return PedidoResponse.de(pedido);
    }

    @Transactional
    public void excluir(Long id) {
        Pedido pedido = buscarEntidade(id);
        if (!pedido.podeSerExcluido()) {
            throw new RegraNegocioException("Somente pedidos cancelados podem ser excluídos.");
        }
        pedidoRepository.delete(pedido);
    }

    private Pedido buscarEntidade(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
    }
}
