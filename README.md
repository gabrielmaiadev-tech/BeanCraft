# BeanCraft

API REST para cadastro de cafés, construída com Java 21, Spring Boot 3, PostgreSQL e Flyway.

## Requisitos

- JDK 21 ou superior
- Maven 3.9+
- Docker, para iniciar o PostgreSQL e executar o teste de integração com Testcontainers

## Executar

Inicie o banco local:

```sh
docker compose up -d
```

Inicie a API:

```sh
mvn spring-boot:run
```

A conexão pode ser configurada por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. Os valores padrão apontam para o banco `beancraft` no `localhost:5432`.

## API de produtos

- `GET /api/products`: lista produtos
- `GET /api/products/{id}`: consulta um produto
- `POST /api/products`: cria um produto e retorna `201 Created` com `Location`
- `PUT /api/products/{id}`: substitui os dados de um produto
- `DELETE /api/products/{id}`: remove um produto e retorna `204 No Content`

Campos de criação e atualização: `name`, `origin`, `roast` (`LIGHT`, `MEDIUM` ou `DARK`), `weightGrams`, `price` e `stock`. O nome é único. Erros são retornados como `application/problem+json`.

Documentação interativa: `/swagger-ui.html` (OpenAPI em `/api-docs`).

## Testes

```sh
mvn test
```

Os testes unitários do serviço usam JUnit 5 e Mockito. O teste do repositório usa Testcontainers e é ignorado automaticamente quando Docker não está disponível.