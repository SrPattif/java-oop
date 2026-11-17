package br.com.pedidos.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.pedidos.model.Cliente;
import br.com.pedidos.model.Produto;
import br.com.pedidos.repository.ClienteRepository;
import br.com.pedidos.repository.ProdutoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Cliente cliente;
    private Produto produto;

    @BeforeEach
    void setUp() {
        cliente = clienteRepository.save(new Cliente("Maria", "maria@email.com", null));
        produto = produtoRepository.save(new Produto("Mouse", "Sem fio", new BigDecimal("80.00"), 10));
    }

    @Test
    void deveCriarPedidoEAtualizarEstoque() throws Exception {
        String json = """
                {"clienteId": %d, "itens": [{"produtoId": %d, "quantidade": 3}]}
                """.formatted(cliente.getId(), produto.getId());

        mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.total").value(240.00))
                .andExpect(jsonPath("$.cliente.nome").value("Maria"));

        mockMvc.perform(get("/api/produtos/{id}", produto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estoque").value(7));
    }

    @Test
    void deveRetornar422QuandoEstoqueForInsuficiente() throws Exception {
        String json = """
                {"clienteId": %d, "itens": [{"produtoId": %d, "quantidade": 50}]}
                """.formatted(cliente.getId(), produto.getId());

        mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", containsString("Estoque insuficiente")));
    }

    @Test
    void deveRetornar404QuandoPedidoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/pedidos/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Pedido com id 9999 não encontrado."));
    }

    @Test
    void deveRetornar400QuandoDadosForemInvalidos() throws Exception {
        String json = """
                {"clienteId": null, "itens": []}
                """;

        mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes.length()").value(2));
    }

    @Test
    void deveRetornar422AoTentarStatusInvalido() throws Exception {
        String pedido = """
                {"clienteId": %d, "itens": [{"produtoId": %d, "quantidade": 1}]}
                """.formatted(cliente.getId(), produto.getId());
        String resposta = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON).content(pedido))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(resposta, "$.id");

        mockMvc.perform(patch("/api/pedidos/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"ENTREGUE\"}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
