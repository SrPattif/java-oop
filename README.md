# Gestão de Pedidos — Projeto Final

API REST em **Java + Spring Boot** para uma pequena loja: cadastro de **clientes**, **produtos** (com estoque) e **pedidos**.

## Problema de negócio

Uma loja precisa registrar os pedidos de seus clientes garantindo que:

- só é possível vender produtos que tenham estoque;
- ao criar um pedido, o estoque é baixado automaticamente;
- ao cancelar um pedido, os itens voltam ao estoque;
- o pedido segue um fluxo de status: `PENDENTE → PAGO → ENVIADO → ENTREGUE` (cancelamento só em `PENDENTE` ou `PAGO`);
- o preço do item fica "congelado" no pedido, mesmo que o preço do produto mude depois.

## Tecnologias

- Java 17+ / Spring Boot 3.5
- Spring Web (API REST), Spring Data JPA (persistência), Bean Validation
- Banco H2 em memória
- JUnit 5, Mockito e MockMvc (testes)

## Como executar

Requisitos: JDK 17 ou superior e Maven.

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Alguns clientes e produtos de exemplo são carregados de `src/main/resources/data.sql`.

Console do banco: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:pedidosdb`, usuário `sa`, sem senha).

Rodar os testes:

```bash
mvn test
```

## Entidades

| Entidade     | Descrição                                                  | Relacionamentos                       |
|--------------|------------------------------------------------------------|---------------------------------------|
| `Cliente`    | Nome, e-mail (único), telefone                             | 1 cliente → N pedidos                 |
| `Produto`    | Nome, descrição, preço, estoque                            | 1 produto → N itens de pedido         |
| `Pedido`     | Cliente, data, status, itens, total calculado              | N:1 com Cliente, 1:N com ItemPedido   |
| `ItemPedido` | Produto, quantidade, preço unitário, subtotal              | N:1 com Pedido e com Produto          |

`StatusPedido` é um enum que define quais transições de status são permitidas.

## Arquitetura em camadas

```
Cliente HTTP ──► Controller ──► Service ──► Repository ──► Banco H2
                 (REST/DTOs)    (transação,  (Spring Data
                                 orquestração)  JPA)
                                     │
                                     ▼
                              Entidades de domínio
                              (regras de negócio)
```

```
src/main/java/br/com/pedidos
├── controller/   endpoints REST
├── service/      casos de uso e transações
├── repository/   interfaces Spring Data JPA
├── model/        entidades JPA com as regras de negócio
├── dto/          objetos de entrada/saída da API (records)
└── exception/    exceções de negócio e tratamento global de erros
```

## Endpoints

### Clientes — `/api/clientes`
| Método | Rota                 | Descrição              |
|--------|----------------------|------------------------|
| GET    | `/api/clientes`      | Lista clientes         |
| GET    | `/api/clientes/{id}` | Busca cliente          |
| POST   | `/api/clientes`      | Cadastra cliente       |
| PUT    | `/api/clientes/{id}` | Atualiza cliente       |
| DELETE | `/api/clientes/{id}` | Exclui (se não tiver pedidos) |

### Produtos — `/api/produtos`
| Método | Rota                         | Descrição                       |
|--------|------------------------------|---------------------------------|
| GET    | `/api/produtos?nome=mouse`   | Lista (filtro opcional por nome)|
| GET    | `/api/produtos/{id}`         | Busca produto                   |
| POST   | `/api/produtos`              | Cadastra produto                |
| PUT    | `/api/produtos/{id}`         | Atualiza produto                |
| DELETE | `/api/produtos/{id}`         | Exclui (se nunca foi vendido)   |

### Pedidos — `/api/pedidos`
| Método | Rota                                | Descrição                                   |
|--------|-------------------------------------|---------------------------------------------|
| GET    | `/api/pedidos?clienteId=1` ou `?status=PAGO` | Lista (filtros opcionais)          |
| GET    | `/api/pedidos/{id}`                 | Busca pedido                                |
| POST   | `/api/pedidos`                      | Cria pedido e baixa estoque                 |
| PATCH  | `/api/pedidos/{id}/status`          | Altera status (cancelar devolve estoque)    |
| DELETE | `/api/pedidos/{id}`                 | Exclui (somente pedidos cancelados)         |

### Exemplos

```bash
# Criar um cliente
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana Lima", "email": "ana@email.com", "telefone": "(11) 98888-0000"}'

# Criar um pedido
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 1, "itens": [{"produtoId": 1, "quantidade": 2}, {"produtoId": 2, "quantidade": 1}]}'

# Marcar como pago
curl -X PATCH http://localhost:8080/api/pedidos/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "PAGO"}'
```

## Tratamento de erros

O `GlobalExceptionHandler` (`@RestControllerAdvice`) devolve sempre um JSON padronizado:

```json
{
  "timestamp": "2026-10-28T22:30:32.98",
  "status": 422,
  "erro": "Unprocessable Entity",
  "mensagem": "Estoque insuficiente para o produto 'Mouse'. Disponível: 10, solicitado: 50.",
  "detalhes": []
}
```

| Situação                                          | Status HTTP |
|---------------------------------------------------|-------------|
| Recurso não encontrado (`RecursoNaoEncontradoException`) | 404  |
| Dados inválidos (Bean Validation / JSON mal formado)     | 400  |
| Regra de negócio violada (`RegraNegocioException`, `EstoqueInsuficienteException`) | 422 |

## Testes automatizados (12 testes)

- `PedidoTest` — testes unitários do domínio: cálculo do total, baixa de estoque, estoque insuficiente, devolução de estoque no cancelamento e transição de status inválida.
- `PedidoServiceTest` — testes da camada de serviço com Mockito (criação de pedido, cliente inexistente, exclusão proibida).
- `PedidoControllerIntegrationTest` — testes de integração com MockMvc passando por todas as camadas até o banco H2 (201, 400, 404 e 422).

## Conteúdos da disciplina no projeto

| Conteúdo              | Onde aparece                                                                                         |
|-----------------------|------------------------------------------------------------------------------------------------------|
| ERP / CRM / SCM       | Pedidos e controle de estoque = **ERP**; cadastro de clientes = **CRM**.                             |
| Sistemas distribuídos | Comunicação cliente ↔ servidor via HTTP/JSON pela API REST.                                          |
| Componentes           | Separação em `controller`, `service`, `repository`, `dto` e `exception`; injeção de dependência pelo Spring. |
| Java EE / Spring Boot | Spring Web, Spring Data JPA, Jakarta Persistence e Jakarta Validation, transações com `@Transactional`. |
| OOP avançada          | Encapsulamento (sem setters públicos; o estado muda por métodos como `baixarEstoque` e `alterarStatus`), herança de exceções, enum com comportamento (`StatusPedido`), entidades responsáveis por suas próprias regras. |
| Debug e testes        | Testes unitários, com mocks e de integração; `spring.jpa.show-sql=true` para acompanhar o SQL gerado. |
