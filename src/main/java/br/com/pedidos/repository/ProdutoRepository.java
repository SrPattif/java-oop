package br.com.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.pedidos.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
