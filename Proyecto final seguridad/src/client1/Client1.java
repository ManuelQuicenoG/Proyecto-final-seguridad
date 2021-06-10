package client1;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

import security.User;

import java.awt.*;
import java.net.*;
import java.security.spec.KeySpec;
import java.util.Base64;
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
	private User user1;
	private static final String saltAES = "salttest";
	
	private int claveParaCifrar;
	
	
	public Client1() {
		hacerInterfaz();
	}

	
	public void hacerInterfaz() {
		ventana_chat = new JFrame("CLIENTE 1");
		btn_enviar = new JButton("ENVIAR");
		txt_mensaje = new JTextField(4);
		area_chat = new JTextArea(14, 20);
		area_chat.setEditable(false);
		user1 = new User("user1 client");
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
					socket = servidor.accept();
					boolean flag = true;
					lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while(flag) {
						String mensaje_recibido = lector.readLine();
						String parts[] =  mensaje_recibido.split(",");
						if(parts.length==3) {
							int primo = Integer.parseInt(parts[1]);
							int gene = Integer.parseInt(parts[2]);
							int claveCome = Integer.parseInt(parts[0]);
							user1.asignaClavesPublicas(primo, gene);
							user1.calculaSecreto();
							int clave = user1.calculaClave();
							claveParaCifrar = user1.secretoComun(claveCome);
							escritor = new PrintWriter(socket.getOutputStream(), true);
							escritor.println(clave+",ok,ready");
							flag = false;
							break;
						}
					}
					leer();
					escribir();
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
						 System.out.println("mensaje recibido: "+mensaje_recibido);
						 mensaje_recibido = getAESDecrypt(mensaje_recibido);
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
								String cif = getAES(enviar_mensaje);
								System.out.println("mensaje enviado: "+cif);
								escritor.println(cif);
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
	
	
	public String getAES(String data) {
        try {
            byte[] iv = new byte[16];
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec((claveParaCifrar+"").toCharArray(), saltAES.getBytes(), 65536, 128);
            SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getAESDecrypt(String data) {
        byte[] iv = new byte[16];
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec((claveParaCifrar+"").toCharArray(), saltAES.getBytes(), 65536, 128);
            SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	
	public static void main(String[] args) {
//		try {
			new Client1();
//		} catch (Exception e) {
//			
//		}
		
	}
}
