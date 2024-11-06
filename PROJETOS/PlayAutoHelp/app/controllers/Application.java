package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import models.*;
import play.mvc.*;
import play.data.*;
import views.html.*;
import org.apache.commons.mail.SimpleEmail;

public class Application extends Controller {
  
	// Formularios de tipos, necessarios a insercao de dados.
  	final static Form<User> userForm = form(User.class);
	final static Form<Email> emailForm = form(Email.class);
	final static Form<Cliente> clienteForm = form(Cliente.class);
	final static Form<Func> funcForm = form(Func.class);
	final static Form<Peca> pecaForm = form(Peca.class);
	final static Form<Servico> servForm = form(Servico.class);
	final static Form<Veiculo> veicForm = form(Veiculo.class);
	final static Form<Atend> atendForm = form(Atend.class);
	
    // Prepared Statement
	private static PreparedStatement pstm;
	
	// Abrir Conexao
	private static Connection abrirConexao() throws SQLException {

		String url = "jdbc:postgresql://ec2-54-243-47-196.compute-1.amazonaws.com:5432/d58do36gji2b9s?sslfactory=org.postgresql.ssl.NonValidatingFactory";
		Properties props = new Properties();
		props.setProperty("user", "exgabuxttauoed");
		props.setProperty("password", "foOKbAOIarBZD2WDS2YBIOn--_");
		props.setProperty("ssl", "true");
		Connection conn = DriverManager.getConnection(url, props);

		return conn;

	}

	// Fechar Conexao
	private static void fecharConexao(Connection conn) throws SQLException {
		conn.close();
	}
	
	// Pages
	
	public static Result index() {
		return ok(index.render(""));
	}

	public static Result pagServ() {
		return ok(servico.render(servForm, ""));
	}
	
	public static Result pagPec() {
		return ok(pec.render(""));
	}
	
	public static Result pagOrc() {
		return ok(orc.render(""));
	}
	
	public static Result pagQuem() {
		return ok(quem.render(""));
	}
	
	public static Result pagContato() {
		return ok(contato.render(""));
	}
	
	public static Result pagEmail() {
		return ok(email.render(emailForm, ""));
	}
	
	public static Result pagMec() {
		return ok(mec.render(""));
	}
	
	public static Result pagManu() {
		return ok(manu.render(""));
	}
	
	public static Result pagPneu() {
		return ok(pneu.render(""));
	}
	
	public static Result pagPrev() {
		return ok(prev.render(""));
	}
	
	public static Result pagAdmin() {
		
		//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(admin.render(userForm, ""));
		}else{
			return ok(painel.render("Usuario Logado: " + username));
		}
	}
	
	public static Result pagPainel() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(painel.render("Usuario Logado: " + username));
		}
	}
		
	public static Result pagAtend() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(atend.render(atendForm, ""));
		}
	}
	
	public static Result pagFunc() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(func.render(funcForm, ""));
		}
	}
	
		
	public static Result pagCliente() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(cliente.render(clienteForm, ""));
		}
	}
	
		
	public static Result pagVeiculo() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(veiculo.render(veicForm, ""));
		}
	}
	
	public static Result pagServico() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(servico.render(servForm, ""));
		}
	}
	
	public static Result pagPeca() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(peca.render(pecaForm, ""));
		}
	}
	
	public static Result pagRel() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(relatorios.render("Usuario Logado: " + username));
		}
	}
	
	public static Result pagUser() {
	//Verificar Sessao
		String username = session("username");
		if(username == null){
			return ok(index.render("'Acesso Restrito!'"));
		}else{
			return ok(user.render(userForm, ""));
		}
	}
	
	// Action

	public static Result action_admin() {
		
		try{
		// Regiao de captura dos dados da aplicacao

		// Bind
		Form<User> filledForm = userForm.bindFromRequest();
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
		
		// Recupera id_func
		int id_func = rs.getInt("id_func");
		
		String nome = recuperaNomeFunc(conn, id_func);
		
		fecharConexao(conn);
		System.out.println("Conexao fechada!"); 
	    System.out.println("Logado! Bem Vindo " + nome); 

		return ok(painel.render("Logado! Bem Vindo " + nome));
		
		}catch(Exception e){
			System.out.println("'Erro: " + e.getMessage() + "'"); 
			return ok(index.render("'Erro: " + e.getMessage() + "'"));
		}
	}
	
	// Destruir sessao // Reinicializar atributos de sessao
	public static Result logout() {
		session().clear();
		System.out.println("Deslogado!"); 
		return ok(index.render("'Deslogado!'"));
	}
	
	public static Result action_user() {
		// Recuperar o username do atributo de sessao
		String usuario = session("username");
		
		//Verificar se o usuario esta logado
		if(usuario == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<User> filledForm = userForm.bindFromRequest();
			// Get Fields
			int id_usuario = 0;
			int id_func = 0;
			
			String nomeFuncionario = filledForm.field("nomeFuncionario").value();
			String username = filledForm.field("username").value();
			String password = filledForm.field("password").value();
			
			//Consistencias
			if(username.equals("") || password.equals("")){
				throw new Exception("Preencha os campos com *.");
			}
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			//Recupera id_func
			id_func = recuperaFunc(conn, nomeFuncionario);
			
			if(id_func == 0){
				throw new SQLException("Nao existe funcionario cadastrado com o nome: " + nomeFuncionario + ".");
			}
			
			System.out.println("id_func: " + id_func);
			
			String consultaSQL = "Select * from usuario where id_func = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setInt(1, id_func);
		    ResultSet rs = pstm.executeQuery();
		     
	        if (rs.next()) {
	            throw new SQLException("Ja existe usuario cadastrado para o funcionario: " + nomeFuncionario + ".");
	        }
			
	        String atualizaSQL = "Insert INTO usuario(id_func,username,password) VALUES (?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setInt(1, id_func);
			pstm.setString(2, username);
			pstm.setString(3, password);
			pstm.executeUpdate();
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

		return ok(user.render(userForm, "'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(user.render(userForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}		
	}
	
	public static Result action_cliente() {
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Cliente> filledForm = clienteForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();
			String cpf = filledForm.field("cpf").value();
			String endereco = filledForm.field("endereco").value();
			String email = filledForm.field("email").value();
			String data = filledForm.field("data").value();
			String telefone = filledForm.field("telefone").value();
			String celular = filledForm.field("celular").value();
			
			//Consistencias
			if(nome.equals("") || cpf.equals("") || endereco.equals("") || email.equals("") || data.equals("") || telefone.equals("") || celular.equals("")){
				throw new Exception("Preencha os campos com *.");
			}
			if(!cpf.matches("[0-9]*")){
				throw new Exception("CPF deve conter apenas numeros.");
			}
			if(!validarCPF(cpf)){
				throw new Exception("CPF Invalido!");
			}
			if(!validarEmail(email)){
				throw new Exception("Email invalido.");
			}
			if(!telefone.matches("[0-9]*")){
				throw new Exception("Telefone deve conter apenas numeros.");
			}
			if(!celular.matches("[0-9]*")){
				throw new Exception("Celular deve conter apenas numeros.");
			}
		
			// Play Console
			System.out.println("Regiao de Insert");
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			String consultaSQL = "Select * from cliente where cpf = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setString(1, cpf);
		    ResultSet rs = pstm.executeQuery();
		     
	        if (rs.next()) {
	            throw new SQLException("Ja existe cliente cadastrado com o cpf: " + rs.getString("cpf") + ".");
	        }
			
	        String atualizaSQL = "Insert INTO cliente (nome, cpf, endereco, email, data, telefone, celular) VALUES (?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, cpf);
			pstm.setString(3, endereco);
			pstm.setString(4, email);
			pstm.setString(5, data);
			pstm.setString(6, telefone);
			pstm.setString(7, celular);
			pstm.executeUpdate();
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

			return ok(cliente.render(clienteForm,
					"'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(cliente.render(clienteForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}		
	}
	
	public static Result action_func() {
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Func> filledForm = funcForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();
			String cpf = filledForm.field("cpf").value();
			String endereco = filledForm.field("endereco").value();
			String email = filledForm.field("email").value();
			String data = filledForm.field("data").value();
			String telefone = filledForm.field("telefone").value();
			String celular = filledForm.field("celular").value();
			String salario = filledForm.field("salario").value();
			String data_ad = filledForm.field("data_ad").value();
			
			//Consistencias
			if(nome.equals("") || cpf.equals("") || endereco.equals("") || email.equals("") || data.equals("") || telefone.equals("") || celular.equals("") || data_ad.equals("")){
				throw new Exception("Preencha os campos com *.");
			}
			if(!cpf.matches("[0-9]*")){
				throw new Exception("CPF deve conter apenas numeros.");
			}
			if(!validarCPF(cpf)){
				throw new Exception("CPF Invalido!");
			}
			if(!validarEmail(email)){
				throw new Exception("Email invalido.");
			}
			if(!telefone.matches("[0-9]*")){
				throw new Exception("Telefone deve conter apenas numeros.");
			}
			if(!celular.matches("[0-9]*")){
				throw new Exception("Celular deve conter apenas numeros.");
			}
			if(salario != ""){
				if(!salario.matches("[0-9]*")){
					throw new Exception("Salario deve conter apenas numeros.");
				}
			}
			
			// Play Console
			System.out.println("Regiao de Insert");
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			String consultaSQL = "Select * from funcionario where nome = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setString(1, nome);
		    ResultSet rs = pstm.executeQuery();
		     
	        if (rs.next()) {
	            throw new SQLException("Ja existe funcionario cadastrado com o nome: " + rs.getString("nome") + ".");
	        }
			
	        String atualizaSQL = "Insert INTO funcionario (nome, cpf, endereco, email, data, telefone, celular, salario, data_ad) VALUES (?,?,?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, cpf);
			pstm.setString(3, endereco);
			pstm.setString(4, email);
			pstm.setString(5, data);
			pstm.setString(6, telefone);
			pstm.setString(7, celular);
			pstm.setString(8, salario);
			pstm.setString(9, data_ad);
			pstm.executeUpdate();
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

			return ok(func.render(funcForm,
					"'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(func.render(funcForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}	
	}
	
	public static Result action_peca() {
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Peca> filledForm = pecaForm.bindFromRequest();
			// Get Fields
			String nome = filledForm.field("nome").value();
			String preco = filledForm.field("preco").value();
			
			//Consistencias
			if(nome.equals("") || preco.equals("")){
				throw new Exception("Preencha os campos com *.");
			}
			if(!preco.matches("[0-9]*")){
				throw new Exception("Preco deve conter apenas numeros.");
			}
			
			// Play Console
			System.out.println("Regiao de Insert");
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			String consultaSQL = "Select * from peca where nome = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setString(1, nome);
		    ResultSet rs = pstm.executeQuery();
		     
	        if (rs.next()) {
	            throw new SQLException("Ja existe peca cadastrada com o nome: " + rs.getString("nome") + ".");
	        }
			
	        String atualizaSQL = "Insert INTO peca (nome, preco) VALUES (?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setString(1, nome);
			pstm.setString(2, preco);
			pstm.executeUpdate();
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

			return ok(peca.render(pecaForm,
					"'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(peca.render(pecaForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}		
	}
	
	public static Result action_serv() {
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Servico> filledForm = servForm.bindFromRequest();
			// Get Fields
			int id_serv = 0;
			int id_peca = 0;
			
			//Peca Opcional
			String peca = filledForm.field("peca").value();
			String qtd_peca = filledForm.field("qtd_peca").value();
		
			String nome = filledForm.field("nome").value();
			String valor = filledForm.field("valor").value();
		
			//Consistencias
			if(nome.equals("") || valor.equals("")){
				throw new Exception("Preencha os campos com *.");
			}
			if(!valor.matches("[0-9]*")){
				throw new Exception("Valor deve conter apenas numeros.");
			}

			// Play Console
			System.out.println("Regiao de Insert");
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			//Recupera id_peca
			if (peca != ""){
				String consultaSQL = "Select * from peca where nome = ?";
				pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pstm.setString(1, peca);
				ResultSet rs = pstm.executeQuery();
				if (rs.next()) {
					id_peca = rs.getInt("id_peca");
					System.out.println("id_peca: " + id_peca); 
				}else {
					throw new SQLException("Nao existe peca cadastrada com o nome: " + rs.getString("nome") + ".");
				}
			}
			//Recupera id_serv
			String consultaSQL = "Select id_serv from servico where nome = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setString(1, nome);
			ResultSet rs = pstm.executeQuery();
		    
			//Verificar se o servico existe
	        if (rs.next()) {
				id_serv = rs.getInt("id_serv");
				System.out.println("Existe: id_serv: " + id_serv); 
				
				consultaSQL = "Select * from servico where id_serv = ? and id_peca = ?";
				pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pstm.setInt(1, id_serv);
				pstm.setInt(2, id_peca);
				rs = pstm.executeQuery();
				//Verificar se ja existe a peca cadastrada neste servico
				if (rs.next()) {
					throw new SQLException("Ja existe esta peca cadastrada no servico: " + rs.getString("nome") + ".");
				}
		   }else {
				consultaSQL = "select id_serv from servico where id_serv = (select max(id_serv) from servico where 1 = ?)";
				pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pstm.setInt(1, 1);
				rs = pstm.executeQuery();
				if (rs.next()) {
					id_serv = rs.getInt("id_serv") + 1;
					System.out.println("Nao existe: id_serv: " + id_serv); 
				}else {
					id_serv = 1;
					System.out.println("Primeiro servico cadastrado: " + id_serv);
				}
		   }
			
	        String atualizaSQL = "Insert INTO servico VALUES (?,?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setInt(1, id_serv);
			pstm.setInt(2, id_peca);
			pstm.setString(3, qtd_peca);
			pstm.setString(4, nome);
			pstm.setString(5, valor);
			
			pstm.executeUpdate();
			System.out.println("Insert Realizado!"); 
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

			return ok(servico.render(servForm,
					"'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(servico.render(servForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}		
	}
	
	public static Result action_atend() {
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Atend> filledForm = atendForm.bindFromRequest();
			// Get Fields
			int id_atend = 0;
			int id_serv = 0;
			int id_func = 0;
			int id_veic = 0;
			
			String nomeServico = filledForm.field("nomeServico").value();
			String nomeFuncionario = filledForm.field("nomeFuncionario").value();
			String placa = filledForm.field("placa").value();
			String vlr_total = ""; //Calcular
			String desconto = filledForm.field("desconto").value();
			String data_e = filledForm.field("data_e").value();
			String data_s = filledForm.field("data_s").value();
			String finalizado = filledForm.field("finalizado").value();
			
			//Consistencias
			if(nomeServico.equals("") || nomeFuncionario.equals("") || placa.equals("") || data_e.equals("") || data_s.equals("")){
				throw new Exception("Preencha os campos com *.");
			}
			if(desconto != ""){
				if(!desconto.matches("[0-9]*")){
					throw new Exception("Desconto deve conter apenas numeros.");
				}
			}
			// Play Console
			System.out.println("Regiao de Insert");
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			//Recupera id_func
			id_func = recuperaFunc(conn, nomeFuncionario);
			
			if(id_func == 0){
				throw new SQLException("Nao existe funcionario cadastrado com o nome: " + nomeFuncionario + ".");
			}
			
			System.out.println("id_func: " + id_func);
			
			//Recupera id_veic
			id_veic = recuperaVeiculo(conn, placa);
			
			if(id_veic == 0){
				throw new SQLException("Nao existe veiculo cadastrado com a placa: " + placa + ".");
			}
		
			System.out.println("id_veic: " + id_veic);
			
			//Recupera id_atend
			String consultaSQL = "Select id_atend from atend where id_veic = ? and finalizado = ?";
		    pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    pstm.setInt(1, id_veic);
			pstm.setString(2, "N");
		    ResultSet rs = pstm.executeQuery();
			
			if (rs.next()) {
				id_atend = rs.getInt("id_atend");
				System.out.println("Atend: " + id_atend);
			}else {
				consultaSQL = "select id_atend from atend where id_atend = (select max(id_atend) from atend where 1 = ?)";
				pstm = conn.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pstm.setInt(1, 1);
				rs = pstm.executeQuery();
				if (rs.next()) {
					id_atend = rs.getInt("id_atend") + 1;
					System.out.println("Nao existe: id_atend: " + id_atend); 
				}else {
					id_atend = 1;
					System.out.println("Primeiro atendimento cadastrado: " + id_atend); 
				}
			}
				
			System.out.println("id_atend: " + id_atend);
			
			//Recupera id_serv
			id_serv = recuperaServico(conn, nomeServico);
			   
			if(id_serv == 0){
				throw new SQLException("O servico " + nomeServico + "nao esta cadastrado.");
			}
			
			System.out.println("id_serv: " + id_serv);
			
			//Verificar se ja existe o servico cadastrado neste atendimento
			String consultaSQL2 = "Select * from atend where id_atend = ? and id_serv = ?";
			pstm = conn.prepareStatement(consultaSQL2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstm.setInt(1, id_atend);
			pstm.setInt(2, id_serv);
			ResultSet rs2 = pstm.executeQuery();
			if (rs2.next()) {
				throw new SQLException("Este servico ja esta cadastrado neste atendimento.");
			}
			
			System.out.println("Este servico ainda nao faz parte do atendimento: " + id_serv);
			
			// Calcular vlr_total do servico com base na qtd_peca
			
			String atualizaSQL = "Insert INTO atend VALUES (?,?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setInt(1, id_atend);
			pstm.setInt(2, id_serv);
			pstm.setInt(3, id_func);
			pstm.setInt(4, id_veic);
			pstm.setString(5, vlr_total);
			pstm.setString(6, desconto);
			pstm.setString(7, data_e);
			pstm.setString(8, data_s);
			pstm.setString(9, finalizado);
			
			pstm.executeUpdate();
			System.out.println("Insert Realizado!"); 
			fecharConexao(conn);
			System.out.println("Conexao fechada!"); 

			return ok(atend.render(atendForm,
					"'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(atend.render(atendForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}		
	}
	
	public static String recuperaNomeFunc(Connection conn, int id_func) {  
	   
	   String nome = "";
	   
	   try{
		   PreparedStatement pstm = conn.prepareStatement("Select nome from funcionario where id_func = ?");  
		   pstm.setInt(1, id_func);  
		   ResultSet rs = pstm.executeQuery();  
		   if(rs.next()){  
				nome = rs.getString("nome");
		   }
		   rs.close();  
		   pstm.close();  
	   
	   }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	   }
      
   	   return nome;  
	}
	
	public static int recuperaFunc(Connection conn, String nome) {  
	   
	   int id_func = 0;
	   
	   try{
		   PreparedStatement pstm = conn.prepareStatement("Select * from funcionario where nome = ?");  
		   pstm.setString(1, nome);  
		   ResultSet rs = pstm.executeQuery();  
		   if(rs.next()){  
				id_func = rs.getInt("id_func");
		   }
		   rs.close();  
		   pstm.close();  
	   
	   }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	   }
      
   	   return id_func;  
	}
	
	public static int recuperaServico(Connection conn, String nome) {  
	 
	   int id_serv = 0;
	   
	   try{
		   PreparedStatement pstm = conn.prepareStatement("Select * from servico where nome = ?");  
		   pstm.setString(1, nome);  
		   ResultSet rs = pstm.executeQuery();  
		   if(rs.next()){  
				id_serv = rs.getInt("id_serv");
		   }
		   rs.close();  
		   pstm.close();  
	   
	   }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	   }
      
   	   return id_serv;  
	}
	
	public static int recuperaVeiculo(Connection conn, String placa) {  
	   
	   int id_veic = 0;
	   
	   try{
		   PreparedStatement pstm = conn.prepareStatement("Select * from veiculo where placa = ?");  
		   pstm.setString(1, placa);  
		   ResultSet rs = pstm.executeQuery();  
		   if(rs.next()){  
				id_veic = rs.getInt("id_veic");
		   }
		   rs.close();  
		   pstm.close();  
	   
	   }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	   }
      
   	   return id_veic;  
	}	
	
	public static int recuperaCliente(Connection conn, String nome) {  
	   
	   int id_cliente = 0;
	   
	   try{
		   PreparedStatement pstm = conn.prepareStatement("Select * from cliente where nome = ?");  
		   pstm.setString(1, nome);  
		   ResultSet rs = pstm.executeQuery();  
		   if(rs.next()){  
				id_cliente = rs.getInt("id_cliente");
		   }
		   rs.close();  
		   pstm.close();  
	   
	   }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	   }
      
   	   return id_cliente;  
	}	
	
	public static Boolean verificaVeiculo(Connection conn, String placa) {  
      
	   Boolean existe = false;
	   
	   try{
		   PreparedStatement pstm = conn.prepareStatement("Select * from veiculo where placa = ?");  
		   pstm.setString(1, placa);  
		   ResultSet rs = pstm.executeQuery();  
		   if(rs.next()){  
				existe = true; 
		   }
		   rs.close();  
		   pstm.close();  
	   
	   }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	   }
      
   	   return existe;  
	}  
	
	public static Result action_veic() {
		// Recuperar o username do atributo de sessao
		String username = session("username");
		
		//Verificar se o usuario esta logado
		if(username == null){
			return ok(index.render("'Para cadastrar e necessario estar logado.'"));
		}
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Veiculo> filledForm = veicForm.bindFromRequest();
			// Get Fields
			int id_veic = 0;
			int id_cliente = 0;
			String nomeCliente = filledForm.field("nomeCliente").value();
			String marca = filledForm.field("marca").value();
			String modelo = filledForm.field("modelo").value();
			String placa = filledForm.field("placa").value();
			String ano_f = filledForm.field("ano_f").value();
			String ano_m = filledForm.field("ano_m").value();
			String km = filledForm.field("km").value();
			String oleo = filledForm.field("oleo").value();
			//byte[] foto = filledForm.field("foto").value();
			String foto = filledForm.field("foto").value();
			
			//Consistencias
			if(cliente.equals("") || marca.equals("") || modelo.equals("") || placa.equals("") || ano_f.equals("") || ano_m.equals("") || km.equals("")){
				throw new Exception("Preencha os campos com *.");
			}

			// Play Console
			System.out.println("Regiao de Insert");
		
			System.out.println("Abrindo Conexao"); 
			Connection conn = abrirConexao();
			System.out.println("Conexao aberta!"); 
			
			System.out.println("Verificar se o veiculo existe"); 
			 
			if(verificaVeiculo(conn, placa)){ 
				throw new Exception("Ja existe veiculo cadastrado com a placa: " + placa + ".");
			}
			
			id_cliente = recuperaCliente(conn, nomeCliente);
			
			if(id_cliente == 0){
				throw new SQLException("Nao existe cliente cadastrado com o nome: " + nomeCliente + ".");
			}
	
	        String atualizaSQL = "Insert INTO veiculo (id_cliente, marca, modelo, placa, ano_f, ano_m, km, oleo, foto) VALUES (?,?,?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(atualizaSQL);
			pstm.setInt(1, id_cliente);
			pstm.setString(2, marca);
			pstm.setString(3, modelo);
			pstm.setString(4, placa);
			pstm.setString(5, ano_f);
			pstm.setString(6, ano_m);
			pstm.setString(7, km);
			pstm.setString(8, oleo);
			pstm.setString(9, foto);
			
			pstm.executeUpdate();
			
			System.out.println("Insert Realizado!"); 
			fecharConexao(conn);	
			System.out.println("Conexao fechada!"); 
	
			return ok(veiculo.render(veicForm,
					"'Incluido com Sucesso!'"));
		} catch (Exception e) {
			return ok(veiculo.render(veicForm,
					"'Erro ao Incluir! " + e.getMessage() + "'"));
		}		
	}
	
	public static Result action_consultar() {
		//Gerar relatorio
		return ok(index.render(""));
	}
	
	public static Result action_sendEmail() {
		
		try {
			// Regiao de captura dos dados da aplicacao

			// Bind
			Form<Email> filledForm = emailForm.bindFromRequest();
			// Get Fields
			String sender = filledForm.field("sender").value();
			//String recipient = filledForm.field("recipient").value();
			String recipient = "joaopauloaramuni@gmail.com";
			String subject = filledForm.field("subject").value();
			String message = filledForm.field("message").value();
			
			// Consistencias
			if(sender.equals("") || recipient.equals("") || subject.equals("") || message.equals("")){
				 throw new Exception("Preencha todos os campos com *.");
			}
			if(!validarEmail(sender)){
				throw new Exception("Email de origem invalido.");
			}
			if(!validarEmail(recipient)){
				throw new Exception("Email de destino invalido.");
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
			
			System.out.println("Fim do Insert!");
			
			fecharConexao(conn);
			System.out.println("Conexao fechada!");
			
			System.out.println("From: " + sender + " To: " + recipient + " Subject: " + subject + " Message" + message);
			
			// Enviar Email
			SimpleEmail email = new SimpleEmail();
			email.setHostName("smtp.gmail.com"); // o servidor SMTP para envio do e-mail
			email.addTo(recipient); // destinatario
			email.setFrom(sender); // remetente
			email.setSubject(subject); // assunto do e-mail
			email.setMsg(message); // conteudo do e-mail
			email.setAuthentication("joaopauloaramuni@gmail.com", "4llmnon500");
			email.setSmtpPort(465);
			email.setSSL(true);
			email.setTLS(true);
			email.send();

			return ok(index.render("'Salvo e enviado com sucesso!'"));
		} catch (Exception e) {
			return ok(index
					.render("'Erro: " + e.getMessage() + "'"));
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
	
	public static boolean validarCPF(String cpf) {

		if (cpf.length() != 11 || !cpf.matches("[0-9]*"))
			return false;

		String fator = "100908070605040302";
		String fator2 = "111009080706050403";

		int tot1 = 0;
		int tot2 = 0;
		int j = 0;

		for (int i = 0; i <= 8; i++) {
			tot1 += Integer.parseInt(cpf.substring(i, i + 1))
					* Integer.parseInt(fator.substring(j, j + 2));
			tot2 += Integer.parseInt(cpf.substring(i, i + 1))
					* Integer.parseInt(fator2.substring(j, j + 2));
			j += 2;
		}
		int dv1 = 11 - (tot1 % 11);
		if (dv1 > 9)
			dv1 = 0;
		tot2 += dv1 * 2;
		int dv2 = 11 - (tot2 % 11);
		if (dv2 > 9)
			dv2 = 0;

		return cpf.substring(9, 11).equals("" + dv1 + dv2);

	}
	// Combos
	
	public static List<String> comboCliente(){
	
		List<String> nomeCli = new ArrayList();
	   
	    try{
		   System.out.println("Abrindo Conexao"); 
		   Connection conn = abrirConexao();
		   System.out.println("Conexao aberta!"); 
		   PreparedStatement pstm = conn.prepareStatement("Select nome from cliente where 1 = ?");  
		   pstm.setInt(1, 1);  
		   ResultSet rs = pstm.executeQuery();  
		   while(rs.next()){  
				nomeCli.add(rs.getString("nome"));
		   }
		   rs.close();  
		   pstm.close();  
		   fecharConexao(conn);
		   System.out.println("Conexao fechada!"); 
		   
	    }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	    }
	
		return nomeCli;
		
	}
	
	public static List<String> comboPeca(){
	
		List<String> nomePeca = new ArrayList();
	   
	    try{
		   System.out.println("Abrindo Conexao"); 
		   Connection conn = abrirConexao();
		   System.out.println("Conexao aberta!"); 
		   PreparedStatement pstm = conn.prepareStatement("Select nome from peca where 1 = ?");  
		   pstm.setInt(1, 1);  
		   ResultSet rs = pstm.executeQuery();  
		   while(rs.next()){  
				nomePeca.add(rs.getString("nome"));
		   }
		   rs.close();  
		   pstm.close();  
		   fecharConexao(conn);
		   System.out.println("Conexao fechada!"); 
		   
	    }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	    }
	
		return nomePeca;
		
	}
	
	public static List<String> comboQtdPeca(){
	
		List<String> qtdPeca = new ArrayList();
	   
		int cont = 1;
		
		while(cont <= 20){
		
			qtdPeca.add(Integer.toString(cont));
			
			cont++;
		}
		
		return qtdPeca;
		
	}
	
	public static List<String> comboFinalizado(){
	
		List<String> finalizado = new ArrayList();
	   
		finalizado.add("N");
		finalizado.add("S");
				
		return finalizado;
		
	}
	
	public static List<String> comboServico(){
	
		List<String> nomeServ = new ArrayList();
	   
	    try{
		   System.out.println("Abrindo Conexao"); 
		   Connection conn = abrirConexao();
		   System.out.println("Conexao aberta!"); 
		   PreparedStatement pstm = conn.prepareStatement("Select nome from servico where 1 = ?");  
		   pstm.setInt(1, 1);  
		   ResultSet rs = pstm.executeQuery();  
		   while(rs.next()){  
				nomeServ.add(rs.getString("nome"));
		   }
		   rs.close();  
		   pstm.close();  
		   fecharConexao(conn);
		   System.out.println("Conexao fechada!"); 
		   
	    }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	    }
	
		return nomeServ;
		
	}
	
	public static List<String> comboFunc(){
	
		List<String> nomeFunc = new ArrayList();
	   
	    try{
		   System.out.println("Abrindo Conexao"); 
		   Connection conn = abrirConexao();
		   System.out.println("Conexao aberta!"); 
		   PreparedStatement pstm = conn.prepareStatement("Select nome from funcionario where 1 = ?");  
		   pstm.setInt(1, 1);  
		   ResultSet rs = pstm.executeQuery();  
		   while(rs.next()){  
				nomeFunc.add(rs.getString("nome"));
		   }
		   rs.close();  
		   pstm.close();  
		   fecharConexao(conn);
		   System.out.println("Conexao fechada!"); 
		   
	    }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	    }
	
		return nomeFunc;
		
	}
	
	public static List<String> comboVeic(){
	
		List<String> nomeVeic = new ArrayList();
	   
	    try{
		   System.out.println("Abrindo Conexao"); 
		   Connection conn = abrirConexao();
		   System.out.println("Conexao aberta!"); 
		   PreparedStatement pstm = conn.prepareStatement("Select placa from veiculo where 1 = ?");  
		   pstm.setInt(1, 1);  
		   ResultSet rs = pstm.executeQuery();  
		   while(rs.next()){  
				nomeVeic.add(rs.getString("placa"));
		   }
		   rs.close();  
		   pstm.close();  
		   fecharConexao(conn);
		   System.out.println("Conexao fechada!"); 
		   
	    }catch(Exception e){
			e.getMessage();
			System.out.println("'Erro: " + e.getMessage() + "'"); 
	    }
	
		return nomeVeic;
		
	}
}