package clases;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gui.VentanaChat;

public class DatosTest {

	private Datos datos;
	private Usuario usuario1;
    private Usuario usuario2;
	private Date fecha;
    
	@Before
	public void setUp() throws Exception {
		datos = new Datos("datosTest.dat");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaString = "10/11/1990";
		fecha = null;
		try {
			fecha = sdf.parse(fechaString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void comprobacionCargaDatos() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(3, Datos.getUsuarios().size());
		assertEquals(3, Datos.getMapaCorreos().size());
		assertEquals(3, Datos.getMapaID().size());
	}
	
    @Test
    public void testAnadirUsuario() {
    	usuario1 = new Usuario("usuario1@ejemplo.com", "Usuario1",fecha);
    	assertEquals(4, Datos.getUsuarios().size());
        assertEquals(usuario1, Datos.getMapaCorreos().get("usuario1@ejemplo.com"));
        assertTrue(Datos.getUsuarios().contains(usuario1));
    }
    
    @Test
	public void cargadoGuardado() {
    	usuario1 = new Usuario("usuario1@ejemplo.com", "Usuario1",fecha);
    	usuario2 = new Usuario("usuario2@ejemplo.com", "Usuario2",fecha);
    	
    	Datos.guardarDatos("datosTestGuardado.dat");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	datos = new Datos("datosTestGuardado.dat");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(5, Datos.getUsuarios().size());
		assertEquals(5, Datos.getMapaCorreos().size());
		assertEquals(5, Datos.getMapaID().size());
    }
    


}
