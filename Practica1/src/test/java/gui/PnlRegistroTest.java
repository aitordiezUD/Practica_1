package gui;

import static org.junit.Assert.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.toedter.calendar.JDateChooser;

import clases.Datos;
import clases.Usuario;

import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

@RunWith(MockitoJUnitRunner.class)
public class PnlRegistroTest {

    @Mock
    private JTextField mockTfCorreo;

    @Mock
    private JDateChooser mockDateChooser;

    @Mock
    private JPasswordField mockPfContrasena;

    @Mock
    private JPasswordField mockPfContrasena2;

    
	private Datos datos;

    
	@Before
	public void setUp() throws Exception {
		datos = new Datos("datosTest.dat");
	}
    
    @Test
    public void testVerificarEdadConFechaValida() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaString = "10/11/1990";
		Date fecha = null;
		try {
			fecha = sdf.parse(fechaString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockDateChooser.getDate()).thenReturn(fecha); // Fecha válida
        assertTrue(pnlRegistro.verificarEdad(mockDateChooser));
    }

    @Test
    public void testVerificarEdadConFechaInvalida() {
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockDateChooser.getDate()).thenReturn(new Date()); // Fecha inválida
        assertFalse(pnlRegistro.verificarEdad(mockDateChooser));
    }

    @Test
    public void testVerificarContrasenasConContrasenasIguales() {
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockPfContrasena.getPassword()).thenReturn("contrasena".toCharArray());
        when(mockPfContrasena2.getPassword()).thenReturn("contrasena".toCharArray());
        assertTrue(pnlRegistro.verificarContrasenas(mockPfContrasena, mockPfContrasena2));
    }

    @Test
    public void testVerificarContrasenasConContrasenasDiferentes() {
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockPfContrasena.getPassword()).thenReturn("contrasena1".toCharArray());
        when(mockPfContrasena2.getPassword()).thenReturn("contrasena2".toCharArray());
        assertFalse(pnlRegistro.verificarContrasenas(mockPfContrasena, mockPfContrasena2));
    }

    @Test
    public void testVerificarContrasenasConContraseñaNula() {
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockPfContrasena.getPassword()).thenReturn(null);
        when(mockPfContrasena2.getPassword()).thenReturn("contrasena".toCharArray());
        assertFalse(pnlRegistro.verificarContrasenas(mockPfContrasena, mockPfContrasena2));
    }

    @Test
    public void testVerificarCorreoConCorreoValido() {
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockTfCorreo.getText()).thenReturn("usuario@example.com");
        assertTrue(pnlRegistro.verificarCorreo(mockTfCorreo));
    }

    @Test
    public void testVerificarCorreoConCorreoInvalido() {
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockTfCorreo.getText()).thenReturn("usuario"); // Correo inválido
        assertFalse(pnlRegistro.verificarCorreo(mockTfCorreo));
    }

    @Test
    public void testVerificarCorreoConCorreoExistente() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaString = "10/11/1990";
		Date fecha = null;
		try {
			fecha = sdf.parse(fechaString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        PnlRegistro pnlRegistro = new PnlRegistro();
        when(mockTfCorreo.getText()).thenReturn("usuario@example.com");
        Datos.getMapaCorreos().put("usuario@example.com", new Usuario("usuario1@ejemplo.com", "Usuario1",fecha));
        assertFalse(pnlRegistro.verificarCorreo(mockTfCorreo));
    }
}