package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPasswordField;

import clases.Datos;
import clases.Usuario;

public class VentanaInicio extends JFrame{
	protected static CardLayout cardLayout;
	protected static JPanel pnlContenido;
	protected static Datos datos;
	
	
	


	public static void main(String[] args) {
		new VentanaInicio();
	}

	public VentanaInicio() {
		setSize(400,400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Log In || Registro");
		setLocationRelativeTo(null);
		datos = new Datos("datos.dat");
		Datos.getLog().log(Level.INFO, "Iniciando Ventana de Inicio");
		
		
		
		cardLayout = new CardLayout();
		pnlContenido = new JPanel();
		pnlContenido.setLayout(cardLayout);
		pnlContenido.add("pnlLogIn", new PnlLogIn());
		pnlContenido.add("pnlRegistro", new PnlRegistro());
		setContentPane(pnlContenido);
		cardLayout.show(pnlContenido, "pnlLogIn");
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				Datos.getLog().log(Level.INFO, "Cerrando Ventana de Inicio");
				Datos.guardarDatos("datos.dat");
			}
		});
		
		setVisible(true);
	}
}

