# Portal do Aluno 🎵

Sistema web para gestão de uma escola de música particular com aulas a domicílio — matrícula de alunos, controle de responsáveis (menores de idade), gestão de funcionários e cargos, e autenticação com JWT.

Projeto desenvolvido do zero como estudo aplicado de engenharia de software: modelagem de domínio → arquitetura → banco de dados → backend, com decisões técnicas documentadas ao longo do processo.

---

## Stack

- **Java 21** + **Spring Boot 4**
- **PostgreSQL** — banco de dados relacional
- **Flyway** — controle de versão do schema do banco
- **Spring Data JPA / Hibernate** — persistência
- **Spring Security** + **JWT** (`jjwt`) — autenticação
- **BCrypt** — hash de senhas
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
 ├── auth             # autenticação compartilhada (login, JWT)
 └── shared           # configuração de segurança, serviços transversais
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
- Autenticação compartilhada entre `Aluno` e `Funcionário` via entidade `User`, com senhas nunca armazenadas em texto puro (BCrypt).
- Cadastro de funcionário sem senha inicial — a senha é definida posteriormente (fluxo de convite, em desenvolvimento).
- Relação N:N entre `Funcionário` e `Cargo`, permitindo acumular mais de um cargo.
- Login gera token JWT com validade de 24h.

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

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/aluno` | Cadastra um novo aluno (com responsável, se menor de idade) |
| `GET` | `/aluno?name=` | Busca alunos por nome |
| `POST` | `/funcionario` | Cadastra um novo funcionário com cargo(s) |
| `GET` | `/funcionario?name=` | Busca funcionários por nome |
| `GET` | `/cargo` | Lista cargos com os funcionários vinculados |
| `POST` | `/auth/login` | Autentica e retorna um token JWT |

---

## Roadmap

- [ ] Filtro de validação de JWT nas requisições (proteção efetiva das rotas autenticadas)
- [ ] Fluxo de convite por e-mail para funcionário definir senha
- [ ] Módulos de Aula, Relatório, Cronograma, Pagamento e Suporte
- [ ] Log de auditoria de ações administrativas
- [ ] Testes automatizados
- [ ] CI/CD com GitHub Actions
- [ ] Frontend em Angular

---

## Sobre o projeto

Construído como exercício deliberado de engenharia de software: cada decisão técnica (escolha de arquitetura, modelagem de entidades, regras de negócio) foi documentada e justificada durante o desenvolvimento, priorizando entender o "porquê" de cada escolha em vez de aplicar padrões sem questionar.
