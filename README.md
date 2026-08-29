# Portal do Aluno 🎵

Sistema web para gestão de uma escola de música particular com aulas a domicílio — matrícula de alunos, controle de responsáveis (menores de idade), gestão de funcionários e cargos, e autenticação com JWT.

Projeto desenvolvido do zero como estudo aplicado de engenharia de software: modelagem de domínio → arquitetura → banco de dados → backend, com decisões técnicas documentadas ao longo do processo.

---

## Stack

- **Java 21** + **Spring Boot 4**
- **PostgreSQL** — banco de dados relacional
- **Flyway** — controle de versão do schema do banco
- **Spring Data JPA / Hibernate** — persistência
- **Spring Security** + **JWT** (`jjwt`) — autenticação e autorização por role
- **BCrypt** — hash de senhas
- **JUnit 5** + **Mockito** — testes unitários das regras de negócio
- **Maven** — gerenciamento de dependências
- **spring-dotenv** — carregamento de variáveis de ambiente via `.env`

---

## Arquitetura

Monólito modular: uma única aplicação, mas organizada internamente em módulos de domínio independentes, cada um com suas próprias camadas.

```
com.example.portalaluno
 ├── aluno          # matrícula de alunos, regra de menor/maior de idade
 ├── responsavel     # responsável legal (para alunos menores de idade)
 ├── funcionario      # cadastro de funcionários (professor, secretário, coordenador)
 ├── cargo            # cargos atribuíveis a funcionários (relação N:N)
 ├── aula             # agendamento de aulas (professor ↔ aluno)
 ├── pagamento        # controle de mensalidades (integração com Asaas planejada)
 ├── auth             # autenticação compartilhada (login, JWT, seed do Super Admin)
 └── shared           # configuração de segurança, filtro JWT, serviços transversais
```

Cada módulo segue o padrão de camadas:

```
Controller → Service → Repository → Entity
                ↑
              DTOs (Request / Response)
```

- **Controller** — expõe os endpoints REST, sem lógica de negócio.
- **Service** — onde vive a regra de negócio (ex: validação de idade, reaproveitamento de cadastro de responsável).
- **Repository** — acesso a dados via Spring Data JPA.
- **DTOs** — separam o contrato da API (o que entra/sai por HTTP) da estrutura interna do banco.

---

## Principais regras de negócio implementadas

- Cadastro de aluno com fluxo condicional por idade: maior de idade informa o próprio CPF; menor de idade exige dados de um responsável.
- Reaproveitamento automático de cadastro de responsável por CPF (evita duplicar dados de pais/mães com mais de um filho matriculado).
- Autenticação compartilhada entre `Aluno`, `Funcionário` e Super Admin via entidade `User`, com senhas nunca armazenadas em texto puro (BCrypt).
- Cadastro de funcionário sem senha inicial — a senha é definida posteriormente (fluxo de convite, em desenvolvimento).
- Relação N:N entre `Funcionário` e `Cargo`, permitindo acumular mais de um cargo.
- Login gera token JWT (24h de validade); filtro dedicado valida o token em toda requisição autenticada e popula o contexto de segurança do Spring.
- Autorização por role via `@PreAuthorize` (ex: busca de alunos restrita a funcionários).
- Super Admin criado automaticamente na inicialização via seed (`CommandLineRunner`), com credenciais vindas do `.env` — nunca hardcoded.
- Agendamento de aula vincula professor autenticado (extraído do token) e aluno.
- Regras críticas de cadastro cobertas por testes unitários (JUnit + Mockito).

---

## Rodando o projeto localmente

### Pré-requisitos
- JDK 21
- PostgreSQL rodando localmente
- Maven (ou usar o wrapper `./mvnw` incluso)

### Configuração

1. Crie um banco PostgreSQL vazio:
   ```sql
   CREATE DATABASE portal_aluno;
   ```

2. Crie um arquivo `.env` na raiz do projeto:
   ```
   DB_URL=jdbc:postgresql://localhost:5432/portal_aluno
   DB_USER=postgres
   DB_PASSWORD=sua_senha_aqui
   JWT_SECRET=uma_chave_secreta_longa_e_aleatoria
   ```

3. Rode a aplicação:
   ```
   ./mvnw spring-boot:run
   ```

O Flyway aplica as migrations automaticamente na primeira execução, criando o schema completo (incluindo os cargos iniciais: Professor, Secretário, Coordenador).

---

## Endpoints principais

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/aluno` | Cadastra um novo aluno (com responsável, se menor de idade) | Público |
| `GET` | `/aluno?name=` | Busca alunos por nome | Funcionário |
| `POST` | `/funcionario` | Cadastra um novo funcionário com cargo(s) | Funcionário |
| `GET` | `/funcionario?name=` | Busca funcionários por nome | Funcionário |
| `GET` | `/cargo` | Lista cargos com os funcionários vinculados | Funcionário |
| `POST` | `/aula` | Agenda uma aula (professor extraído do token) | Funcionário |
| `POST` | `/auth/login` | Autentica e retorna um token JWT | Público |

---

## Testes

```
./mvnw test
```

Testes unitários com JUnit 5 + Mockito, focados nas regras de negócio críticas do cadastro de aluno: validação de idade/responsável, reaproveitamento de e-mail duplicado, obrigatoriedade de senha.

---

## Roadmap

- [x] Filtro de validação de JWT nas requisições
- [x] Autorização por role
- [x] Seed do Super Admin
- [x] Módulo de Aula
- [ ] Fluxo de convite por e-mail para funcionário definir senha
- [ ] Módulo de Pagamento (integração com Asaas/Pix)
- [ ] Módulos de Relatório, Cronograma e Suporte
- [ ] Log de auditoria de ações administrativas
- [ ] Expandir cobertura de testes automatizados
- [ ] CI/CD com GitHub Actions
- [ ] Frontend em Angular

---

## Sobre o projeto

Construído como exercício deliberado de engenharia de software: cada decisão técnica (escolha de arquitetura, modelagem de entidades, regras de negócio) foi documentada e justificada durante o desenvolvimento, priorizando entender o "porquê" de cada escolha em vez de aplicar padrões sem questionar.
