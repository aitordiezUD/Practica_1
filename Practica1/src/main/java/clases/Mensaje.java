package clases;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Mensaje implements Serializable{
	private int from;
	private int to;
	private String mensaje;
	private Date date;
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Mensaje(int from, int to, String mensaje, Date date) {
		this.from = from;
		this.to = to;
		this.mensaje = mensaje;
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "[From: " + from + " To: " + to + "] " + mensaje + " (" + date + ")";
	}
	
	

}
