package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import clases.Datos;
import clases.Mensaje;
import clases.Usuario;
//import es.deusto.prog3.utils.comunicacion.ConfigCS;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

enum tipoMensaje {ENVIO, RECEPCION};

public class VentanaChat extends JFrame {
	private Usuario usuario;
	private DefaultListModel<Usuario> modeloLista;
	private JList<Usuario> listaContactos;
	private Usuario contacto = null;
	private JPanel pnlChatsContent;
	private CardLayout layoutChats;
	private HashMap<Integer, MiPanelChat> mapaPaneles;
	
	
//	SOCKETS:
	private boolean finComunicacion = false;
	private ObjectOutputStream flujoOut;
	
	
	
	public ObjectOutputStream getFlujoOut() {
		return flujoOut;
	}

	public JList<Usuario> getListaContactos() {
		return listaContactos;
	}

	public VentanaChat(Usuario usuario) {
		setSize(800,600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle(usuario.getCorreo());
		
		Datos.getLog().log(Level.INFO,"Creación de ventana de chat para usuario: " + usuario.getId());
		
//		(new Thread() {@Override public void run() {VentanaChat.this.lanzaCliente();}}).start();
		(new Thread(() -> {VentanaChat.this.lanzaCliente();})).start();
		
		this.usuario = usuario;
		mapaPaneles = new HashMap<>();
		
//		CREACION PANEL CHAT

		pnlChatsContent = new JPanel();
		layoutChats = new CardLayout();
		pnlChatsContent.setLayout(layoutChats);
		getContentPane().add(pnlChatsContent);
		
		
//		CREACION DE LA LISTA A LA PARTE IZQUIERDA DEL PANEL
		modeloLista = new DefaultListModel<Usuario>();
		listaContactos = new JList<Usuario>(modeloLista);
		listaContactos.setPreferredSize(new Dimension(200,200));
		anadirContactos();
		
		Datos.getMapaActListas().put(this, listaContactos);
		getContentPane().add(new JScrollPane(listaContactos),BorderLayout.WEST);
		
		
//		listaContactos.setCellRenderer(new DefaultListCellRenderer() {
//			private static final long serialVersionUID = 1L;
//			JPanel pnl;
//			JLabel lbl;
//			
//			@Override
//			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
//					boolean isSelected, boolean cellHasFocus) {
//				pnl = new JPanel();
//				pnl.setPreferredSize(new Dimension(200,40));
//				if (isSelected) {
//                    pnl.setBackground(new Color(122, 199, 218));
//                } else {
//                    pnl.setBackground(new Color(202, 232, 232));
//                    pnl.setForeground(list.getForeground());
//                }
//				lbl = new JLabel(value.toString());
//				pnl.add(lbl);
//				return pnl;
//			}
//			
//		});
		
		listaContactos.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
		    JPanel pnl = new JPanel();
		    pnl.setPreferredSize(new Dimension(200, 40));

		    if (isSelected) {
		        pnl.setBackground(new Color(122, 199, 218));
		    } else {
		        pnl.setBackground(new Color(202, 232, 232));
		        pnl.setForeground(list.getForeground());
		    }

		    JLabel lbl = new JLabel(value.toString());
		    pnl.add(lbl);

		    return pnl;
		});
		
		listaContactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                listaContactos.clearSelection();
            }

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				int index = listaContactos.locationToIndex(e.getPoint());
				if (index != -1) {
					VentanaChat.this.contacto = modeloLista.get(index);
					layoutChats.show(pnlChatsContent,contacto.getId()+"");
				}
			}
        });
		
		
		listaContactos.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = listaContactos.locationToIndex(e.getPoint());
                if (index != -1) {
                    listaContactos.setSelectedIndex(index);
                }
            }
        });
		if (modeloLista.getSize()>0) {
			layoutChats.show(pnlChatsContent, modeloLista.get(0).getId()+"");
		}
		
//		PREPARACION SOCKETS
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Datos.getLog().log(Level.INFO, "Usuario: " + usuario.getId() + " cierra su ventana de chat, se procede a cerrar la comunicación");
				finComunicacion = true;
				Datos.getMapaActListas().remove(VentanaChat.this);
			}
		});
		

		setVisible(true);
	}
	
	
	public void anadirContactos() {
		for (Usuario u: Datos.getUsuarios()) {
			if (!u.equals(this.usuario)) {
				if (!modeloLista.contains(u)) {
				modeloLista.add(modeloLista.getSize(),u);
				MiPanelChat p = new MiPanelChat(usuario,u, VentanaChat.this);
				pnlChatsContent.add(p,u.getId()+"");
				mapaPaneles.put(u.getId(), p);
				}
			}
		}
	}
	
	public void lanzaCliente() {
		Datos.getLog().log(Level.INFO, "Lanzando cliente ; Usuario: " + usuario.getId());
		try (Socket socket = new Socket( "localhost", 4000 )) {
    		socket.setSoTimeout( 1000 ); // Pone el timeout para que no se quede eternamente en la espera (1)
            flujoOut = new ObjectOutputStream(socket.getOutputStream());
            flujoOut.writeObject(usuario.getId()); // Mensaje que envia el ID del sender
            ObjectInputStream echoes = new ObjectInputStream(socket.getInputStream());
            Object echo1 = echoes.readObject();
            do {
            	try {
            		Mensaje mensaje = (Mensaje) echoes.readObject();  // Devuelve mensaje de servidor o null cuando se cierra la comunicación
            		mapaPaneles.get(mensaje.getFrom()).locateMessage(tipoMensaje.RECEPCION, mensaje);
    			} catch (SocketTimeoutException e) {Datos.getLog().log(Level.FINER, "Esperando mensajes del servidor ; usuario: " +usuario.getId() );} // Excepción de timeout - no es un problema
            }while(!finComunicacion);
			flujoOut.writeObject( "\\#FINDECOMUNICACION" );
			Datos.getLog().log(Level.INFO, "Cerrando conexion con el servidor ; usuario: " + usuario.getId());
        } catch (Exception e) {
        	Datos.getLog().log(Level.WARNING, "Error al lanzar cliente ; usuario: " + usuario.getId() + " ; Se procede a cerrar la comunicación");
        	JOptionPane.showMessageDialog(null, "Recuerda lanzar el servidor");
        	finComunicacion = true;
        	VentanaChat.this.dispose();
        }
	}

    
    private static class MiPanelChat extends JPanel{
    	JScrollPane spPnlChatMensajes;
    	JLabel lblContacto;
    	JPanel pnlChatMensajes;
    	JPanel pnlContacto;
    	JPanel pnlTextField;
    	JTextField tfMensaje;
    	
    	
    	public MiPanelChat(Usuario usuario ,Usuario contacto, VentanaChat vc) {
    		
    		Datos.getLog().log(Level.INFO, "Creacion panel chat entre: " + usuario.getId() + " y " + contacto.getId());
    		
    		setBackground(Color.WHITE);
    		setLayout(new BorderLayout());
    		pnlChatMensajes = new JPanel();
    		pnlChatMensajes.setLayout(new BoxLayout(pnlChatMensajes, BoxLayout.Y_AXIS));
    		spPnlChatMensajes = new JScrollPane(pnlChatMensajes);
    		
    		
    		pnlContacto = new JPanel();
    		pnlContacto.setPreferredSize(new Dimension(50,40));
    		pnlContacto.setBackground(new Color(202, 232, 232));
    		pnlContacto.setLayout(new BorderLayout(0, 0));
    		lblContacto = new JLabel();
    		lblContacto.setHorizontalAlignment(SwingConstants.CENTER);
    		pnlContacto.add(lblContacto, BorderLayout.CENTER);
    		add(pnlContacto, BorderLayout.NORTH);
    		
    		pnlTextField = new JPanel();
    		pnlTextField.setLayout(new BorderLayout());
    		tfMensaje = new JTextField();
    		pnlTextField.add(tfMensaje);
    		pnlTextField.setPreferredSize(new Dimension(200,40));
    		add(pnlTextField, BorderLayout.SOUTH);
    		
    		add(spPnlChatMensajes, BorderLayout.CENTER);
    		
    		setLblContacto(contacto);
    		
    		
    		
    		tfMensaje.addActionListener( new ActionListener() { // Evento de <enter> de textfield
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				Mensaje mensaje = new Mensaje(usuario.getId(), contacto.getId(), tfMensaje.getText(), new Date());
    				locateMessage(tipoMensaje.ENVIO, mensaje);
    				tfMensaje.setText( "" );
    				try {
    					vc.getFlujoOut().writeObject(mensaje);
    					
    				} catch (IOException e1) {  // Error en writeObject
    					Datos.getLog().log(Level.SEVERE, "No se ha podido envier el mensaje: " + mensaje, e1);
    					JOptionPane.showMessageDialog(null, "No se ha podido enviar el mensaje.");
    				}
    			}
    		});
    	}
    	
    	public void setLblContacto(Usuario u) {
    		lblContacto.setText(u.getCorreo());
    	}
    	
		private void locateMessage(tipoMensaje tipo, Mensaje mensaje) {
		    	
				pnlChatMensajes.revalidate();
				pnlChatMensajes.repaint();
		        
		        JPanel messagePanel = null;
		        if (tipo.equals(tipoMensaje.ENVIO)) {
		        	messagePanel = sendedMessage(mensaje);
		        }else {
		        	messagePanel = receivedMessage(mensaje);
		        }
		        
		        
		        pnlChatMensajes.add(messagePanel);
		        
		        
		        // Actualizar la interfaz gráfica
		        pnlChatMensajes.revalidate();
		        pnlChatMensajes.repaint();
		        
		        // Desplazar la barra de desplazamiento hacia abajo
		        SwingUtilities.invokeLater(() -> {
		            JScrollBar verticalScrollBar = spPnlChatMensajes.getVerticalScrollBar();
		            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
		        });
		        
		    }
		
		public static JPanel sendedMessage(Mensaje msg) {
	    	// Crear un nuevo panel para cada mensaje
	        JPanel panel = formatLabel(msg);
	        JPanel messagePanel = new JPanel(new BorderLayout());
	        messagePanel.add(panel, BorderLayout.EAST);
	        Integer width = (int) messagePanel.getMaximumSize().getWidth();
	        Integer height = (int) messagePanel.getPreferredSize().getHeight();
	        messagePanel.setMaximumSize(new Dimension(width, height));
	        return messagePanel;
	    }
	    
	    public static JPanel receivedMessage(Mensaje msg) {
	    	// Crear un nuevo panel para cada mensaje
	        JPanel panel = formatLabel(msg);
	        JPanel messagePanel = new JPanel(new BorderLayout());
	        messagePanel.add(panel, BorderLayout.WEST);
	        Integer width = (int) messagePanel.getMaximumSize().getWidth();
	        Integer height = (int) messagePanel.getPreferredSize().getHeight();
	        messagePanel.setMaximumSize(new Dimension(width, height));
	        return messagePanel;
	    }
	    
	    public static JPanel formatLabel(Mensaje out) {
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	        
	        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out.getMensaje() + "</p></html>");
	        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
	        output.setBackground(new Color(37, 211, 102));
	        output.setOpaque(true);
	        output.setBorder(new EmptyBorder(15, 15, 15, 50));
	        
	        panel.add(output);
	        
	//        Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	        
	        JLabel lblTime = new JLabel();
	        lblTime.setText(sdf.format(out.getDate()));
	        
	        panel.add(lblTime);
	        
	        return panel;
	    }
    	
    }
	

}
