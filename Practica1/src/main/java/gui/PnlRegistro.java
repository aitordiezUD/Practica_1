package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import com.toedter.calendar.JDateChooser;

import clases.Datos;
import clases.Usuario;





public class PnlRegistro extends JPanel{
	private final String FORMATOCORREO = "^(.+)@(\\S+)$";
	
	private JTextField tfCorreo;
	private JDateChooser dateChooser;
	private JPasswordField pfContrasena;
	private JPasswordField pfContrasena2;
	
	public PnlRegistro() {
		setLayout(new BorderLayout());
		
		JPanel pnlMargN = new JPanel();
		pnlMargN.setPreferredSize(new Dimension(50, 20));
		add(pnlMargN, BorderLayout.NORTH);
		
		JPanel pnlMargS = new JPanel();
		pnlMargS.setPreferredSize(new Dimension(50, 50));
		add(pnlMargS, BorderLayout.SOUTH);
		
		JButton btnRegistrarse = new JButton("Registrarse");
		btnRegistrarse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (verificarCorreo(tfCorreo) & verificarContrasenas(pfContrasena, pfContrasena2) & verificarEdad(dateChooser)) {
					char[] passwordChars1 = pfContrasena.getPassword();
			        String contrasena1 = new String(passwordChars1);
			        
			        Datos.getLog().log(Level.INFO, "Usuario " + tfCorreo.getText() + " ha sido registrado exitosamente.");
			        
			        new Usuario(tfCorreo.getText(), contrasena1, dateChooser.getDate());
			        VentanaInicio.cardLayout.show(VentanaInicio.pnlContenido, "pnlLogIn");
			        
			        Datos.actualizarListas();
				}else {
					Datos.getLog().log(Level.INFO, "No se ha podido registrar al usuario: " + tfCorreo.getText());
				}
			}
		});
		pnlMargS.add(btnRegistrarse);
		
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
		pnlLblCorreo.setMaximumSize(new Dimension(32767, 30));
		pnlLblCorreo.setPreferredSize(new Dimension(200, 15));
		panel.add(pnlLblCorreo);
		pnlLblCorreo.setLayout(new BorderLayout(0, 0));
		
		JLabel lblCorreo = new JLabel("Correo electronico:");
		pnlLblCorreo.add(lblCorreo);
		
		tfCorreo = new JTextField();
		tfCorreo.setPreferredSize(new Dimension(6, 15));
		tfCorreo.setMinimumSize(new Dimension(7, 10));
		tfCorreo.setMaximumSize(new Dimension(2147483647, 30));
		panel.add(tfCorreo);
		
		JPanel pnlLblContrasena = new JPanel();
		pnlLblContrasena.setPreferredSize(new Dimension(200, 30));
		pnlLblContrasena.setMaximumSize(new Dimension(32767, 30));
		panel.add(pnlLblContrasena);
		pnlLblContrasena.setLayout(new BorderLayout(0, 0));
		
		JLabel lblContrasena = new JLabel("Contraseña: ");
		lblContrasena.setMinimumSize(new Dimension(63, 1));
		lblContrasena.setPreferredSize(new Dimension(200, 30));
		lblContrasena.setMaximumSize(new Dimension(63, 30));
		pnlLblContrasena.add(lblContrasena);
		
		pfContrasena = new JPasswordField();
		pfContrasena.setMaximumSize(new Dimension(2147483647, 30));
		pfContrasena.setPreferredSize(new Dimension(40, 15));
		panel.add(pfContrasena);
		
		JPanel pnlLblRepContrasena = new JPanel();
		pnlLblRepContrasena.setMinimumSize(new Dimension(10, 1));
		pnlLblRepContrasena.setPreferredSize(new Dimension(200, 6));
		pnlLblRepContrasena.setMaximumSize(new Dimension(32767, 30));
		panel.add(pnlLblRepContrasena);
		pnlLblRepContrasena.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Repetir Contraseña:\r\n");
		pnlLblRepContrasena.add(lblNewLabel);
		
		pfContrasena2 = new JPasswordField();
		pfContrasena2.setPreferredSize(new Dimension(40, 15));
		pfContrasena2.setMaximumSize(new Dimension(2147483647, 30));
		panel.add(pfContrasena2);
		
		JPanel pnlLblNcto = new JPanel();
		pnlLblNcto.setPreferredSize(new Dimension(200, 30));
		pnlLblNcto.setMaximumSize(new Dimension(32767, 50));
		panel.add(pnlLblNcto);
		pnlLblNcto.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFechaNcto = new JLabel("Fecha de Nacimiento:");
		lblFechaNcto.setPreferredSize(new Dimension(200, 30));
		lblFechaNcto.setMaximumSize(new Dimension(63, 40));
		pnlLblNcto.add(lblFechaNcto);
		
		dateChooser = new JDateChooser();
		
		JPanel pnlDateChooser = new JPanel();
		pnlDateChooser.setMaximumSize(new Dimension(32767, 40));
		pnlDateChooser.setLayout(new BorderLayout(0, 0));
		pnlDateChooser.add(dateChooser);
		panel.add(pnlDateChooser);
	}
	
    protected boolean verificarEdad(JDateChooser dateChooser) {

        Date fechaSeleccionada = dateChooser.getDate();

        if (fechaSeleccionada != null) {
            // Obtener la fecha actual
            Date fechaActual = new Date();

            // Calcular la diferencia de años
            int diferenciaAnios = calcularDiferenciaAnios(fechaActual, fechaSeleccionada);
            // Verificar si la diferencia es al menos 18 años
            if (diferenciaAnios >= 18) {
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Debes tener al menos 18 años.");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una fecha.");
            return false;
        }
    }
    
    protected boolean verificarContrasenas(JPasswordField pfContrasena, JPasswordField pfContrasena2) {
    	if (pfContrasena.getPassword() == null | pfContrasena2.getPassword() == null) {
    		JOptionPane.showMessageDialog(null, "Indique la contraseña que desee.");
        	return false;
    	}
    	
    	char[] passwordChars1 = pfContrasena.getPassword();
        char[] passwordChars2 = pfContrasena2.getPassword();

        String contrasena1 = new String(passwordChars1);
        String contrasena2 = new String(passwordChars2);
        
		if (contrasena1.equals(contrasena2)) {
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Las dos contraseñas no coinciden");
			return false;
		}
    }
    
    protected boolean verificarCorreo(JTextField tfCorreo) {
    	Pattern pattern = Pattern.compile(FORMATOCORREO);
    	Matcher matcher = pattern.matcher(tfCorreo.getText());
    	
    	if (matcher.matches()) {
    		if (Datos.getMapaCorreos().keySet().contains(tfCorreo.getText())) {
    			JOptionPane.showMessageDialog(null, "El correo electrónico ya lo tiene otro usuario en nuestro sistema");
        		return false;
    		}
    		return true;
    	}else {
    		JOptionPane.showMessageDialog(null, "El correo electrónico no tiene un formato adecuado");
    		return false;
    	}
    	
    };
    
    protected static int calcularDiferenciaAnios(Date fechaInicio, Date fechaFin) {
        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(fechaInicio);

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(fechaFin);

        int anios = calInicio.get(Calendar.YEAR) - calFin.get(Calendar.YEAR); 

        // Ajustar si aún no ha pasado el cumpleaños
        if (calInicio.get(Calendar.DAY_OF_YEAR) > calFin.get(Calendar.DAY_OF_YEAR)) {
            anios--;
        }

        return anios;
    }
}
