package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Peca extends Model{
	
	@Id
	public int id_peca;
	
	public String nome;
	public String preco;
	
	public Peca(int id_peca, String nome, String preco)  {
		this.id_peca = id_peca;
		this.nome = nome;
		this.preco = preco;
	}

	@Override
	public String toString() {
		return "Peca [nome=" + nome + ", preco=" + preco + "]";
	}

}

