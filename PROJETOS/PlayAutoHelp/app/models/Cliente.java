package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Cliente extends Model{
	
	@Id
	public int id_cliente;
	
	public String nome;
	public String cpf;
	public String endereco;
	public String email;
	public String data;
	public String telefone;
	public String celular;
	
	public Cliente(int id_cliente, String nome, String cpf, String endereco, String email, String data, String telefone, String celular)  {
		this.id_cliente = id_cliente;
		this.nome = nome;
		this.cpf = cpf;
		this.endereco = endereco;
		this.email = email;
		this.data = data;
		this.telefone = telefone;
		this.celular = celular;
	}

	@Override
	public String toString() {
		return "Cliente [nome=" + nome + ", cpf=" + cpf + ", endereco="
				+ endereco + ", email=" + email + ", data=" + data + ", telefone=" + telefone + ", celular=" + celular + "]";
	}

}

