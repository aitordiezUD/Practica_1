package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import clases.Datos;
import clases.Usuario;

public class PnlLogIn extends JPanel{
	private JPasswordField pfContrasena;
	private JTextField tfCorreo;
	
	public PnlLogIn() {
		setLayout(new BorderLayout());
		
		
		JPanel pnlMargN = new JPanel();
		pnlMargN.setPreferredSize(new Dimension(50, 50));
		add(pnlMargN, BorderLayout.NORTH);
		
		JPanel pnlMargS = new JPanel();
		pnlMargS.setPreferredSize(new Dimension(50, 50));
		add(pnlMargS, BorderLayout.SOUTH);
		
		JPanel pnlMargW = new JPanel();
		pnlMargW.setPreferredSize(new Dimension(50, 50));
		add(pnlMargW, BorderLayout.WEST);
		
		JPanel pnlMargE = new JPanel();
		pnlMargE.setPreferredSize(new Dimension(50, 50));
		add(pnlMargE, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel pnlLblCorreo = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlLblCorreo.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		pnlLblCorreo.setMaximumSize(new Dimension(32767, 50));
		pnlLblCorreo.setPreferredSize(new Dimension(200, 50));
		panel.add(pnlLblCorreo);
		
		JLabel lblCorreo = new JLabel("Correo electronico:");
		lblCorreo.setPreferredSize(new Dimension(200, 40));
		lblCorreo.setMaximumSize(new Dimension(2000, 45));
		pnlLblCorreo.add(lblCorreo);
		
		tfCorreo = new JTextField();
		tfCorreo.setPreferredSize(new Dimension(7, 27));
		tfCorreo.setMaximumSize(new Dimension(2147483647, 50));
		panel.add(tfCorreo);
		
		JPanel pnlLblContrasena = new JPanel();
		pnlLblContrasena.setMaximumSize(new Dimension(32767, 50));
		FlowLayout flowLayout_1 = (FlowLayout) pnlLblContrasena.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEADING);
		panel.add(pnlLblContrasena);
		
		JLabel lblContrasena = new JLabel("Contraseña: ");
		lblContrasena.setPreferredSize(new Dimension(200, 30));
		lblContrasena.setMaximumSize(new Dimension(63, 40));
		pnlLblContrasena.add(lblContrasena);
		
		pfContrasena = new JPasswordField();
		pfContrasena.setMaximumSize(new Dimension(2147483647, 50));
		pfContrasena.setPreferredSize(new Dimension(40, 27));
		panel.add(pfContrasena);
		
		JPanel pnlBotones = new JPanel();
		panel.add(pnlBotones);
		
		JButton btnIniciar = new JButton("Iniciar Sesión");
		btnIniciar.setHorizontalAlignment(SwingConstants.LEADING);
		btnIniciar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				char[] passwordChars1 = pfContrasena.getPassword();
		        String contrasena = new String(passwordChars1);
		        if (autenticarUsuario(tfCorreo.getText(),contrasena)) {
		        	Usuario u = Datos.getMapaCorreos().get(tfCorreo.getText());
		        	new VentanaChat(u);
		        	Datos.getLog().log(Level.INFO, "Usuario " + tfCorreo.getText() + " ha iniciado sesión");
		        }else {
		        	Datos.getLog().log(Level.INFO, "Autenticación errónea del usuario: " + tfCorreo.getText());
		        }
		        
			}
		});
		pnlBotones.add(btnIniciar);
		
		JButton btnRegistrarse = new JButton("Registrarse");
		btnRegistrarse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				VentanaInicio.cardLayout.show(VentanaInicio.pnlContenido, "pnlRegistro");
				Datos.getLog().log(Level.INFO, "Cambiando al panel de Registro");
			}
		});
		pnlBotones.add(btnRegistrarse);
	}
	
	private boolean autenticarUsuario(String correo ,String contrasena) {
		if (Datos.getMapaCorreos().containsKey(correo)) {
			Usuario u = Datos.getMapaCorreos().get(correo);
			if (u.getPasswd().equals(contrasena)) {
				return true;
			}else {
				JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
				return false;
			}
		}else {
			JOptionPane.showMessageDialog(null, "El correo no existe");
			return false;
		}
		
	};
}
