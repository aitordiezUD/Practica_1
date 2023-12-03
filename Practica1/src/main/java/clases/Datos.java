package clases;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;

import gui.VentanaChat;

public class Datos {
	protected static HashMap<Integer, Usuario> mapaID;
	protected static HashMap<String, Usuario> mapaCorreos;
	protected static ArrayList<Usuario> usuarios;
	protected static HashMap<VentanaChat,JList> mapaActListas;
	protected static Logger log;
	
	public static Logger getLog() {
		return log;
	}
	public static HashMap<VentanaChat, JList> getMapaActListas() {
		return mapaActListas;
	}

	public static void setMapaActListas(HashMap<VentanaChat, JList> mapaActListas) {
		Datos.mapaActListas = mapaActListas;
	}

	public static HashMap<Integer, Usuario> getMapaID() {
		return mapaID;
	}

	public static void setMapaID(HashMap<Integer, Usuario> mapaID) {
		Datos.mapaID = mapaID;
	}

	public static HashMap<String, Usuario> getMapaCorreos() {
		return mapaCorreos;
	}

	public static void setMapaCorreos(HashMap<String, Usuario> mapaCorreos) {
		Datos.mapaCorreos = mapaCorreos;
	}

	public static ArrayList<Usuario> getUsuarios() {
		return usuarios;
	}

	public static void setUsuarios(ArrayList<Usuario> usuarios) {
		Datos.usuarios = usuarios;
	}

	
	
	public Datos(String fileName) {
		log = Logger.getLogger("logger-chat");
		try {
			FileHandler h = new FileHandler( "programaChat.xml" );
			h.setLevel(Level.INFO);
			log.addHandler(h);
		} catch (SecurityException e) {} catch (IOException e) {}
		
		
		log.log(Level.INFO, "Inicio del programa");
		
		mapaID = new HashMap<>();
		usuarios = new ArrayList<>();
		mapaCorreos = new HashMap<>();
		mapaActListas = new HashMap<>();
		
		cargarDatos(fileName);
	}
	
//	public static void main(String[] args) {
//		new Datos();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        String fechaString = "10/11/1990";
//        Date fecha = null;
//        try {
//			fecha = sdf.parse(fechaString);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Usuario u1 = new Usuario("user1@deusto.es", "asdf", fecha);
//		Usuario u2 = new Usuario("user2@deusto.es", "asdf", fecha);
//		Usuario u3 = new Usuario("user3@deusto.es", "asdf", fecha);
//		
//		guardarDatos(datosTest.dat);
//	}
	
	public static void anadirUsuario(Usuario u) {
		mapaID.put(u.getId(), u);
		mapaCorreos.put(u.getCorreo(),u);
		usuarios.add(u);
	}
	
	public static void cargarDatos(String fileName) {
		(new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				File f = new File(fileName);
				FileInputStream fis = null;
				ObjectInputStream ois = null;
				try {
					fis = new FileInputStream(f);
					ois =  new ObjectInputStream(fis);
					Object o = ois.readObject();
					while ( o != null ) {
						Usuario u = ( Usuario ) o;
						Datos.anadirUsuario(u);
						try {
							o = ois.readObject();
						} catch (Exception e1) {
							// TODO: handle exception
							break;
						}
					}
					fis.close();
				} catch (Exception e) {
					// TODO: handle exception
					Datos.getLog().log(Level.WARNING, "Error al cargar datos", e);
				}
			}
		}).start();	
	}
	
	public static void guardarDatos(String fileName) {
		(new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				File f  = new File(fileName);
				FileOutputStream fos = null;
				ObjectOutputStream oos = null;
				
				
				try {
					fos = new FileOutputStream(f);
					oos = new ObjectOutputStream(fos);
					for ( Usuario u: Datos.getUsuarios()) {
						oos.writeObject(u);
					}
					
				}catch(Exception e) {
					Datos.getLog().log(Level.WARNING, "Error al guardar datos", e);
				}finally {
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public static void actualizarListas() {
		for (VentanaChat vc : mapaActListas.keySet()) {
			vc.anadirContactos();
			vc.getListaContactos().repaint();
		}
	}
	
}
