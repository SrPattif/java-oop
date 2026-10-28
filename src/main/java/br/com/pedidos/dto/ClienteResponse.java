package br.com.pedidos.dto;

import br.com.pedidos.model.Cliente;

public record ClienteResponse(Long id, String nome, String email, String telefone) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getTelefone());
    }
}
