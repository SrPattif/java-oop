package br.com.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.pedidos.dto.ClienteRequest;
import br.com.pedidos.dto.ClienteResponse;
import br.com.pedidos.exception.RecursoNaoEncontradoException;
import br.com.pedidos.exception.RegraNegocioException;
import br.com.pedidos.model.Cliente;
import br.com.pedidos.repository.ClienteRepository;
import br.com.pedidos.repository.PedidoRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        if (clienteRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com o e-mail " + request.email() + ".");
        }
        Cliente cliente = new Cliente(request.nome(), request.email(), request.telefone());
        return ClienteResponse.de(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarEntidade(id);
        if (clienteRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new RegraNegocioException("Já existe outro cliente cadastrado com o e-mail " + request.email() + ".");
        }
        cliente.atualizarDados(request.nome(), request.email(), request.telefone());
        return ClienteResponse.de(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarEntidade(id);
        if (pedidoRepository.existsByClienteId(id)) {
            throw new RegraNegocioException("Não é possível excluir um cliente que possui pedidos.");
        }
        clienteRepository.delete(cliente);
    }

    Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
    }
}
