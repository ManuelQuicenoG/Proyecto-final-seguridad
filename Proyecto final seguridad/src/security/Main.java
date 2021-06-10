package security;

import java.math.BigInteger;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		
		//Inicializamos el sistema y generamos las 2 claves públicas.
		//Guardamos ambos valores para utilizarlos posterior.
		Initialize init = new Initialize();
		int primo = init.getPrimo();
		int generador = init.getGenerador();
		System.out.println("*** Claves públicas *** \nPrimo: "+primo+"\nGenerador: "+generador+"\n");
		
		//Declaramos e inicializamos los 2 clientes que usarán el sistema.
		//Les asignamos las claves públicas generadas anteriormente.
		User a = new User("Anderson");
		a.asignaClavesPublicas(primo, generador);
		a.calculaSecreto();
		a.calculaClave();
		
		User b = new User("María");
		b.asignaClavesPublicas(primo, generador);
		b.calculaSecreto();
		b.calculaClave();
		
		//Ambos clientes calculan la clave compartida.
		//Esto debe ser privado, no debería poder acceder.
		int sharedAB = a.secretoComun(b.getClave());
		int sharedBA = b.secretoComun(a.getClave());
		
		if(sharedAB == sharedBA) {
		System.out.println("La clave compartida es igual.");
		}
		else {
			System.out.println("La clave compartida es distinta.");
		}
	
	}
}
