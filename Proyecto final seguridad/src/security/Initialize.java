package security;


public class Initialize {

	public int getPrimo() {
		int contador = 2;
		int numeroP = 1 + (int)(Math.random() * 500);
		boolean esPrimo=true;
		while ((esPrimo) && (contador!=numeroP)){
		    if (numeroP % contador == 0) {
		    	esPrimo = false;
		    	contador++;
		    }
		  }	
		return numeroP;
	}
	
	public int getGenerador() {
		int numeroG = 1 + (int)(Math.random() * 100);
		return numeroG;
	}
	

}
