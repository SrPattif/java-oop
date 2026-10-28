package br.com.pedidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.pedidos.dto.ClienteRequest;
import br.com.pedidos.dto.ClienteResponse;
import br.com.pedidos.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@RequestBody @Valid ClienteRequest request) {
        ClienteResponse criado = clienteService.criar(request);
        return ResponseEntity.created(URI.create("/api/clientes/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequest request) {
        return clienteService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
