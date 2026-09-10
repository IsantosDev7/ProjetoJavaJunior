# Portal do Aluno 🎵

Sistema web para gestão de uma escola de música particular com aulas a domicílio — matrícula de alunos, controle de responsáveis (menores de idade), gestão de funcionários e cargos, agendamento de aulas, relatórios de aula, cronograma semanal e autenticação com JWT.

Projeto desenvolvido do zero como estudo aplicado de engenharia de software: modelagem de domínio → arquitetura → banco de dados → backend, com decisões técnicas documentadas ao longo do processo.

---

## Stack

- **Java 21** + **Spring Boot 4**
- **PostgreSQL** — banco de dados relacional
- **Flyway** — controle de versão do schema do banco
- **Spring Data JPA / Hibernate** — persistência
- **Spring Security** + **JWT** (`jjwt`) — autenticação e autorização por role e por cargo
- **Bean Validation** (Hibernate Validator) — validação de dados de entrada, incluindo `@CPF`
- **BCrypt** — hash de senhas
- **JUnit 5** + **Mockito** — testes unitários das regras de negócio
- **Maven** — gerenciamento de dependências
- **spring-dotenv** — carregamento de variáveis de ambiente via `.env`
- **Asaas SDK** — integração de pagamentos (Pix), em configuração inicial

---

## Arquitetura

Monólito modular: uma única aplicação, mas organizada internamente em módulos de domínio independentes, cada um com suas próprias camadas.

```
com.example.portalaluno
 ├── aluno          # matrícula de alunos, regra de menor/maior de idade, soft delete
 ├── responsavel     # responsável legal (para alunos menores de idade)
 ├── funcionario      # cadastro de funcionários (professor, secretário, coordenador), soft delete
 ├── cargo            # cargos atribuíveis a funcionários (relação N:N)
 ├── aula             # agendamento de aulas (professor ↔ aluno)
 ├── relatorio        # relatório de aula, com autorização dinâmica (dono/coordenador/admin)
 ├── cronograma       # visão semanal das aulas, filtrada por aluno
 ├── pagamento        # controle de mensalidades (integração com Asaas em configuração)
 ├── auth             # autenticação compartilhada (login, JWT, seed do Super Admin)
 └── shared           # configuração de segurança, filtro JWT, tratamento de exceções, serviços transversais
```

Cada módulo segue o padrão de camadas:

```
Controller → Service → Repository → Entity
                ↑
              DTOs (Request / Response)
```

- **Controller** — expõe os endpoints REST, sem lógica de negócio, sem `try/catch`.
- **Service** — onde vive a regra de negócio (ex: validação de idade, reaproveitamento de cadastro de responsável, conversão para DTO via `toResponse`).
- **Repository** — acesso a dados via Spring Data JPA, sempre devolvendo Entity (nunca DTO).
- **DTOs** — separam o contrato da API (o que entra/sai por HTTP) da estrutura interna do banco.

A ordem de construção de cada fatia vertical nova segue sempre: **Entity → Migration → DTOs → Repository → Service (começando pelo `toResponse`) → Controller**.

---

## Principais regras de negócio implementadas

- Cadastro de aluno com fluxo condicional por idade: maior de idade informa o próprio CPF; menor de idade exige dados de um responsável.
- Reaproveitamento automático de cadastro de responsável por CPF (evita duplicar dados de pais/mães com mais de um filho matriculado).
- Autenticação compartilhada entre `Aluno`, `Funcionário` e Super Admin via entidade `User`, com senhas nunca armazenadas em texto puro (BCrypt).
- Validação completa de dados de entrada via Bean Validation: formato de nome, e-mail, senha forte (maiúscula + número + símbolo), CPF (`@CPF`, dígito verificador), telefone e CEP.
- Cadastro de funcionário sem senha inicial — a senha é definida posteriormente (fluxo de convite, em desenvolvimento).
- Relação N:N entre `Funcionário` e `Cargo`, permitindo acumular mais de um cargo.
- Login gera token JWT (24h de validade); filtro dedicado valida o token em toda requisição autenticada e popula o contexto de segurança do Spring.
- Autorização por role via `@PreAuthorize` (`hasRole`) e por cargo via `FuncionarioSecurity.temCargo(...)` (ex: só Coordenador ou Professor podem cadastrar aula).
- Autorização dinâmica por dono do recurso: `RelatorioSecurity.podeEditar(...)` permite que um Professor edite apenas os próprios relatórios, além de Coordenador e Super Admin.
- Soft delete (nunca exclusão real) via enum de status próprio por entidade: `Aluno` (`status_matricula`), `Funcionario` (`status_matricula`), `Relatorio` (`status`) — preserva rastro para auditoria futura.
- Super Admin criado automaticamente na inicialização via seed (`CommandLineRunner`), com credenciais vindas do `.env` — nunca hardcoded.
- Agendamento de aula vincula professor autenticado (extraído do token) e aluno.
- Relatório de aula: um relatório por aula, confirmação de leitura pelo aluno, edição restrita ao texto (não permite trocar aula/aluno após criado).
- Cronograma semanal: monta a visão de segunda a domingo a partir das aulas cadastradas, filtrado por aluno (o próprio aluno vê o seu; Super Admin pode consultar o de qualquer aluno).
- Todas as listagens paginadas preservam metadados de paginação (`Page<T>`), nunca descartados em `List<T>` simples.
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
ASAAS_API_KEY=sua_chave_sandbox_do_asaas
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
| `PUT` | `/aluno/perfil` | Atualiza o próprio cadastro | Aluno |
| `PUT` | `/aluno/{id}` | Atualiza cadastro de um aluno específico | Funcionário |
| `GET` | `/aluno?name=` | Busca alunos por nome (paginado) | Secretário / Coordenador / Super Admin |
| `PATCH` | `/aluno/{id}/aprovar` | Aprova cadastro pendente de aluno | Secretário / Coordenador / Super Admin |
| `PATCH` | `/aluno/{id}/cancelar` | Cancela matrícula (soft delete) | Coordenador / Super Admin |
| `POST` | `/funcionario` | Cadastra um novo funcionário com cargo(s) | Funcionário |
| `GET` | `/funcionario?name=` | Busca funcionários por nome | Funcionário |
| `PATCH` | `/funcionario/{id}/desativar` | Desativa funcionário (soft delete) | Super Admin |
| `GET` | `/cargo` | Lista cargos com os funcionários vinculados | Funcionário |
| `POST` | `/aula` | Agenda uma aula (professor extraído do token) | Professor / Coordenador / Super Admin |
| `PUT` | `/aula/{id}` | Atualiza uma aula | Professor / Coordenador / Super Admin |
| `PATCH` | `/aula/{id}/cancelar` | Cancela uma aula | Professor / Coordenador / Super Admin |
| `GET` | `/aula/minhas-aulas` | Lista as aulas do próprio aluno logado | Aluno |
| `GET` | `/aula/aluno/{id}` | Lista aulas de um aluno específico | Professor / Coordenador / Secretário / Super Admin |
| `GET` | `/aula/professor/{id}` | Lista aulas de um professor específico | Coordenador / Secretário / Super Admin |
| `POST` | `/relatorio` | Cria relatório de uma aula | Professor / Coordenador / Super Admin |
| `PUT` | `/relatorio/{id}` | Atualiza o texto de um relatório | Dono (Professor) / Coordenador / Super Admin |
| `PATCH` | `/relatorio/{id}/cancelar` | Cancela um relatório (soft delete) | Dono (Professor) / Coordenador / Super Admin |
| `PUT` | `/relatorio/confirmar-leitura/{id}` | Marca relatório como lido | Aluno dono da aula |
| `GET` | `/relatorio/meus` | Lista relatórios do usuário logado (professor ou aluno) | Autenticado |
| `GET` | `/relatorio/todos` | Lista todos os relatórios, com filtro por nome | Coordenador / Super Admin |
| `GET` | `/cronograma/meu?semana=` | Cronograma semanal do próprio aluno logado | Aluno |
| `GET` | `/cronograma/aluno/{id}?semana=` | Cronograma semanal de um aluno específico | Super Admin |
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
- [x] Autorização por role e por cargo (`@PreAuthorize` + `FuncionarioSecurity`)
- [x] Seed do Super Admin
- [x] Módulo de Aula
- [x] Validação completa de dados de entrada (Bean Validation) em Aluno, Responsavel e Funcionario
- [x] Soft delete — Aluno e Funcionario (via enum de status próprio por entidade)
- [x] Módulo de Relatório (criação, edição de texto, cancelamento, confirmação de leitura, autorização dinâmica por dono)
- [x] Módulo de Cronograma (visão semanal derivada de Aula, filtrada por aluno)
- [x] Configuração inicial do SDK Asaas (sandbox)
- [x] Soft delete — Responsavel (mesmo padrão já aplicado a Aluno/Funcionario)
- [ ] `GlobalExceptionHandler`: capturar `MethodArgumentNotValidException` para respostas de erro de validação mais claras
- [ ] Fluxo de convite por e-mail para funcionário definir senha (`TokenConvite`)
- [ ] Fluxo de solicitação/aprovação (ex: Coordenador solicita desligamento de Professor, Super Admin aprova) — nova entidade própria, distinta de `LogAção`
- [ ] Filtro de Professor em `GET /aluno` restrito a alunos vinculados (via Aula), hoje temporariamente sem acesso a essa rota
- [ ] Módulo de Pagamento (integração com Asaas/Pix)
- [ ] Módulo de Suporte (chamados)
- [ ] Log de auditoria de ações administrativas (`LogAção`)
- [ ] Expandir cobertura de testes automatizados
- [ ] CI/CD com GitHub Actions
- [ ] Frontend em Angular

---

## Sobre o projeto

Construído como exercício deliberado de engenharia de software: cada decisão técnica (escolha de arquitetura, modelagem de entidades, regras de negócio) foi documentada e justificada durante o desenvolvimento, priorizando entender o "porquê" de cada escolha em vez de aplicar padrões sem questionar.
