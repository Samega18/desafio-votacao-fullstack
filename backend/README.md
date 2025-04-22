# Sistema de Votação para Cooperativas

Este projeto implementa um sistema de votação para cooperativas, permitindo a criação de pautas, abertura de sessões de votação e contabilização de votos.

## Requisitos

- Java 21
- Maven 3.8+
- PostgreSQL (para ambiente de produção)
- Redis (opcional, para cache)

## Tecnologias Utilizadas

- Spring Boot 3.2.4
- Spring Data JPA
- Spring Web
- Spring Validation
- Spring Cache
- Lombok
- MapStruct
- Springdoc OpenAPI (Swagger)
- H2 Database (para ambiente de desenvolvimento)

## Funcionalidades Adicionais

### Validação de CPF (Bônus 1)
Implementação de um serviço fake que simula a validação de CPFs e retorna aleatoriamente se um associado está apto a votar.

### Otimização de Performance (Bônus 2)
- Cache em múltiplas camadas
- Otimização de queries com índices
- Paginação em resultados extensos
- Possibilidade de escalabilidade horizontal

### Versionamento da API (Bônus 3)
Adotado o versionamento via URI (ex: /api/v1/) para permitir evolução da API sem quebrar compatibilidade com clientes existentes.

## Configuração do Ambiente

### Banco de Dados

O sistema está configurado para utilizar:
- H2 Database (em memória) para o perfil de desenvolvimento
- PostgreSQL para o perfil de produção

### Perfis Disponíveis

- **dev**: Utiliza banco H2 em memória e carrega dados de exemplo para testes
- **prod**: Configurado para ambiente de produção com PostgreSQL

## Executando a Aplicação

### Via Maven

```bash
# Compilar o projeto
mvn clean install

# Executar no perfil de desenvolvimento
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Executar no perfil de produção
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Via Java

```bash
# Compilar o projeto
mvn clean install

# Executar no perfil de desenvolvimento
java -jar -Dspring.profiles.active=dev target/sistema-votacao-1.0-SNAPSHOT.jar

# Executar no perfil de produção
java -jar -Dspring.profiles.active=prod target/sistema-votacao-1.0-SNAPSHOT.jar
```

### Classe Principal

A aplicação é inicializada através da classe `com.cooperativa.sistema.votacao.SistemaVotacaoApplication`.

## Documentação da API

Após iniciar a aplicação, a documentação da API estará disponível em:

```
http://localhost:8080/swagger-ui.html
```

## Funcionalidades

- Cadastro de pautas para votação
- Abertura de sessões de votação com tempo configurável
- Registro de votos por associados
- Contabilização automática de resultados
- Consulta de resultados de votações
- Cadastro e Visualização de Associados
- Validação de CPF para Votar em Sessões(Fake)

## Estrutura do Projeto

- **controller**: Endpoints da API REST
- **service**: Lógica de negócio
- **repository**: Acesso a dados
- **domain**: Entidades do domínio
- **dto**: Objetos de transferência de dados
- **exception**: Exceções personalizadas
- **mapper**: Conversão entre entidades e DTOs

## Ambiente de Desenvolvimento

No perfil `dev`, o sistema carrega automaticamente dados de exemplo para facilitar os testes, incluindo:
- Associados
- Pautas
- Uma sessão de votação com votos registrados