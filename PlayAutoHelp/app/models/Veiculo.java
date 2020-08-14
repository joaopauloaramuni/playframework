package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Veiculo extends Model{
	
	@Id
	public int id_veic;
	
	@ManyToOne
	@JoinColumn(name="id_cliente")
	public Cliente cliente;
	
	public String marca;
	public String modelo;
	public String placa;
	public String ano_f;
	public String ano_m;
	public String km;
	public String oleo;
	public String foto;
	
	public Veiculo(int id_veic, String marca, String modelo, String placa, String ano_f, String ano_m, String km, String oleo, String foto)  {
		this.id_veic = id_veic;
		this.marca = marca;
		this.modelo = modelo;
		this.ano_f = ano_f;
		this.ano_m = ano_m;
		this.km = km;
		this.oleo = oleo;
		this.foto = foto;
	}

	@Override
	public String toString() {
		return "Veiculo [id_veic =" + id_veic + ", marca=" + marca + ", modelo=" + modelo + ", placa=" + placa + ", ano_f="
				+ ano_f + ", ano_m=" + ano_m + ", km=" + km + ", oleo=" + oleo + "]";
	}

}

