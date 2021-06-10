package security;

public class Initialize {

	public int getPrimo() {
		int contador = 2;
		int numeroP = 1 + (int) (Math.random() * 1000);
		boolean esPrimo = true;
		while (true) {
			if (esPrimo) {
				if (numeroP % contador == 0) {
					esPrimo = false;
				}
				if(numeroP==1) {
					esPrimo = false;
				}
				if(contador!=numeroP) {
					contador++;
				}else {
					break;
				}
			} else {
				numeroP = 1 + (int) (Math.random() * 500);
				contador = 2;
				esPrimo = true;
			}
		}
		return numeroP;
	}

	public int getGenerador() {
		int numeroG = 1 + (int) (Math.random() * 100);
		return numeroG;
	}

}
