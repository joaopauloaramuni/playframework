package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Servico extends Model{
	
	@Id
	public int id_serv;
	@Id
	public int id_peca;
	
	public String qtd_peca;
	public String nome;
	public String valor;
	
	public Servico(int id_serv, int id_peca, String qtd_peca, String nome, String valor)  {
		this.id_serv = id_serv;
		this.id_peca = id_peca;
		this.qtd_peca = qtd_peca;
		this.nome = nome;
		this.valor = valor;
	}

	@Override
	public String toString() {
		return "Servico [id_serv =" + id_serv + ", id_peca=" + id_peca + ", qtd_peca=" + qtd_peca + ", nome=" + nome + ", valor=" + valor + "]";
	}

}

