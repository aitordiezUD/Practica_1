package clases;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class Usuario implements Serializable{
	private int id;
	private String correo;
	private String passwd;
	private String fecha;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Usuario(String correo, String passwd, Date fecha) {
		Random random = new Random();
		int id = -1;
		while (id == -1 | Datos.mapaID.containsKey(id)) {
			id = random.nextInt(1000);
		}
		this.id = id;
		this.correo = correo;
		this.passwd = passwd;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.fecha = sdf.format(fecha);
		Datos.mapaID.put(id, this);
		Datos.anadirUsuario(this);
	}
	
	@Override
	public String toString() {
		return correo;
	}
	@Override
	public int hashCode() {
		return Objects.hash(correo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(correo, other.correo);
	}
	
	
	
}
