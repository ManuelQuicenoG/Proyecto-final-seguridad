package security;

//import java.math.BigInteger;
//import java.util.Random;

public class User {

	private int primo;
	private int generador;
	private int secreto;
	private int clave;
	private int claveCompartida;
	private String nombre;
	
	public User(String nombre) {
		this.nombre = nombre;
	}
	
	public void asignaClavesPublicas(int p, int g) {
		primo = p;
		generador = g;
	}
	
	
	public int calculaSecreto() {
		secreto = 1 + (int)(Math.random() * 100);
		
		while(primo > secreto) {
			secreto = 1 + (int)(Math.random() * 100);
			if(primo > secreto) {
				break;
			}
		}
		return secreto;
	}

	
	/** <<Algoritmo>>
	 * Alice y Bob acordaron usar p = 23 y base g = 5
	 * Alice escoge un int secreto a = 6
	 * y se lo envía a Bob A = g^a mod p
	 * A = 5^6 mod 23 = 8.
	 * A = g^a mod p 
	 */
	
	
	public int calculaClave(){
		int x = (int)Math.pow(generador, secreto);
		clave = x % primo;
		return clave;
	}
	
	
	public int secretoComun(int clave) {
		int y = (int)Math.pow(clave, secreto);
		claveCompartida = y % primo;
		return claveCompartida; 
	}
	
	public int getClave() {
		return clave;
	}

	public void setClave(int clave) {
		this.clave = clave;
	}


	
//	public BigInteger calculaSecretoA() {
//		int numBits = 128;
//		BigInteger secreto = new BigInteger(numBits, new Random());
//		System.out.println(secreto);
//		return secreto;
//	}
	
	
//	
//	public BigInteger calculaClave(){
//		clave = generador.modPow(secreto, primo);
//		return clave;
//	}
//
//	
//	public BigInteger secretoComun(BigInteger clave) {
//		sharedKey = clave.modPow(secreto, primo);
//		return sharedKey; 
//	}
//		
	
}
