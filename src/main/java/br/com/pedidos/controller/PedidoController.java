package br.com.pedidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.pedidos.dto.AlterarStatusRequest;
import br.com.pedidos.dto.PedidoRequest;
import br.com.pedidos.dto.PedidoResponse;
import br.com.pedidos.model.StatusPedido;
import br.com.pedidos.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusPedido status) {
        return pedidoService.listar(clienteId, status);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoRequest request) {
        PedidoResponse criado = pedidoService.criar(request);
        return ResponseEntity.created(URI.create("/api/pedidos/" + criado.id())).body(criado);
    }

    @PatchMapping("/{id}/status")
    public PedidoResponse alterarStatus(@PathVariable Long id, @RequestBody @Valid AlterarStatusRequest request) {
        return pedidoService.alterarStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
