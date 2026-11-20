# 🚗 Auto Help — Sistema para Oficina Mecânica

![Play Framework](https://img.shields.io/badge/Play_Framework-2.0.2-92D13D?style=for-the-badge&logo=playframework&logoColor=white)
![Java](https://img.shields.io/badge/Java-6%2F7-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Scala](https://img.shields.io/badge/Scala-2.9.1-DC322F?style=for-the-badge&logo=scala&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-9.x-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Status](https://img.shields.io/badge/status-legado%20(2013%2F2014)-lightgrey?style=for-the-badge)

**Auto Help** é um sistema web para oficinas mecânicas desenvolvido em **Java** com o **Play Framework 2.0.2** e **PostgreSQL**. Ele reúne um **site institucional** (serviços, peças, orçamentos, contato) e um **painel administrativo** para cadastro de clientes, veículos, funcionários, peças, serviços e atendimentos.

Projeto **freelancer** desenvolvido para uma oficina mecânica entre **2013 e 2014**.

> ⚠️ **Projeto legado.** Escrito para o Play 2.0.2, sbt 0.11.3 e Scala 2.9.1. Mantido como registro histórico e material de estudo; algumas funcionalidades ficaram incompletas (ver [limitações](#-observações-e-limitações-conhecidas)).

---

## 📑 Sumário

- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Rotas](#-rotas)
- [Como executar](#-como-executar)
- [Observações e limitações conhecidas](#-observações-e-limitações-conhecidas)
- [Autor](#-autor)

---

## ✨ Funcionalidades

### 🌐 Site institucional (público)

- Página inicial com slider de imagens (*FlexSlider*), destaques de serviços, peças, parceiros e fotos;
- **Quem Somos**, **Serviços**, **Peças**, **Orçamentos**;
- Páginas de serviços específicos: **Mecânico**, **Manutenção**, **Manutenção Preventiva** e **Pneus**;
- **Contato** com mapa e formulário **Fale Conosco**, que grava a mensagem no banco e a envia por e-mail (SMTP do Gmail);
- Links para redes sociais.

### 🔐 Painel administrativo (restrito)

Acesso por login (`/admin`), com controle de sessão. Páginas restritas redirecionam para a home com o aviso *"Acesso Restrito!"* quando não há usuário logado.

| Módulo | O que faz |
|---|---|
| **Usuários** | Cria login/senha vinculados a um funcionário (um usuário por funcionário). |
| **Funcionários** | Cadastro com nome, CPF, endereço, e-mail, nascimento, telefones, salário e data de admissão. |
| **Clientes** | Cadastro com nome, CPF (com **validação dos dígitos verificadores**), endereço, e-mail, data e telefones. |
| **Veículos** | Marca, modelo, placa, ano de fabricação/modelo, km, óleo e foto, vinculados a um cliente. |
| **Peças** | Nome e preço. |
| **Serviços** | Um serviço é composto por uma ou mais peças com quantidade e valor. |
| **Atendimentos** | Ordem de serviço: liga veículo (pela placa), funcionário e serviços, com desconto, data de entrada/saída e status *finalizado*. Atendimentos em aberto de um mesmo veículo são agrupados. |
| **Relatórios** | Página reservada para relatórios (não implementada). |

Os formulários usam **combos** (listas suspensas) carregados do banco: clientes, peças, serviços, funcionários e placas de veículos.

---

## 🛠 Tecnologias

| Camada | Tecnologia | Versão |
|---|---|---|
| Framework web | Play Framework (Java API) | 2.0.2 |
| Build | sbt | 0.11.3 |
| Linguagem | Java + Scala (templates `*.scala.html`) | Java 6/7 · Scala 2.9.1 |
| Banco de dados | PostgreSQL (Heroku Postgres na época) | 9.x |
| Driver JDBC | `postgresql` | 9.1-901.jdbc4 (sbt) / 9.2-1002.jdbc4 (`lib/`) |
| E-mail | `play-plugins-mailer` / Apache Commons Email | 2.0.2 |
| Validação | `validation-api` (Bean Validation) | 1.0.0.GA |
| Front-end | HTML, CSS, jQuery 1.8, FlexSlider, Modernizr, fontes customizadas | — |

> O acesso aos dados é feito com **JDBC puro** (`DriverManager` + `PreparedStatement`). Os models usam anotações JPA/Ebean apenas para o binding dos formulários.

---

## 📁 Estrutura do projeto

```
PlayAutoHelp/
├── BD.txt                        # Script SQL de criação do banco
├── app/
│   ├── controllers/
│   │   └── Application.java      # Páginas, actions de cadastro, combos, validações, e-mail
│   ├── models/
│   │   ├── Atend.java            # Atendimento / ordem de serviço
│   │   ├── Cliente.java
│   │   ├── Email.java
│   │   ├── Func.java             # Funcionário
│   │   ├── Peca.java
│   │   ├── Servico.java
│   │   ├── User.java             # Usuário do painel
│   │   └── Veiculo.java
│   └── views/
│       ├── main.scala.html       # Layout base
│       ├── index, quem, serv, pec, orc, mec, manu, pneu, prev, contato, email   # site público
│       └── admin, painel, user, func, cliente, veiculo, peca, servico, atend, relatorios  # painel
├── conf/
│   ├── application.conf
│   └── routes
├── lib/                          # JARs: driver PostgreSQL, mailer, validation-api
├── project/
│   ├── Build.scala
│   ├── build.properties
│   └── plugins.sbt
└── public/
    ├── images/
    ├── javascripts/              # jQuery, FlexSlider, Modernizr, functions.js
    └── stylesheets/              # style.css, flexslider.css, fontes .ttf
```

---

## 🗄 Modelo de dados

O script completo está em [`BD.txt`](BD.txt). Resumo das tabelas:

```
funcionario (id_func PK, nome, cpf, endereco, email, data, telefone, celular, salario, data_ad)
usuario     (id_usuario PK, id_func FK → funcionario, username, password)
cliente     (id_cliente PK, nome, cpf, endereco, email, data, telefone, celular)
veiculo     (id_veic PK, id_cliente FK → cliente, marca, modelo, placa, ano_f, ano_m, km, oleo, foto)
peca        (id_peca PK, nome, preco)
servico     (id_serv, id_peca, qtd_peca, nome, valor)            PK (id_serv, id_peca)
atend       (id_atend, id_serv, id_func FK, id_veic FK, vlr_total,
             desconto, data_e, data_s, finalizado)               PK (id_atend, id_serv)
email       (sender, recipient, subject, message)
log         (id_log PK, id_usuario FK → usuario, data, acao)
```

```
cliente 1───N veiculo 1───N atend N───1 funcionario 1───1 usuario
                              │
                              N
                           servico N───1 peca
```

- Um **serviço** é um conjunto de linhas em `servico` com o mesmo `id_serv`, uma por peça utilizada.
- Um **atendimento** é um conjunto de linhas em `atend` com o mesmo `id_atend`, uma por serviço realizado. Enquanto `finalizado = 'N'`, novos serviços do mesmo veículo entram no atendimento aberto.

> 💡 Ao rodar o `BD.txt`, crie a tabela `funcionario` **antes** de `usuario` (a FK depende dela) e cadastre um funcionário com `id_func = 1` antes do `insert` do usuário inicial. O `insert into peca` do final do arquivo tem uma coluna a mais e pode ser descartado.

---

## 🔀 Rotas

### Site público

| Método | URL | Página |
|---|---|---|
| GET / POST | `/` | Home |
| GET | `/quem` | Quem Somos |
| GET | `/serv` | Serviços |
| GET | `/pec` | Peças |
| GET | `/orc` | Orçamentos |
| GET | `/mec` | Mecânico |
| GET | `/manu` | Manutenção |
| GET | `/prev` | Manutenção Preventiva |
| GET | `/pneu` | Pneus |
| GET | `/contato` | Contato / mapa |
| GET / POST | `/email` | Fale Conosco (envio de e-mail) |

### Painel administrativo

| Método | URL | Action |
|---|---|---|
| GET / POST | `/admin` | Login (`action_admin`) |
| GET | `/painel` | Painel de controle |
| GET | `/logout` | Logout |
| GET / POST | `/user` | Usuários (`action_user`) |
| GET / POST | `/func` | Funcionários (`action_func`) |
| GET / POST | `/cliente` | Clientes (`action_cliente`) |
| GET / POST | `/veiculo` | Veículos (`action_veic`) |
| GET / POST | `/peca` | Peças (`action_peca`) |
| GET / POST | `/servico` | Serviços (`action_serv`) |
| GET / POST | `/atend` | Atendimentos (`action_atend`) |
| GET | `/relatorios` | Relatórios |
| POST | `/consulta` | Consulta (stub) |
| GET | `/assets/*file` | Arquivos estáticos |

---

## 🚀 Como executar

### Pré-requisitos

- **JDK 6 ou 7** (o Play 2.0 não roda em JDKs modernos);
- **Play Framework 2.0.2+** standalone (ex.: `play-2.0.4.zip`), disponível no [histórico de versões do Play](https://www.playframework.com/releases);
- **PostgreSQL**.

### Passos

1. Crie o banco e rode o script `BD.txt` (atenção à ordem das tabelas — ver [Modelo de dados](#-modelo-de-dados)):
   ```bash
   createdb autohelp
   psql autohelp -f BD.txt
   ```
2. Crie um funcionário e um usuário para acessar o painel:
   ```sql
   INSERT INTO funcionario (nome) VALUES ('Administrador');
   INSERT INTO usuario (id_func, username, password) VALUES (1, 'admin', 'admin');
   ```
3. No `app/controllers/Application.java`, ajuste o método `abrirConexao()` para o seu banco:
   ```java
   String url = "jdbc:postgresql://localhost:5432/autohelp";
   props.setProperty("user", "SEU_USUARIO");
   props.setProperty("password", "SUA_SENHA");
   // remova props.setProperty("ssl", "true") para um banco local sem SSL
   ```
4. Se quiser testar o Fale Conosco, defina as variáveis de ambiente `SMTP_USER` e `SMTP_PASSWORD` (conta do Gmail com *senha de app*) e ajuste o destinatário fixo em `action_sendEmail()`.
5. Rode:
   ```bash
   cd PlayAutoHelp
   play run
   ```
6. Acesse **http://localhost:9000** (site) e **http://localhost:9000/admin** (painel).

---

## ⚠️ Observações e limitações conhecidas

O projeto ficou parcialmente concluído. Quem for estudar ou reaproveitar o código deve saber que:

- **Cadastro de atendimento com erro**: o `INSERT` de `action_atend` tem 8 parâmetros (`?`), mas o código preenche 9 (inclui `finalizado`), o que gera exceção ao salvar. Basta acrescentar um `?` na instrução.
- **Valor total do atendimento não é calculado** (`vlr_total` fica vazio — há um comentário `//Calcular` no código).
- **Relatórios** e a rota `/consulta` não foram implementados.
- A tabela `log` existe no banco, mas não é usada pela aplicação.
- **Credenciais do banco fixas no código**: ficam em `Application.java` (hoje com os *placeholders* `SEU_HOST`, `SEU_BANCO`, `SEU_USUARIO` e `SUA_SENHA`); o ideal é usar variáveis de ambiente, como já é feito com a conta SMTP (`SMTP_USER` / `SMTP_PASSWORD`).
- **Senhas em texto puro** na tabela `usuario`.
- Todos os campos numéricos e de data são `varchar`.
- O `PreparedStatement` é um campo `static` compartilhado e as conexões JDBC não usam pool.
- A pasta `project/target` (artefatos do sbt) foi versionada junto com o código.

---

## 👤 Autor

**João Paulo Aramuni** — projeto freelancer para oficina mecânica, 2013/2014.

- GitHub: [@joaopauloaramuni](https://github.com/joaopauloaramuni)

Licença: ver [LICENSE](../../LICENSE) na raiz do repositório.
