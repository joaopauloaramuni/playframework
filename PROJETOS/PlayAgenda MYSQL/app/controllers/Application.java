package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.apache.commons.mail.SimpleEmail;

import models.*;
import play.mvc.*;
import play.data.*;
import views.html.*;

public class Application extends Controller {

	// Formularios de tipos, necessarios a insercao de dados.
	final static Form<Agenda> agendaForm = form(Agenda.class);
	final static Form<Email> emailForm = form(Email.class);
	final static Form<Usuario> userForm = form(Usuario.class);

	// Prepared Statement
	private static PreparedStatement pstm;

	// Home
	public static Result index() {
		String username = session("username");
		String mensagem = "";
		if(username == null){
			mensagem = "Registre-se e comece a utilizar a Agenda.";
		}else{
			mensagem = "Usuário Logado: " + username;
		}
		return ok(index.render(userForm, mensagem));
	}

	// Registrar
	public static Result pagRegistrar() {
		return ok(registrar.render(userForm,
				"Status: Aguardando entrada de dados."));
	}

	// Regiao de abertura das demais paginas com parametro
	public static Result pagEmail() {
		return ok(email.render(emailForm,
				"Status: Aguardando entrada de dados."));
	}

	public static Result pagInserir() {
		return ok(inserir.render(agendaForm,
				"Status: Aguardando entrada de dados."));
	}

	public static Result pagEditar() {
		return ok(editar.render(agendaForm,
				"Status: Aguardando entrada de dados."));
	}

	public static Result pagDeletar() {
		return ok(deletar.render(agendaForm,
				"Digite o nome do contato a ser deletado."));
	}

	public static Result pagProcurar() {
		return ok(procurar.render(agendaForm, "Área de retorno da consulta."));
	}

	public static Result pagDownloads() {
		return ok(downloads.render("Downloads."));
	}
	
	public static Result pagVideo() {
		return ok(video.render("Play Framework"));
	}

	// Abrir Conexao
	private static Connection abrirConexao() throws SQLException {

		String url = "jdbc:mysql://localhost/contato";
		Properties props = new Properties();
		props.setProperty("user", "root");
		props.setProperty("password", "jp5811518");
		Connection conn = DriverManager.getConnection(url, props);

		return conn;

	}
	// Fechar Conexao
	private static void fecharConexao(Connection conn) throws SQLException {
		conn.close();
	}
	
	// Actions

	// Logar
	public static Result login(){
		
		try{
		// Regiao de captura dos dados da aplicacao

		// Bind
		Form<Usuario> filledForm = userForm.bindFromRequest();
		// Get Fields
		String username = filledForm.field("username").value();
		String password = filledForm.field("password").value();
		
		if(username.equals("") || password.equals("")){
			 throw new Exception("Preencha todos os campos.");
		}
		
		System.out.println("Abrindo Conexao"); 
		Connection conn = abrirConexao();
		System.out.println("Conexao aberta!"); 
		
		String consultaSQL = "Select * from usuario where username = ? and password = ?";
	    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    pstm.setString(1, username);
	    pstm.setString(2, password);
	    ResultSet rs = pstm.executeQuery();
	    
	    if (!rs.next()) {
            throw new SQLException("Login/Senha incorretos!");
        }
		
		// Salvar o username como atributo de sessao
		session("username", username);
		
		String nome = rs.getString("nome");
		
		fecharConexao(conn);
		System.out.println("Conexao fechada!"); 
			
		return ok(index.render(userForm, "Logado! Bem Vindo " + nome));
		
		}catch(Exception e){
			return ok(index.render(userForm, " - Erro: " + e.getMessage()));
		}
	}
	
	// Destruir sessão // Reinicializar atributos de sessão
	public static Result logout() {
		session().clear();
		String username = session("username");
		return ok(index.render(userForm, "Deslogado!"));
	}
	
	public static Result action_registrar() {
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Usuario> filledForm = userForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();
			String email = filledForm.field("email").value();
			String username = filledForm.field("username").value();
			String password = filledForm.field("password").value();
			
			if(nome.equals("") || email.equals("") || username.equals("") || password.equals("")){
				 throw new Exception("Preencha todos os campos.");
			}
			if(!validarEmail(email)){
				throw new Exception("Email inválido.");
			}
			
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			String consultaSQL = "Select * from usuario where username = ?";
		    
			pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setString(1, username);
		    ResultSet rs = pstm.executeQuery();
		    
		    if (rs.next()) {
	            throw new SQLException("Já existe usuário cadastrado com o Username: " + rs.getString("username"));
	        }
			
	        String atualizaSQL = "Insert INTO usuario VALUES (?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, email);
			pstm.setString(3, username);
			pstm.setString(4, password);
			pstm.executeUpdate();
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 
			
			return ok(registrar.render(userForm,
					"Status: Incluído com Sucesso!"));
		} catch (Exception e) {
			return ok(registrar.render(userForm, "Status: Erro! "
					+ e.getMessage()));
		}
	}
	
	public static Result action_insert() {
		
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(inserir.render(agendaForm, "Para utilizar a agenda é necessário estar logado."));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Agenda> filledForm = agendaForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();
			String email = filledForm.field("email").value();
			String telefone = filledForm.field("telefone").value();
			String endereco = filledForm.field("endereco").value();
			String facebook = filledForm.field("facebook").value();
			
			//Consistencias
			if(nome.equals("") || email.equals("") || telefone.equals("")){
				throw new Exception("Status: Preencha os campos com *.");
			}
			if(!validarEmail(email)){
				throw new Exception("Status: Email inválido.");
			}
			if(!telefone.matches("[0-9]*")){
				throw new Exception("Status: Telefone deve conter apenas números.");
			}
			
			// Objeto da classe Agenda
			Agenda contato = new Agenda(nome, email, username, telefone, endereco,
					facebook);

			// Play Console
			System.out.println("Regiao de Insert");
			System.out.println("Contato: " + contato.toString());
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			String consultaSQL = "Select * from agenda where nome = ? and username = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setString(1, nome);
		    pstm.setString(2, username);
		    ResultSet rs = pstm.executeQuery();
		     
	        if (rs.next()) {
	            throw new SQLException("Já existe contato cadastrado com o nome: " + rs.getString("nome") +
	            		" na agenda do usuário: " + username + ".");
	        }
			
	        String atualizaSQL = "Insert INTO agenda VALUES (?,?,?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, email);
			pstm.setString(3, username);
			pstm.setString(4, telefone);
			pstm.setString(5, endereco);
			pstm.setString(6, facebook);
			pstm.executeUpdate();
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

			return ok(inserir.render(agendaForm,
					"Status: Incluído com Sucesso!"));
		} catch (Exception e) {
			return ok(inserir.render(agendaForm,
					"Status: Erro ao Incluir! " + e.getMessage()));
		}
	}

	public static Result action_update() {
		
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render(userForm, " - Para utilizar a agenda é necessário estar logado."));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Agenda> filledForm = agendaForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();
			String email = filledForm.field("email").value();
			String telefone = filledForm.field("telefone").value();
			String endereco = filledForm.field("endereco").value();
			String facebook = filledForm.field("facebook").value();
			
			System.out.println("Abrindo Conexao");
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!");

			ResultSet rs = buscarContatoPorNome(conn, nome, username);
			rs.next();

			String atualizaSQL = "UPDATE agenda SET nome  = ? , email = ?, username = ? , telefone  =  ? , endereco = ?, facebook = ? WHERE nome = ? and username = ?";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, email);
			pstm.setString(3, username);
			pstm.setString(4, telefone);
			pstm.setString(5, endereco);
			pstm.setString(6, facebook);
			pstm.setString(7, nome);
			pstm.setString(8, username);
			pstm.executeUpdate();

			fecharConexao(conn);
			System.out.println("Conexao fechada!");

			return ok(editar.render(agendaForm,
					"Status: Contato editado com sucesso!"));

		} catch (Exception e) {
			return ok(editar.render(agendaForm,
					"Status: Erro ao Editar! " + e.getMessage()));
		}
	}

	public static Result action_delete() {
		
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		// Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render(userForm, " - Para utilizar a agenda é necessário estar logado."));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Agenda> filledForm = agendaForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();

			System.out.println("Abrindo Conexao");
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!");
			
			ResultSet rs = buscarContatoPorNome(conn, nome, username);
			rs.next();
			
			String atualizaSQL = "Delete From agenda where nome = ? and username = ?";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, username);
			pstm.executeUpdate();

			fecharConexao(conn);
			System.out.println("Conexao fechada!");

			return ok(deletar.render(agendaForm,
					"Status: Deletado com sucesso!"));
		} catch (Exception e) {
			return ok(deletar.render(agendaForm, "Status: Erro ao deletar! "
					+ e.getMessage()));
		}

	}
	
	public static Result action_search() {
		
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render(userForm, " - Para utilizar a agenda é necessário estar logado."));
		}

		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Agenda> filledForm = agendaForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();

			System.out.println("Abrindo Conexao");
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!");
			
			ResultSet rs = buscarContatoPorNome(conn, nome, username);
			rs.next();

			Agenda objAgenda = new Agenda(rs.getString("nome"),
					rs.getString("email"),rs.getString("username"), rs.getString("telefone"),
					rs.getString("endereco"), rs.getString("facebook"));

			return ok(procurar.render(agendaForm, objAgenda.toString()));

		} catch (Exception e) {
			return ok(procurar.render(agendaForm,
					"Status: Erro! " + e.getMessage()));
		}
	}
	
	public static Result action_sendEmail() {
		
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render(userForm, "Para utilizar a agenda é necessário estar logado."));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Email> filledForm = emailForm.bindFromRequest();
			// Get Fields
			String sender = filledForm.field("sender").value();
			String recipient = filledForm.field("recipient").value();
			String subject = filledForm.field("subject").value();
			String message = filledForm.field("message").value();
			
			// Consistências
			if(sender.equals("") || recipient.equals("") || subject.equals("") || message.equals("")){
				 throw new Exception("Preencha todos os campos com *.");
			}
			if(!validarEmail(sender)){
				throw new Exception("Email de origem inválido.");
			}
			if(!validarEmail(recipient)){
				throw new Exception("Email de destino inválido.");
			}
			
			// Objeto da classe email
			Email msgEmail = new Email(sender, recipient, subject, message);

			// Play Console
			System.out.println("Regiao de Insert");
			System.out.println("Email: " + msgEmail.toString());

			System.out.println("Abrindo Conexao");
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!");

			String atualizaSQL = "Insert INTO email VALUES (?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, sender);
			pstm.setString(2, recipient);
			pstm.setString(3, subject);
			pstm.setString(4, message);
			pstm.executeUpdate();

			fecharConexao(conn);
			System.out.println("Conexao fechada!");
			
			System.out.println("From: " + sender + " To: " + recipient + " Subject: " + subject + " Message" + message);
			
			// Enviar Email
			sendEmail(sender, recipient, subject, message);

			return ok(email.render(emailForm,
					"Status: Salvo e enviado com sucesso!"));
		} catch (Exception e) {
			return ok(email
					.render(emailForm, "Status: Erro! " + e.getMessage()));
		}
	}

	private static void sendEmail(String sender, String recipient,
			String subject, String message) {
		
		try {
			SimpleEmail email = new SimpleEmail();
			email.setHostName("smtp.gmail.com"); // o servidor SMTP para envio do e-mail
			email.addTo(recipient); // destinatário
			email.setFrom(sender); // remetente
			email.setSubject(subject); // assunto do e-mail
			email.setMsg(message); // conteudo do e-mail
			email.setAuthentication("webagendaplayframework@gmail.com", "webagenda");
			email.setSmtpPort(465);
			email.setSSL(true);
			email.setTLS(true);
			email.send();
		} catch (Exception e) {
			e.getMessage();
			System.out.println("Exception: " + e);
		}
		
	}
	
	private static boolean validarEmail(String email) {
		
		if(email.length()<= 2){
			return false;
		}
		if (email.charAt(0) == '@' || email.charAt(email.length() - 1) == '@'
				|| email.charAt(0) == '.'
				|| email.charAt(email.length() - 1) == '.')
			return false;
		int cont = 0, cont2 = 0;
		for (int i = 0; i < email.length(); i++) {
			if (email.charAt(i) == '@')
				cont++;
			if (email.charAt(i) == ' ')
				cont2++;
		}
		return (cont == 1 && cont2 == 0 && email.indexOf("..") == -1
				&& email.charAt(email.length() - 4) == '.' || email
					.charAt(email.length() - 3) == '.');
	}

	private static ResultSet buscarContatoPorNome(Connection conn, String nome, String username)
			throws SQLException {
		String consultaSQL = "Select * from agenda where nome = ? and username = ?";
		pstm = conn.prepareStatement(consultaSQL,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		pstm.setString(1, nome);
		pstm.setString(2, username);
		ResultSet rs = pstm.executeQuery();
		if (!rs.next()) {
			throw new SQLException("Não existe contato cadastrado com nome "
					+ nome + " na agenda do usuário " + username + ".");
		}
		rs.beforeFirst();
		return rs;
	}
}