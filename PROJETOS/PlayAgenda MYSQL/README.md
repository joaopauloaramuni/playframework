# 📒 PlayAgenda (MySQL)

![Play Framework](https://img.shields.io/badge/Play_Framework-2.0-92D13D?style=for-the-badge&logo=playframework&logoColor=white)
![Java](https://img.shields.io/badge/Java-6%2F7-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Scala](https://img.shields.io/badge/Scala-2.9.1-DC322F?style=for-the-badge&logo=scala&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-5.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Status](https://img.shields.io/badge/status-legado%20(2013)-lightgrey?style=for-the-badge)

Agenda de contatos web multiusuário desenvolvida em **Java** com o **Play Framework 2.0**, usando **MySQL** como banco de dados. Projeto acadêmico desenvolvido na **Universidade FUMEC** em **maio de 2013**, como parte de uma pesquisa sobre o Play Framework.

> ⚠️ **Projeto legado.** Este código foi escrito para o Play 2.0 (2012/2013), com sbt 0.11 e Scala 2.9.1. Ele é mantido aqui como registro histórico e material de estudo — não é compatível com versões atuais do Play sem migração.

Existe uma versão irmã deste projeto usando PostgreSQL e preparada para deploy no Heroku: [`PlayAgenda PostgreSQL`](../PlayAgenda%20PostgreSQL).

---

## 📑 Sumário

- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Rotas](#-rotas)
- [Como executar](#-como-executar)
- [Documentação incluída](#-documentação-incluída)
- [Observações e limitações conhecidas](#-observações-e-limitações-conhecidas)
- [Autor](#-autor)

---

## ✨ Funcionalidades

- **Cadastro de usuários** (nome, e-mail, username e senha), com verificação de username duplicado.
- **Login / logout** com controle de sessão (o `username` fica salvo na sessão do Play).
- **Agenda individual por usuário** — cada usuário só enxerga e altera os próprios contatos.
- **CRUD de contatos**:
  - Inserir contato (nome, e-mail, telefone, endereço e Facebook);
  - Editar contato (localizado pelo nome);
  - Excluir contato (pelo nome);
  - Pesquisar contato (pelo nome).
- **Envio de e-mail** pela aplicação via SMTP do Gmail (Apache Commons Email), com registro da mensagem no banco.
- **Validações** no servidor: campos obrigatórios, formato de e-mail e telefone apenas numérico.
- Página de **downloads** (documentação e pesquisa sobre o Play) e página com **vídeo** de apresentação do framework.
- Interface com menu animado em jQuery (*jimgMenu*) e barra de login.

---

## 🛠 Tecnologias

| Camada | Tecnologia | Versão |
|---|---|---|
| Framework web | Play Framework (Java API) | 2.0 |
| Build | sbt | 0.11.2 |
| Linguagem | Java + Scala (templates `*.scala.html`) | Java 6/7 · Scala 2.9.1 |
| Banco de dados | MySQL | 5.x |
| Driver JDBC | `mysql-connector-java` | 5.1.18 |
| ORM (apenas DDL) | Ebean (incluído no Play 2.0) | — |
| E-mail | `play-plugins-mailer` / Apache Commons Email | 2.0.2 |
| Front-end | HTML, CSS, jQuery, jQuery Easing | — |

> O acesso aos dados é feito com **JDBC puro** (`DriverManager` + `PreparedStatement`). O Ebean é usado apenas para gerar o DDL inicial das tabelas (evolutions).

---

## 📁 Estrutura do projeto

```
PlayAgenda MYSQL/
├── app/
│   ├── controllers/
│   │   └── Application.java      # Todas as actions (login, CRUD, e-mail, páginas)
│   ├── models/
│   │   ├── Agenda.java           # Contato da agenda
│   │   ├── Email.java            # Mensagem de e-mail enviada
│   │   └── Usuario.java          # Usuário do sistema
│   └── views/                    # Templates Scala (main, index, inserir, editar,
│                                 # deletar, procurar, registrar, email, downloads, video)
├── conf/
│   ├── application.conf          # Configuração da aplicação e do banco
│   ├── routes                    # Mapeamento de URLs → actions
│   └── evolutions/default/1.sql  # Script de criação das tabelas
├── lib/
│   └── mysql-connector-java-5.1.18-bin.jar
├── project/
│   ├── Build.scala               # Nome, versão e dependências
│   ├── build.properties          # Versão do sbt
│   └── plugins.sbt               # Plugin do Play
└── public/
    ├── arquivo/                  # Documentação e pesquisa (PDF/DOC)
    ├── images/
    ├── javascripts/
    └── stylesheets/
```

---

## 🗄 Modelo de dados

Definido em [`conf/evolutions/default/1.sql`](conf/evolutions/default/1.sql):

```sql
create table usuario (
  nome      varchar(255),
  email     varchar(255),
  username  varchar(255),
  password  varchar(255)
);

create table agenda (
  nome      varchar(255),
  email     varchar(255),
  username  varchar(255),   -- dono do contato (usuário logado)
  telefone  varchar(255),
  endereco  varchar(255),
  facebook  varchar(255)
);

create table email (
  sender     varchar(255),
  recipient  varchar(255),
  subject    varchar(255),
  message    varchar(255)
);
```

A ligação entre contato e usuário é feita pela coluna `agenda.username`. Um mesmo usuário não pode ter dois contatos com o mesmo nome.

---

## 🔀 Rotas

| Método | URL | Action | Descrição |
|---|---|---|---|
| GET | `/` | `index()` | Página inicial / login |
| POST | `/` | `login()` | Autentica o usuário |
| GET | `/logout` | `logout()` | Encerra a sessão |
| GET / POST | `/register` | `pagRegistrar()` / `action_registrar()` | Cadastro de usuário |
| GET / POST | `/insert` | `pagInserir()` / `action_insert()` | Inserir contato |
| GET / POST | `/edit` | `pagEditar()` / `action_update()` | Editar contato |
| GET / POST | `/del` | `pagDeletar()` / `action_delete()` | Excluir contato |
| GET / POST | `/search` | `pagProcurar()` / `action_search()` | Pesquisar contato |
| GET / POST | `/email` | `pagEmail()` / `action_sendEmail()` | Enviar e-mail |
| GET | `/downloads` | `pagDownloads()` | Documentos para download |
| GET | `/video` | `pagVideo()` | Vídeo sobre o Play Framework |
| GET | `/assets/*file` | `Assets.at(...)` | Arquivos estáticos |

---

## 🚀 Como executar

Por ser um projeto de 2013, é necessário reproduzir um ambiente antigo.

### Pré-requisitos

- **JDK 6 ou 7** (o Play 2.0 não roda em JDKs modernos);
- **Play Framework 2.0.x** — distribuição *standalone* (ex.: `play-2.0.4.zip`), disponível no [histórico de versões do Play](https://www.playframework.com/releases);
- **MySQL 5.x**.

### 1. Criar o banco

```sql
CREATE DATABASE contato;
```

As tabelas podem ser criadas manualmente com o script `conf/evolutions/default/1.sql` ou automaticamente pelo Play (ele oferece aplicar as *evolutions* na primeira execução).

### 2. Configurar a conexão

A conexão aparece em **dois lugares**, e ambos precisam apontar para o seu banco:

- `conf/application.conf`:
  ```properties
  db.default.driver=com.mysql.jdbc.Driver
  db.default.url="jdbc:mysql://localhost/contato"
  db.default.user=SEU_USUARIO
  db.default.pass=SUA_SENHA
  ```
- método `abrirConexao()` em `app/controllers/Application.java` (URL, usuário e senha do JDBC).

Para o envio de e-mails, defina as variáveis de ambiente `SMTP_USER` e `SMTP_PASSWORD` com uma conta do Gmail (hoje é necessário usar uma *senha de app*):

```bash
export SMTP_USER=sua.conta@gmail.com
export SMTP_PASSWORD=sua_senha_de_app
```

### 3. Rodar

```bash
cd "PlayAgenda MYSQL"
play run
```

A aplicação sobe em **http://localhost:9000**.

---

## 📄 Documentação incluída

A pasta [`public/arquivo`](public/arquivo) contém os documentos originais do trabalho, também disponíveis na página `/downloads`:

- **DocumentacaoAplic** (`.pdf` / `.doc`) — documentação da aplicação;
- **PesquisaPlay** (`.pdf` / `.doc`) — pesquisa sobre o Play Framework.

---

## ⚠️ Observações e limitações conhecidas

Pontos que refletem a época e o caráter acadêmico do projeto — importantes caso alguém queira reaproveitar o código:

- **Credenciais do banco fixas no código**: usuário/senha do MySQL ficam em `application.conf` e `Application.java` (hoje com os *placeholders* `SEU_USUARIO` / `SUA_SENHA`, que precisam ser trocados). O ideal é movê-los para variáveis de ambiente, como já é feito com a conta SMTP (`SMTP_USER` / `SMTP_PASSWORD`).
- **Senhas em texto puro** na tabela `usuario` (sem hash).
- **Sem chaves primárias** nas tabelas; a identificação é feita por `nome`/`username`.
- O `PreparedStatement` é um campo `static` compartilhado no controller, o que não é seguro com requisições concorrentes.
- Conexões JDBC são abertas manualmente a cada requisição, sem pool e sem fechamento em caso de exceção.
- A pasta `project/target` (artefatos de build do sbt) foi versionada junto com o código.
- O Play 2.0 está descontinuado; para rodar em versões atuais seria preciso migrar para sbt 1.x, Play 2.8+/3.x e reescrever os templates e controllers.

---

## 👤 Autor

**João Paulo Aramuni** — Universidade FUMEC, 2013.

- GitHub: [@joaopauloaramuni](https://github.com/joaopauloaramuni)

Licença: ver [LICENSE](../../LICENSE) na raiz do repositório.
