# ADR 001 — Arquitetura modular e hexagonal

**Status:** aceito  
**Data:** 31/08/2026

## Contexto

O sistema terá áreas com regras diferentes, como catálogo, estoque, clientes e vendas. Se todo o código for separado apenas em pastas globais de controller, service e repository, essas áreas acabam misturadas conforme o projeto cresce.

Também não queremos criar uma camada de abstrações antes de saber se ela será usada. A estrutura precisa ajudar a manter os limites do negócio sem transformar uma operação simples em várias classes genéricas.

## Decisão

O backend será um monólito modular. A primeira divisão de pacotes será pelo contexto de negócio (`catalog`, `inventory`, `sales` etc.), e não pelo tipo técnico da classe.

Dentro de um módulo, a direção esperada é:

```text
adapter.in.web
  -> application.port.in
    -> application.service
      -> domain
      -> application.port.out
        <- adapter.out.persistence ou adapter.out.security
```

Na prática:

- `domain` concentra as regras e não depende de Spring, HTTP ou JPA
- `application.port.in` define o que o módulo oferece
- `application.service` coordena o caso de uso
- `application.port.out` define o que o caso de uso precisa do lado de fora, como persistência ou relógio
- `adapter.in.web` trata HTTP, validação de entrada e conversão de dados
- `adapter.out.*` contém implementações técnicas, como JPA e segurança
- `configuration` faz a ligação dessas peças com o Spring

Não serão criados pacotes globais de controller, service, repository ou entity. Pacotes vazios para módulos futuros também não serão adicionados; cada parte nasce junto com a primeira funcionalidade que precisa dela.

O domínio e as portas não conhecem os adapters. Um módulo também não acessa diretamente o repository ou a entidade JPA de outro módulo. Quando essa comunicação for necessária, ela será feita por um contrato explícito.

`@Transactional` fica na fronteira Spring do caso de uso, e não nas classes de domínio. A implementação exata será definida com a primeira funcionalidade vertical.

## Consequências

- fica mais fácil localizar o código de uma área do sistema
- regras de domínio podem ser testadas sem subir Spring ou banco
- a troca de um detalhe técnico tende a ficar restrita ao adapter
- haverá algum código de mapeamento entre HTTP, domínio e persistência
- os limites só funcionam se forem respeitados nas revisões; quando os primeiros módulos existirem, adicionaremos testes de arquitetura para ajudar nisso
