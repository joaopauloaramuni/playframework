package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Func extends Model{
	
	@Id
	public int id_func;
	
	public String nome;
	public String cpf;
	public String endereco;
	public String email;
	public String data;
	public String telefone;
	public String celular;
	public String salario;
	public String data_ad;
	
	public Func(int id_func, String nome, String cpf, String endereco, String email, String data, String telefone, String celular, String salario, String data_ad)  {
		this.id_func = id_func;
		this.nome = nome;
		this.cpf = cpf;
		this.endereco = endereco;
		this.email = email;
		this.data = data;
		this.telefone = telefone;
		this.celular = celular;
		this.salario = salario;
		this.data_ad = data_ad;
	}

	@Override
	public String toString() {
		return "Func [nome=" + nome + ", cpf=" + cpf + ", endereco="
				+ endereco + ", email=" + email + ", data=" + data + ", telefone=" + telefone + ", celular=" + celular + ", salario=" + salario + ", data_ad=" + data_ad + "]";
	}

}

