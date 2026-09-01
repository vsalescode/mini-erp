# Mini ERP

API de um mini ERP para controle de estoque e vendas. Neste momento o repositório contém apenas a base do backend: Spring Boot, PostgreSQL, Flyway e os testes de integração. As funcionalidades do sistema serão adicionadas nas próximas etapas.

## Rodando o projeto localmente

É necessário ter Java 21 e Docker instalados. O Maven já pode ser executado pelo wrapper do projeto.

Primeiro, crie o arquivo de configuração local:

```powershell
Copy-Item .env.example .env
```

Troque a senha de exemplo no `.env` e suba o banco:

```powershell
docker compose up -d postgres
```

Depois, inicie a aplicação com o profile `dev`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

O `.env` é usado somente no ambiente local e não deve entrar no Git. A senha do banco não possui valor padrão na configuração da aplicação; se ela não estiver definida, a inicialização falhará.

Com a aplicação no ar, o health check fica disponível em `http://localhost:8080/actuator/health`.

Para parar o banco sem remover o volume:

```powershell
docker compose stop postgres
```

## Testes

Os testes de integração precisam do Docker ativo:

```powershell
.\mvnw.cmd clean verify
```

O Testcontainers sobe um PostgreSQL temporário durante a suíte. Assim, o teste do Flyway não depende do banco criado pelo `docker compose`.

## Profiles

- base: configurações comuns de JPA, Flyway, UTC e Actuator
- `dev`: lê os dados de conexão das variáveis de ambiente ou do `.env`
- `test`: usado pela suíte com o PostgreSQL do Testcontainers

## Arquitetura

O backend será separado por módulos de negócio. A decisão e as regras de dependência estão no [ADR 001](docs/adr/001-modular-hexagonal-architecture.md).
