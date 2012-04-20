/*  Pair.java
 *  Blux
 *  15/03/12
 *
 *  Thread principal, c'est avec lui que tout commence
 */
import java.util.Hashtable;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;
import java.net.SocketException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.lang.Math;

public class Pair{
    
    public static void main(String[] args){
	boolean estMisAssertion = false;
	assert estMisAssertion = true;

	System.out.println("Lancement du programme Pair");
	if (estMisAssertion)
	    System.out.println("Veuillez patienter quelques instants ...\n");

	FichierConfiguration fichierConf = new FichierConfiguration("configuration.txt");
	System.out.println(fichierConf);
	
	
	File repertoire = new File("Download");
	if ( ! repertoire.isDirectory() ) {
	    System.out.println("Le Dossier Download est introuvable");
	    System.exit(1);
	}
	
	// *TODO* filtrer les fichiers cachés   
	// use :"File[] listFiles(FileFilter filter)" 
	File[] list = repertoire.listFiles();

	Hashtable<String,Fichier> collection = new Hashtable<String,Fichier> (2*list.length, (float)0.75);
	
	for ( int i = 0; i < list.length ; i++) {
	    // si ce n'est pas un fichier caché
	    if ( list[i].exists() & list[i].getName().indexOf(".") != 0 ) {
		remplire(list[i], collection, fichierConf.getTaille());
	    }
	}


	// On dispose maintenant de la collection remplie
	sauver(collection);
	
	int localPort = 0;
	if (0 < args.length){
	    localPort = Integer.parseInt(args[0]); 
	}

	Serveur th2 = new Serveur("String name", collection, fichierConf, localPort);
	th2.start();
	
	/** /
	System.out.println("\nReprise du téléchargement des fichiers non complets");    
	RepriseTelechargement th6 = new RepriseTelechargement("th6", fichierConf, collection);
	th6.run();	
	System.out.println("Fin de la reprise des dl\n");    
	// */

	System.out.println("\nProgramme utilisateur");    
	ThreadUtilisateur th4 = new ThreadUtilisateur("th4", fichierConf, collection);
	th4.run();	
	System.out.println("Fin du programme utilisateur");    
	
	sauver(collection);
	System.out.println("Fin du programme Pair");    
	System.exit(0);
    }
    
    
    static private void sauver(Hashtable hash){
	System.out.println("Sauvegarde des fichiers cachés");    
	Enumeration e  = hash.elements();
	while (e.hasMoreElements())
	    ((Fichier) e.nextElement()).saveValue();
    }

    static private void remplire(File fichier, Hashtable hash, int taillePieces){
	String key = FileHashSum.sha1sum(fichier) ;
	Fichier element = new Fichier(fichier.getName(), key, fichier.length() , taillePieces) ; // *TODO* la clé n'est pas la bonne
	int taille;
	File fichierCache = new File("Download/."+fichier.getName()) ;
	boolean [] masque = null;
	// Si le fichier caché existe on récupère le masque
	if (fichierCache.exists()){
	    try {
		RandomAccessFile raf_fichierCache = new RandomAccessFile(fichierCache,"r") ;
		String strMasque = raf_fichierCache.readLine() ;
				
		masque = new boolean[strMasque.length()];
		for (int i=0 ; i<strMasque.length() ; i++){
		    if ((strMasque.indexOf('0',i)-i)==0){
			masque[i] = false;
		    } else if ((strMasque.indexOf('1',i)-i)==0){
			masque[i] = true;
		    }else {
			System.out.println("Pas cool in Pair.remplire :"+strMasque.charAt(i) + " i:"+ i );
			System.exit(42);
		    }
		}
				
		String strTaillePiece = raf_fichierCache.readLine() ;
		String strTaille = raf_fichierCache.readLine() ;
		element.setTaillePiece(Integer.parseInt(strTaillePiece));
		element.setTaille(Integer.parseInt(strTaille));
		
		
		raf_fichierCache.close();
	    } catch (IOException e) {
		System.out.println("ioe in remplire : "+e);
	    }
	}
	else {
	    int l = (int) Math.ceil(((double)element.getTaille()) / element.getTaillePiece());
	    masque = new boolean[l];
	    for ( int i=0 ; i<masque.length ; i++)
		masque[i] = true;
	}
	element.setMasque(masque);
	System.out.println("Chargement en mémoire du fichier : " + element.getName() + " [" + element.getPourcentage() + "%]");
	hash.put(key, element) ;
    }    
    
}
