package br.com.pedidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.pedidos.dto.ProdutoRequest;
import br.com.pedidos.dto.ProdutoResponse;
import br.com.pedidos.exception.RecursoNaoEncontradoException;
import br.com.pedidos.exception.RegraNegocioException;
import br.com.pedidos.model.Produto;
import br.com.pedidos.repository.PedidoRepository;
import br.com.pedidos.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar(String nome) {
        List<Produto> produtos = (nome == null || nome.isBlank())
                ? produtoRepository.findAll()
                : produtoRepository.findByNomeContainingIgnoreCase(nome);
        return produtos.stream().map(ProdutoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return ProdutoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto(request.nome(), request.descricao(), request.preco(), request.estoque());
        return ProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        produto.atualizarDados(request.nome(), request.descricao(), request.preco(), request.estoque());
        return ProdutoResponse.de(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        if (pedidoRepository.existeItemComProduto(id)) {
            throw new RegraNegocioException("Não é possível excluir um produto que já foi usado em pedidos.");
        }
        produtoRepository.delete(produto);
    }

    Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }
}
