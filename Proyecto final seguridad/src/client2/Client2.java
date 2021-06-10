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
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import security.Initialize;
import security.User;

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
	private static final String saltAES = "salttest";
	private User user2;
	
	private int claveParaCifrar;
	
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
		
		user2 = new User("user 2"); 
		Initialize init = new Initialize();
		
		Thread principal = new Thread(new Runnable() {
			public void run() {
				try {
					socket = new Socket("localhost", 9000);
					escritor = new PrintWriter(socket.getOutputStream(), true);
					int primo = init.getPrimo();
					int gene = init.getGenerador();
					user2.asignaClavesPublicas(primo, gene);
					user2.calculaSecreto();
					int clave = user2.calculaClave();
					escritor.println(clave+","+primo+","+gene);
					lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					boolean flag = true;
					String mensaje = "";
					while(flag) {
						String mensaje_recibido = lector.readLine();
						if(mensaje_recibido.split(",").length==3) {
							String [] parts = mensaje_recibido.split(",");
							if(parts[1].equals("ok")) {
								mensaje = mensaje_recibido;
								flag = false;
							}
						}
					}
					String [] parts = mensaje.split(",");
					int claveCome = Integer.parseInt(parts[0]);
					claveParaCifrar = user2.secretoComun(claveCome);
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
						 System.out.println("mensaje recibido: "+mensaje_recibido);
						 mensaje_recibido = getAESDecrypt(mensaje_recibido);
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
								String cif = getAES(enviar_mensaje);
								System.out.println("mensaje enviado: "+cif);
								escritor.println(cif);
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
			new Client2();
//		} catch (Exception e) {
//			
//		}
		
	}
}
