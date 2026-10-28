package br.com.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.pedidos.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
