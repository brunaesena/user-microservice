# Desafio Técnico - CRUD de Usuários e Tarefas

Este projeto é uma aplicação de microserviços construída para demonstrar um CRUD completo de **usuários** e **tarefas**, com backend em **Java Spring Boot**, frontend em **Angular**, e integração via **Docker** para facilitar o desenvolvimento local.

---

## 📁 Estrutura do Projeto

```
root/
├── frontend-angular-desafio   # Frontend Angular
├── infra                      # Infraestrutura Docker (banco de dados, containers)
├── task-microservice          # Microsserviço de tarefas (Spring Boot)
└── user-microservice          # Microsserviço de usuários (Spring Boot)
```

---

## 🚀 Como rodar o projeto localmente

### 1. Clone também o repositório de infraestrutura

Este projeto depende de um segundo repositório chamado `infra`, que deve estar no mesmo nível de pastas:

```bash
git clone https://github.com/brunaesena/user-microservice
git clone https://github.com/brunaesena/infra
```

### 2. Suba os containers com Docker Compose

Dentro da pasta `infra`, execute:

```bash
docker-compose up --build
```

Isso irá subir:
- Container `user-service` a partir da pasta `../user-microservice`
- Container `task-service` (caso configurado)
- Banco de dados PostgreSQL

### 3. Acesse os microsserviços

- Usuários: http://localhost:8081/api/users
- Tarefas: http://localhost:8082/api/tasks

---

## 📦 Tecnologias Utilizadas

- Java 17 + Spring Boot
- Angular 16
- PostgreSQL
- Docker e Docker Compose
- JUnit 5
- Testcontainers

---

## 🧩 Arquitetura da Aplicação

A aplicação backend foi desenvolvida com **Spring Boot**, seguindo o **padrão de projeto MVC (Model-View-Controller)**. A comunicação entre as camadas é feita de forma desacoplada utilizando **DTOs (Data Transfer Objects)** para garantir uma melhor organização e segurança dos dados trafegados.

## 🧪 Rodando os Testes

Para executar os testes (unitários e de integração), utilize o seguinte comando na raiz da pasta do microsserviço:

```bash
mvn clean test
```

Esse comando irá compilar o projeto e executar todos os testes disponíveis, incluindo:

- Testes de unidade dos serviços (`UserService`)
- Testes de integração dos controllers e repositories, com banco de dados real via **Testcontainers**

## 📋 Objetivo

O projeto foi desenvolvido como parte de um desafio técnico com foco em:

- Criação, atualização, listagem e exclusão de **usuários**
- Gerenciamento completo de **tarefas associadas a usuários**
- Separação de responsabilidades em microserviços
- Boa cobertura de testes e integração com containers

---

## 📃 Documentação - Swagger

- A documentação dos endpoints está disponível em `user-microservice-openapi.yaml` e pode ser visualizada com https://editor.swagger.io/

---

## 📌 Notas Finais

- Certifique-se de que as portas `8081`, `8082`, `5432` e `5433` estão livres no seu ambiente local.
- Esse microsserviço se comunica com o `task-microservice`, portanto é recomendado subir ambos via Docker.
- O frontend está configurado para consumir os microsserviços via HTTP.

---
