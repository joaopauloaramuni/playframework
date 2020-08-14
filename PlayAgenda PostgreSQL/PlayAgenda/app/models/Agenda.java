package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Agenda extends Model{
	
	public String nome;
	public String email;
	public String username;
	public String telefone;
	public String endereco;
	public String facebook;
	
	public Agenda(String nome, String email, String username, String telefone, String endereco, String facebook)  {
		this.nome = nome;
		this.email = email;
		this.username = username;
		this.telefone = telefone;
		this.endereco = endereco;
		this.facebook = facebook;
	}

	@Override
	public String toString() {
		return "Agenda \n Nome = " + nome + ",\n Email = " + email + ",\n Username = "
				+ username + ",\n Telefone = " + telefone + ",\n Endereco = "
				+ endereco + ",\n Facebook = " + facebook;
	}

}

