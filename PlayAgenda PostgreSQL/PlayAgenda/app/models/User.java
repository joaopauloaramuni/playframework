package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class User extends Model{
	
	public String nome;
	public String email;
	public String username;
	public String password;
	
	public User(String nome, String email, String username, String password)  {
		this.nome = nome;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [nome=" + nome + ", email=" + email + ", username="
				+ username + ", password=" + password + "]";
	}

}

