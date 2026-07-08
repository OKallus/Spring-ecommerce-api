# 🛒 E-commerce API

API REST para e-commerce desenvolvida com Spring Boot, com autenticação JWT, controle de acesso por roles, carrinho de compras e gerenciamento de pedidos.

---

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Swagger / OpenAPI 3**
- **Lombok**
- **Maven**

---

## ⚙️ Como rodar o projeto

### Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose instalados

### 1. Clone o repositório

```bash
git clone https://github.com/OKallus/Spring-ecommerce-api.git
cd Spring-ecommerce-api
```

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e preencha com seus valores:

```bash
cp .env.example .env
```

Edite o `.env`:

```env
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_aqui
JWT_SECRET=gere_uma_chave_aleatoria_de_pelo_menos_256_bits
```

### 3. Suba os containers

```bash
docker-compose up --build
```

A API estará disponível em: `http://localhost:8080`

---

## 📄 Documentação dos endpoints

Acesse o Swagger UI após subir a aplicação:

```
http://localhost:8080/swagger-ui.html
```

### Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/register` | Cadastrar novo usuário | ❌ |
| POST | `/api/auth/login` | Login e obter token JWT | ❌ |

### Produtos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/products` | Listar produtos (paginado) | ❌ |
| GET | `/api/products/{id}` | Buscar produto por ID | ❌ |
| POST | `/api/products` | Criar produto | ✅ ADMIN |
| PUT | `/api/products/{id}` | Atualizar produto | ✅ ADMIN |
| DELETE | `/api/products/{id}` | Remover produto (soft delete) | ✅ ADMIN |

### Carrinho

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/cart` | Ver carrinho | ✅ USER |
| POST | `/api/cart/items` | Adicionar item | ✅ USER |
| PUT | `/api/cart/items/{itemId}` | Atualizar quantidade | ✅ USER |
| DELETE | `/api/cart/items/{itemId}` | Remover item | ✅ USER |

### Pedidos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/orders` | Criar pedido a partir do carrinho | ✅ USER |
| GET | `/api/orders` | Listar histórico de pedidos | ✅ USER |
| GET | `/api/orders/{id}` | Detalhar pedido | ✅ USER |

---

## 🔐 Autenticação

A API utiliza **JWT Bearer Token**. Após o login, inclua o token no header das requisições:

```
Authorization: Bearer {seu_token}
```

---

## 🗂️ Estrutura do projeto

```
src/main/java/com/ecommerce/ecommerce_api/
├── config/         # Configurações (Security, OpenAPI)
├── controller/     # Controllers REST
├── dto/            # Request e Response DTOs
├── entity/         # Entidades JPA
├── enums/          # Enums (Role, OrderStatus)
├── exception/      # Tratamento global de erros
├── repository/     # Repositórios JPA
├── security/       # JWT Filter, JwtService, UserDetailsService
└── service/        # Regras de negócio
```

---

## 🧠 Decisões técnicas

- **Soft delete** em produtos — registros nunca são removidos fisicamente do banco
- **Snapshot de preço** em `OrderItem.unitPrice` — garante que o preço do pedido não muda se o produto for atualizado
- **`@EntityGraph`** na listagem de pedidos — evita o problema de N+1 queries ao carregar itens
- **DTOs** em todas as respostas — a entidade `User` nunca é exposta diretamente
- **`@RestControllerAdvice`** centraliza o tratamento de erros com respostas padronizadas

---

## 📦 Variáveis de ambiente

| Variável | Descrição | Padrão (dev) |
|----------|-----------|--------------|
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | — |
| `JWT_SECRET` | Chave secreta para assinar tokens JWT | — |

---

## 👤 Autor

Carlos Eduardo — [GitHub](https://github.com/OKallus)