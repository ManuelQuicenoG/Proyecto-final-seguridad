package client2;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client2 {
	private JFrame ventanaChat;
	private JButton btnEnviar;
	private JTextField txtMesaje;
	private JTextArea areaChat;
	private JPanel contenedorAreaChat;
	private JPanel contenedorBtntxt;
	private JLabel image1; 
	private JLabel image2;
	private JLabel banner;
	private JScrollPane scrollPanel;
	private Socket socket;
	private BufferedReader lector;
	private PrintWriter escritor;
	
	public Client2 (){
		inicializateGUI();
	}
	
	public void inicializateGUI() {
		
		//componentes
		ventanaChat = new JFrame("CLIENTE 2");
		btnEnviar = new JButton("ENVIAR");
		txtMesaje = new JTextField(4);
		areaChat = new JTextArea(14, 20);
		areaChat.setEditable(false);
		Font fuente=new Font("Dialog", Font.BOLD, 14);
		areaChat.setFont ( fuente ) ;
		scrollPanel = new JScrollPane(areaChat);
		
		//contenedores
		contenedorAreaChat = new JPanel(new GridLayout(1,1));
		contenedorAreaChat.add(scrollPanel);
		contenedorBtntxt = new JPanel(new GridLayout(1,2));
		contenedorBtntxt.add(txtMesaje);
		contenedorBtntxt.add(btnEnviar);
		
		//imagenes
		image1 = new JLabel();
		image1.setIcon(new ImageIcon("resourses/image1.jpg"));
		image1.setBorder(null);
		image2 = new JLabel();
		image2.setIcon(new ImageIcon("resourses/image2.jpg"));
		image2.setBorder(null);
		banner = new JLabel();
		banner.setIcon(new ImageIcon("resourses/banner.jpg"));
		banner.setBorder(null);
		
		//ventana
		ventanaChat.setLayout(new BorderLayout());
		ventanaChat.add(image1, BorderLayout.WEST);
		ventanaChat.add(image2, BorderLayout.EAST);
		ventanaChat.add(banner, BorderLayout.NORTH);
		ventanaChat.add(contenedorAreaChat, BorderLayout.CENTER);
		ventanaChat.add(contenedorBtntxt, BorderLayout.SOUTH);
		ventanaChat.pack();
		ventanaChat.setVisible(true);
		ventanaChat.setResizable(false);
		ventanaChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Thread principal = new Thread(new Runnable() {
			public void run() {
				try {
					socket = new Socket("localhost", 9000);
					leer();
					escribir();
				}catch (Exception ex) {
					
				}
			}
		});
		principal.start();
	}
	
	public void leer() {
		Thread leer_hilo = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					 while(true) {
						 String mensaje_recibido = lector.readLine();
						 areaChat.append("EL OTRO USUARIO DICE: "+"\n"+ mensaje_recibido+"\n");
					 }
				} catch (Exception ex) {
					
				}
				
			}
		});
		
		leer_hilo.start();
		
	}
	
	
	public void escribir() {
		Thread escribir_hilo = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					escritor = new PrintWriter(socket.getOutputStream(), true);
					btnEnviar.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							String enviar_mensaje = txtMesaje.getText();
							if(!enviar_mensaje.isEmpty()) {
								escritor.println(enviar_mensaje);	
								areaChat.append("TÚ: "+"\n"+ enviar_mensaje+"\n");
								txtMesaje.setText("");
							}
						}
					});
				} catch (Exception e) {
					
				}
				
			}
		});
		escribir_hilo.start();
	}
	
	public static void main(String[] args) {
		try {
			new Client2();
		} catch (Exception e) {
			
		}
		
	}
}
