package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class User extends Model{
	
	@Id
	public int id_usuario;
	
	@ManyToOne
	@JoinColumn(name="id_func")
	public Func funcionario;
	
	public String username;
	public String password;
	
	public User(int id_usuario, String username, String password)  {
		this.id_usuario = id_usuario;
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [id_usuario=" + id_usuario + ", username=" + username + ", password="
				+ password + "]";
	}

}

