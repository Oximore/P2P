/*  Main.java
 *  Blux
 *  14/02/12
 *
 *  Pour tester
 */


//package noeud;

public class Test{
    
    public static void main(String[] args){
	System.out.println("Essais");
	
	FichierConfiguration file = new FichierConfiguration("fichier.txt");
	System.out.print(file.toString());
	
	file.setNbConnexion(file.getNbConnexion() + 2);
	
	file.saveValues("fichier.txt");
	System.out.print(file.toString());

	System.out.println("Fin test");
    }
    
}

