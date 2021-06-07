package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
	JFrame ventanaChat;
	JButton btnEnviar;
	JTextField txtMesaje;
	JTextArea areaChat;
	JPanel contenedorAreaChat;
	JPanel contenedorBtntxt;
	JLabel image1; 
	JLabel image2;
	
	public Client (){
		inicializateGUI();
	}
	
	public void inicializateGUI() {
		
		//componentes
		ventanaChat = new JFrame("User 1");
		btnEnviar = new JButton("Enviar");
		txtMesaje = new JTextField(4);
		areaChat = new JTextArea(10, 12);
		
		//contenedores
		contenedorAreaChat = new JPanel(new GridLayout(1,1));
		contenedorAreaChat.add(areaChat);
		contenedorBtntxt = new JPanel(new GridLayout(1,2));
		contenedorBtntxt.add(txtMesaje);
		contenedorBtntxt.add(btnEnviar);
		
		//imagenes
		image1 = new JLabel();
		File archivoImagen = new File("resourses/" + "image1" + ".jpg");
		image1.setIcon(new ImageIcon(archivoImagen.getPath()));
		
		//ventana
		ventanaChat.setLayout(new BorderLayout());
		ventanaChat.add(contenedorAreaChat, BorderLayout.NORTH);
		ventanaChat.add(contenedorBtntxt, BorderLayout.SOUTH);
		ventanaChat.add(image1, BorderLayout.EAST);
		ventanaChat.setSize(300,220);
		ventanaChat.setVisible(true);
		ventanaChat.setResizable(false);
		ventanaChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		new Client();
	}
}
