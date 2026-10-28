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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.pedidos.dto.ProdutoRequest;
import br.com.pedidos.dto.ProdutoResponse;
import br.com.pedidos.service.ProdutoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoResponse> listar(@RequestParam(required = false) String nome) {
        return produtoService.listar(nome);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@RequestBody @Valid ProdutoRequest request) {
        ProdutoResponse criado = produtoService.criar(request);
        return ResponseEntity.created(URI.create("/api/produtos/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
