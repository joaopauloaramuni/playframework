package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Atend extends Model{
	
	@Id
	public int id_atend;
	
	@Id
	public int id_serv;
	
	@ManyToOne
	@JoinColumn(name="id_func")
	public Func funcionario;
	
	@ManyToOne
	@JoinColumn(name="id_veic")
	public Veiculo veiculo;	
	
	public String vlr_total;
	public String desconto;
	public String data_e;
	public String data_s;
	
	public Atend(int id_atend, int id_serv, String vlr_total, String desconto, String data_e, String data_s)  {
		this.id_atend = id_atend;
		this.id_serv = id_serv;
		this.desconto = desconto;
		this.data_e = data_e;
		this.data_s = data_s;
	}

	@Override
	public String toString() {
		return "Atend [id_atend =" + id_atend + ", id_serv=" + id_serv + ", vlr_total=" + vlr_total + ", desconto=" + desconto + ", data_e="
				+ data_e + ", data_s=" + data_s + "]";
	}

}
