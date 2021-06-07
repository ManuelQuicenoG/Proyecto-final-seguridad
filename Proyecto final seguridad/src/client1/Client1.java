package client1;

import javax.swing.*;


import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
public class Client1 {
	
	private JFrame ventana_chat;
	private JButton btn_enviar;
	private JTextField txt_mensaje;
	private JTextArea area_chat;
	private JPanel contenedor_areachat;
	private JLabel image1; 
	private JLabel image2;
	private JLabel banner;
	private JPanel contenedor_btntxt;
	private JScrollPane scroll;
	private ServerSocket servidor;
	private Socket socket;
	private BufferedReader lector;
	private PrintWriter escritor;
	
	
	
	public Client1() {
		hacerInterfaz();
	}

	
	public void hacerInterfaz() {
		ventana_chat = new JFrame("CLIENTE 1");
		btn_enviar = new JButton("ENVIAR");
		txt_mensaje = new JTextField(4);
		area_chat = new JTextArea(14, 20);
		area_chat.setEditable(false);
		Font fuente=new Font("Dialog", Font.BOLD, 14);
		area_chat.setFont ( fuente ) ;
		scroll = new JScrollPane(area_chat);
		contenedor_areachat = new JPanel();
		contenedor_areachat.setLayout(new GridLayout(1,1));
		contenedor_areachat.add(scroll);
		contenedor_btntxt = new JPanel();
		contenedor_btntxt.setLayout(new GridLayout(1,2));
		contenedor_btntxt.add(txt_mensaje);
		contenedor_btntxt.add(btn_enviar);
		image1 = new JLabel();
		image1.setIcon(new ImageIcon("resourses/image1.jpg"));
		image1.setBorder(null);
		image2 = new JLabel();
		image2.setIcon(new ImageIcon("resourses/image2.jpg"));
		image2.setBorder(null);
		banner = new JLabel();
		banner.setIcon(new ImageIcon("resourses/banner.jpg"));
		banner.setBorder(null);
		ventana_chat.setLayout(new BorderLayout());
		ventana_chat.add(image1, BorderLayout.WEST);
		ventana_chat.add(image2, BorderLayout.EAST);
		ventana_chat.add(banner, BorderLayout.NORTH);
		ventana_chat.add(contenedor_areachat, BorderLayout.CENTER);
		ventana_chat.add(contenedor_btntxt, BorderLayout.SOUTH);
		ventana_chat.pack();
		ventana_chat.setVisible(true);
		ventana_chat.setResizable(false);
		ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Thread principal = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					servidor = new ServerSocket(9000);
					while(true) {
						socket = servidor.accept(); 
						leer();
						escribir();
					}
				} catch (Exception e) {
					
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
						 area_chat.append("EL OTRO USUARIO DICE: "+"\n"+ mensaje_recibido+"\n");
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
					btn_enviar.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							String enviar_mensaje = txt_mensaje.getText();
							if(!enviar_mensaje.isEmpty()) {
								escritor.println(enviar_mensaje);
								area_chat.append("TÚ: "+"\n"+ enviar_mensaje+"\n");
								txt_mensaje.setText("");
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
			new Client1();
		} catch (Exception e) {
			
		}
		
	}
}
