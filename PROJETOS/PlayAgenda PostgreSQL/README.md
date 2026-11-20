# 📒 PlayAgenda (PostgreSQL / Heroku)

![Play Framework](https://img.shields.io/badge/Play_Framework-2.0-92D13D?style=for-the-badge&logo=playframework&logoColor=white)
![Java](https://img.shields.io/badge/Java-6%2F7-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Scala](https://img.shields.io/badge/Scala-2.9.1-DC322F?style=for-the-badge&logo=scala&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-9.x-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Heroku](https://img.shields.io/badge/Heroku-deploy-430098?style=for-the-badge&logo=heroku&logoColor=white)
![Status](https://img.shields.io/badge/status-legado%20(2013)-lightgrey?style=for-the-badge)

Agenda de contatos web multiusuário desenvolvida em **Java** com o **Play Framework 2.0**, usando **PostgreSQL** e preparada para **deploy no Heroku**. Projeto acadêmico desenvolvido na **Universidade FUMEC** em **maio de 2013**, como parte de uma pesquisa sobre o Play Framework.

É a versão "de produção" do [`PlayAgenda MYSQL`](../PlayAgenda%20MYSQL): mesma aplicação, adaptada para rodar na nuvem com PostgreSQL.

> ⚠️ **Projeto legado.** Este código foi escrito para o Play 2.0 (2012/2013), com sbt 0.11 e Scala 2.9.1, e para a antiga *stack Cedar* do Heroku. Ele é mantido aqui como registro histórico e material de estudo.

---

## 📑 Sumário

- [Funcionalidades](#-funcionalidades)
- [Diferenças em relação à versão MySQL](#-diferenças-em-relação-à-versão-mysql)
- [Tecnologias](#-tecnologias)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Rotas](#-rotas)
- [Como executar localmente](#-como-executar-localmente)
- [Deploy no Heroku (como era feito)](#-deploy-no-heroku-como-era-feito)
- [Documentação incluída](#-documentação-incluída)
- [Observações e limitações conhecidas](#-observações-e-limitações-conhecidas)
- [Autor](#-autor)

---

## ✨ Funcionalidades

- **Cadastro de usuários** (nome, e-mail, username e senha), com verificação de username duplicado.
- **Login / logout** com controle de sessão do Play.
- **Agenda individual por usuário** — cada usuário só enxerga e altera os próprios contatos.
- **CRUD de contatos**: inserir, editar, excluir e pesquisar (pelo nome).
- **Envio de e-mail** via SMTP do Gmail (Apache Commons Email), com registro da mensagem no banco.
- **Validações** no servidor: campos obrigatórios, formato de e-mail e telefone numérico.
- Página de **downloads** com a documentação, a pesquisa e os **códigos-fonte das duas versões** (MySQL e PostgreSQL) em `.zip`.
- Página com **vídeo** de apresentação do Play Framework.

---

## 🔄 Diferenças em relação à versão MySQL

| Item | PlayAgenda MYSQL | PlayAgenda PostgreSQL |
|---|---|---|
| Banco | MySQL local (`localhost/contato`) | PostgreSQL do Heroku (com SSL) |
| Driver | `mysql-connector-java` 5.1.18 | `postgresql` 9.1-901 / 9.2-1002 JDBC4 |
| Model de usuário | `Usuario.java` | `User.java` (mapeado na tabela `usuario`) |
| `db.default` no `application.conf` | Configurado | Não configurado (conexão só no controller) |
| Deploy | — | `Procfile` para o Heroku |
| Downloads | Documentação e pesquisa | + `ProjetoMySQL.zip` e `ProjetoPostgre.zip` |

---

## 🛠 Tecnologias

| Camada | Tecnologia | Versão |
|---|---|---|
| Framework web | Play Framework (Java API) | 2.0 |
| Build | sbt | 0.11.2 |
| Linguagem | Java + Scala (templates `*.scala.html`) | Java 6/7 · Scala 2.9.1 |
| Banco de dados | PostgreSQL (Heroku Postgres) | 9.x |
| Driver JDBC | `postgresql` | 9.1-901.jdbc4 (sbt) / 9.2-1002.jdbc4 (`lib/`) |
| E-mail | `play-plugins-mailer` / Apache Commons Email | 2.0.2 |
| Hospedagem | Heroku (Procfile) | — |
| Front-end | HTML, CSS, jQuery, jQuery Easing | — |

> O acesso aos dados é feito com **JDBC puro** (`DriverManager` + `PreparedStatement`).

---

## 📁 Estrutura do projeto

```
PlayAgenda PostgreSQL/
├── Procfile                      # Comando de inicialização no Heroku
├── app/
│   ├── controllers/
│   │   └── Application.java      # Todas as actions (login, CRUD, e-mail, páginas)
│   ├── models/
│   │   ├── Agenda.java           # Contato da agenda
│   │   ├── Email.java            # Mensagem de e-mail enviada
│   │   └── User.java             # Usuário do sistema
│   └── views/                    # Templates Scala
├── conf/
│   ├── application.conf
│   ├── routes
│   └── evolutions/default/1.sql
├── lib/
│   └── postgresql-9.2-1002.jdbc4.jar
├── project/
│   ├── Build.scala
│   ├── build.properties
│   └── plugins.sbt
└── public/
    ├── arquivo/                  # PDFs/DOCs + zips dos dois projetos
    ├── images/
    ├── javascripts/
    └── stylesheets/
```

---

## 🗄 Modelo de dados

A aplicação usa três tabelas. Script compatível com PostgreSQL:

```sql
CREATE TABLE usuario (
  nome      varchar(255),
  email     varchar(255),
  username  varchar(255),
  password  varchar(255)
);

CREATE TABLE agenda (
  nome      varchar(255),
  email     varchar(255),
  username  varchar(255),   -- dono do contato (usuário logado)
  telefone  varchar(255),
  endereco  varchar(255),
  facebook  varchar(255)
);

CREATE TABLE email (
  sender     varchar(255),
  recipient  varchar(255),
  subject    varchar(255),
  message    varchar(255)
);
```

> ⚠️ O arquivo `conf/evolutions/default/1.sql` versionado está **desatualizado**: não cria a tabela `usuario`, não tem a coluna `agenda.username` e usa `SET FOREIGN_KEY_CHECKS` (sintaxe do MySQL). Use o script acima para criar o banco.

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
| GET | `/downloads` | `pagDownloads()` | Documentos e códigos para download |
| GET | `/video` | `pagVideo()` | Vídeo sobre o Play Framework |
| GET | `/assets/*file` | `Assets.at(...)` | Arquivos estáticos |

---

## 🚀 Como executar localmente

### Pré-requisitos

- **JDK 6 ou 7**;
- **Play Framework 2.0.x** standalone (ex.: `play-2.0.4.zip`), disponível no [histórico de versões do Play](https://www.playframework.com/releases);
- **PostgreSQL** (qualquer versão recente aceita o script acima).

### Passos

1. Crie o banco e as tabelas:
   ```bash
   createdb playagenda
   psql playagenda -f schema.sql   # com o script da seção "Modelo de dados"
   ```
2. Edite o método `abrirConexao()` em `app/controllers/Application.java`, trocando a URL do Heroku pela do seu banco:
   ```java
   String url = "jdbc:postgresql://localhost:5432/playagenda";
   props.setProperty("user", "SEU_USUARIO");
   props.setProperty("password", "SUA_SENHA");
   ```
   (Para um banco local sem SSL, remova o parâmetro `sslfactory` da URL.)
3. Para testar o envio de e-mails, defina as variáveis de ambiente `SMTP_USER` e `SMTP_PASSWORD` (conta do Gmail com *senha de app*).
4. Rode:
   ```bash
   cd "PlayAgenda PostgreSQL"
   play run
   ```
5. Acesse **http://localhost:9000**.

---

## ☁️ Deploy no Heroku (como era feito)

O projeto foi publicado no Heroku em 2013, na *stack Cedar*, que tinha suporte nativo a aplicações Play 2.0. O fluxo era:

```bash
heroku create
heroku addons:add heroku-postgresql:dev
git push heroku master
```

O Heroku compilava o projeto com `play stage` e iniciava a aplicação com o comando do [`Procfile`](Procfile):

```
web: target/start -Dhttp.port=${PORT} ${JAVA_OPTS}
```

> Hoje a stack Cedar, o plano gratuito do Heroku Postgres e o buildpack do Play 2.0 não existem mais. Para publicar novamente seria necessário migrar o projeto para uma versão atual do Play (sbt 1.x, `sbt-native-packager`) e ler a URL do banco da variável `DATABASE_URL`.

---

## 📄 Documentação incluída

A pasta [`public/arquivo`](public/arquivo) contém (também disponíveis em `/downloads`):

- **DocumentacaoAplic** (`.pdf` / `.doc`) — documentação da aplicação;
- **PesquisaPlay** (`.pdf` / `.doc`) — pesquisa sobre o Play Framework;
- **ProjetoMySQL.zip** e **ProjetoPostgre.zip** — código-fonte das duas versões.

---

## ⚠️ Observações e limitações conhecidas

- **Credenciais do banco no código-fonte**: a URL, o usuário e a senha do banco do Heroku estão escritos diretamente em `Application.java`. O correto seria lê-los de variáveis de ambiente (`DATABASE_URL`), como já é feito com a conta SMTP (`SMTP_USER` / `SMTP_PASSWORD`).
- **Senhas em texto puro** na tabela `usuario` (sem hash).
- **Sem chaves primárias** nas tabelas; a identificação é feita por `nome`/`username`.
- O `PreparedStatement` é um campo `static` compartilhado no controller (não é seguro com requisições concorrentes).
- Conexões JDBC abertas manualmente a cada requisição, sem pool e sem fechamento em caso de exceção.
- Script de evolutions desatualizado (ver [Modelo de dados](#-modelo-de-dados)).
- A pasta `project/target` (artefatos do sbt) foi versionada junto com o código.

---

## 👤 Autor

**João Paulo Aramuni** — Universidade FUMEC, 2013.

- GitHub: [@joaopauloaramuni](https://github.com/joaopauloaramuni)

Licença: ver [LICENSE](../../LICENSE) na raiz do repositório.
