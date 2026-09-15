# Portal do Aluno 🎵

Sistema web para gestão de uma escola de música particular com aulas a domicílio — matrícula de alunos, controle de responsáveis (menores de idade), gestão de funcionários e cargos, agendamento de aulas, relatórios e cronograma.

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
- **OWASP Sanitizer** — proteção contra XSS (sanitização de HTML)

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
 ├── auth             # autenticação compartilhada (login, JWT, seed do Super Admin, convite de funcionário)
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

- **Cadastro de aluno com fluxo condicional por idade**: maior de idade informa o próprio CPF; menor de idade exige dados de um responsável.
- **Reaproveitamento automático de cadastro de responsável por CPF**: evita duplicar dados de pais/mães com mais de um filho matriculado.
- **Autenticação compartilhada**: entre `Aluno`, `Funcionário` e Super Admin via entidade `User`, com senhas nunca armazenadas em texto puro (BCrypt).
- **Validação completa de dados**: formato de nome, e-mail, senha forte (maiúscula + número + símbolo), CPF (`@CPF`, dígito verificador), telefone e CEP.
- **Cadastro de funcionário sem senha inicial**: a senha é definida posteriormente via fluxo de convite por e-mail — Super Admin cadastra funcionário, sistema gera token UUID único, envia e-mail com link, funcionário clica e define sua própria senha com validação forte (maiúscula + número + símbolo + 8+ caracteres), token expira em 24h e é deletado após uso (one-time use), pode ser reenviado se expirado.
- **Relação N:N entre Funcionário e Cargo**: permite acumular mais de um cargo (ex: Professor + Coordenador).
- **JWT com 24h de validade**: filtro dedicado valida o token em toda requisição autenticada e popula o contexto de segurança do Spring.
- **Autorização em múltiplas camadas**:
  - Por role via `@PreAuthorize("hasRole()")`
  - Por cargo via `FuncionarioSecurity.temCargo(...)`
  - Dinâmica por dono via `RelatorioSecurity.podeEditar(...)`
- **Soft delete** (nunca exclusão real): via enum de status próprio por entidade (`Aluno.status_matricula`, `Funcionario.status_matricula`, `Relatorio.status`) — preserva rastro para auditoria.
- **Super Admin criado automaticamente**: na inicialização via seed (`CommandLineRunner`), com credenciais vindas do `.env` — nunca hardcoded.
- **Agendamento de aula**: vincula professor autenticado (extraído do token) e aluno.
- **Relatório de aula**: um por aula, confirmação de leitura pelo aluno, edição restrita ao texto (não permite trocar aula/aluno após criado).
- **Cronograma semanal**: monta a visão de segunda a domingo a partir das aulas cadastradas, filtrado por aluno (o próprio aluno vê o seu; Super Admin pode consultar o de qualquer aluno).
- **Paginação preservada**: todas as listagens retornam `Page<T>`, nunca `List<T>` simples.
- **Regras críticas cobertas por testes**: JUnit 5 + Mockito focados em validação de idade/responsável.

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

```env
# Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/portal_aluno
DB_USER=postgres
DB_PASSWORD=sua_senha_aqui

# JWT
JWT_SECRET=uma_chave_secreta_longa_e_aleatoria_minimo_32_caracteres

# Super Admin (inicialização)
ADMIN_EMAIL=admin@portalaluno.com
ADMIN_PASSWORD=SenhaForte123!

# Asaas (Pagamentos)
ASAAS_API_KEY=sua_chave_sandbox_do_asaas
```

3. Rode a aplicação:

```bash
./mvnw spring-boot:run
```

O Flyway aplica as migrations automaticamente na primeira execução, criando o schema completo (incluindo os cargos iniciais: Professor, Secretário, Coordenador, Administrador).

---

## Endpoints principais

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/auth/login` | Autentica e retorna um token JWT | Público |
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
| `POST` | `/convite/aceitar` | Aceita convite e define senha do funcionário | Público |
| `POST` | `/convite/reenviar` | Reenvia convite se o anterior expirou | Público |

---

## Testes

```bash
./mvnw test
```

Testes unitários com JUnit 5 + Mockito, focados nas regras de negócio críticas:
- Validação de idade / exigência de responsável
- Reaproveitamento de cadastro de responsável por CPF
- Autenticação e geração de JWT
- Autorização por role e cargo
- Validação de dados de entrada

---

## Segurança implementada

- ✅ **Senhas com BCrypt**: nunca armazenadas em texto puro
- ✅ **JWT com expiração**: 24 horas de validade
- ✅ **Filtro dedicado**: valida token e popula contexto do Spring Security
- ✅ **Sanitização de HTML**: proteção contra XSS via OWASP Sanitizer
- ✅ **Validação de entrada**: Bean Validation em todos os DTOs
- ✅ **Autorização granular**: role + cargo + dono do recurso
- ✅ **Variáveis de ambiente**: credenciais do `.env`, nunca hardcoded
- ✅ **Token de convite seguro**: UUID aleatório, one-time use, 24h expiração, não permite reutilização

---

## Roadmap

### ✅ Concluído
- [x] Filtro de validação de JWT nas requisições
- [x] Autorização por role e por cargo (`@PreAuthorize` + `FuncionarioSecurity`)
- [x] Seed do Super Admin
- [x] Módulo de Aula
- [x] Validação completa de dados de entrada (Bean Validation)
- [x] Soft delete — Aluno e Funcionário
- [x] Módulo de Relatório (criação, edição, cancelamento, confirmação de leitura)
- [x] Módulo de Cronograma (visão semanal)
- [x] Configuração inicial do SDK Asaas (sandbox)
- [x] Soft delete — Responsável
- [x] Fluxo de convite por e-mail para funcionário definir senha (`TokenConvite`)

### 🔄 Em Progresso
- [ ] `GlobalExceptionHandler`: capturar `MethodArgumentNotValidException` para respostas de erro de validação mais claras

### 📋 Planejado
- [ ] Fluxo de solicitação/aprovação (ex: Coordenador solicita desligamento de Professor)
- [ ] Filtro de Professor em `GET /aluno` restrito a alunos vinculados via Aula
- [ ] Módulo de Pagamento (integração completa com Asaas/Pix)
- [ ] Módulo de Suporte (chamados)
- [ ] Log de auditoria de ações administrativas
- [ ] Expandir cobertura de testes automatizados (target: 80%+)
- [ ] CI/CD com GitHub Actions
- [ ] Frontend em Angular

---

## Sobre o projeto

Construído como exercício deliberado de engenharia de software: cada decisão técnica (escolha de arquitetura, modelagem de entidades, regras de negócio) foi documentada e justificada. O foco é aprender práticas enterprise enquanto constrói um sistema real e funcional.

**Última atualização**: 13 de setembro de 2026  
**Mantém**: [IsantosDev7](https://github.com/IsantosDev7)
