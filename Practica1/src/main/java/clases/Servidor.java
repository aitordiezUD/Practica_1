package clases;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gui.VentanaInicio;






public class Servidor {
	private static final int PUERTO = 4000;
	
	//Atributos para comunicacion
	private static Map<Integer, ObjectOutputStream> mapaDirecciones;
	private static Vector<HiloComunicacion> listaHilos; // Lista de hilos de comunicación  
	private static Vector<Socket> listaSockets;
	private static boolean finComunicacion = false;
    private static int numTotalClientes = 0;
    private static Map<Integer, List<Mensaje>> mapaMensajasPorEnviar;
	
	// Atributos visuales
	private static VentanaServidor vs;  // Ventana del servidor
	private static JLabel lEstado = new JLabel( " " );
	private static JTextArea taMensajes = new JTextArea( 10, 1 );
	
	public Servidor() {
		Datos datos = new Datos("datos.dat");
		Datos.getLog().log(Level.INFO,"Creación Ventana Servidor");
		mapaDirecciones = new HashMap<>();
		mapaDirecciones = Collections.synchronizedMap(mapaDirecciones);
		mapaMensajasPorEnviar = new HashMap<>();
		mapaMensajasPorEnviar = Collections.synchronizedMap(mapaMensajasPorEnviar);
		listaHilos = new Vector<>();
		listaSockets = new Vector<>();
		vs = new VentanaServidor();
		vs.setVisible( true );
//		(new Thread() {
//			@Override
//			public void run() {
//				vs.lanzaServidor();
//			}
//		}).start();
		new Thread(() -> {vs.lanzaServidor();}).start();
	}
	
	public static void main(String[] args) {
		new Servidor();
	}
	
	@SuppressWarnings("serial")
	public static class VentanaServidor extends JFrame {
		public VentanaServidor() {
			setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			setSize( 500, 300 );
			setLocation( 400, 0 );
			setTitle( "Ventana servidor" );
			getContentPane().add( new JScrollPane(taMensajes), BorderLayout.CENTER );
			getContentPane().add( lEstado, BorderLayout.SOUTH );
			addWindowListener( new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					finComunicacion = true;
				}
			});
		}
	    public void lanzaServidor() {
	    	Datos.getLog().log(Level.INFO, "Lanzando el servidor");
	    	try(ServerSocket serverSocket = new ServerSocket( PUERTO )) {
	    		serverSocket.setSoTimeout( 5000 );  // Pone un timeout de 5 milisegundos
		    	while (!finComunicacion) {
		    		// Escucha una conexión por parte de cliente y se queda bloqueado hasta que no se recibe
		    		try {
		    			Socket socket = serverSocket.accept();   
			    		HiloComunicacion hilo = new HiloComunicacion( socket);
			    		hilo.start();
			    		
		    		} catch (SocketTimeoutException e) {}  // Si es un timeout no es un error, simplemente se vuelve a intentar salvo que la ventana se cierre (finComunicacion)
		    	}
		    	serverSocket.close();  // Cierra el socket de servidor
	    	} catch(IOException e) {
	    		Datos.getLog().log(Level.SEVERE, "Error al lanzar el servidor", e);
//	    		e.printStackTrace();
	    		lEstado.setText("Error en servidor: " + e.getMessage());
	    	}
	    }
	}
	
	private static class HiloComunicacion extends Thread {
		private Socket socket;  // socket de comunicación con cada cliente
		private int idSender; // nombre de cada cliente
		public HiloComunicacion(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
	    	try {
	    		socket.setSoTimeout( 1000 ); // Pone el timeout para que no se quede eternamente en la espera (1)
	    		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());  // Canal de entrada de socket (leer del cliente)
	    		idSender = (int) input.readObject();
	    		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());  // Canal de salida de socket (escribir al cliente)
	    		output.writeObject("Recibido");
	    		listaHilos.add( this );
	    		listaSockets.add( socket );
	    		mapaDirecciones.put( idSender, output );
	    		if (mapaMensajasPorEnviar.containsKey(idSender)) {
	    			for (Mensaje m : mapaMensajasPorEnviar.get(idSender)) {
	    				output.writeObject(m);
	    			}
	    		}
	    		while(!finComunicacion) {  // Bucle de comunicación de tiempo real con el cliente
	    			try {
		    			Object objRecibido = input.readObject();  // Espera a recibir petición de cliente (1) - se acaba con timeout
		    			if (objRecibido.equals("\\#FINDECOMUNICACION")) {
		    				break;
		    			}
		    			
		    			Mensaje mensaje = (Mensaje) objRecibido;
		    			taMensajes.append(mensaje.toString()+ "\n");
		    			taMensajes.setSelectionStart( taMensajes.getText().length() );  // Pone el cursor al final del textarea
		    			// Envía el mensaje al cliente destinatario si su OOS esta en el mapa de direcciones, sino se almacenara en el 
		    			//mapa de mensajes por enviar hasta que el destinatario este conectado.
		    			if (mapaDirecciones.get(mensaje.getTo()) != null) {
		    				mapaDirecciones.get(mensaje.getTo()).writeObject(mensaje);
		    			}else {
		    				if (mapaMensajasPorEnviar.get(mensaje.getTo()) == null) {
		    					mapaMensajasPorEnviar.put(mensaje.getTo(), new ArrayList<Mensaje>());
		    				}
		    				mapaMensajasPorEnviar.get(mensaje.getTo()).add(mensaje);
		    			}
		    			
	    			} catch (SocketTimeoutException e) {Datos.getLog().log(Level.FINER, "Esperando mensajes de clientes.");} // Excepción de timeout - no es un problema
	    		}
	    		socket.close();
	    		// Quita el cliente de las listas para que no se use en lo sucesivo
	    		int clienteACerrar = listaSockets.indexOf( socket );
	    		listaHilos.removeElementAt( clienteACerrar );
	    		listaSockets.removeElementAt( clienteACerrar );
	    		mapaDirecciones.remove(idSender);
	    		Datos.getLog().log(Level.INFO, "Cierre de hilo de comunicación con usuario: " + idSender);
	    	} catch(Exception e) {
	    		Datos.getLog().log(Level.SEVERE, "Error en el hilo de comunicación", e);
	    	}
		}
		
	}
	
}
