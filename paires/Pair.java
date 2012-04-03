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


public class Pair{
    
    public static void main(String[] args){
	System.out.println("Lancement du programme Pair");
	System.out.println("Veuillez patienter quelques instants ...");
	
	FichierConfiguration fichierConf = new FichierConfiguration("configuration.txt");

	File repertoire = new File("Download");
	if ( ! repertoire.isDirectory() ) {
	    System.out.println("Le Dossier Download est introuvable");
	    System.exit(1);
	}
	
	// *TODO* filtrer les fichiers cachés   
	// use :"File[] listFiles(FileFilter filter)" 
	File[] list = repertoire.listFiles();
	Hashtable collection = new Hashtable(2*list.length, (float)0.75);
	
	for ( int i = 0; i < list.length ; i++) {
	    // si ce n'est pas un fichier caché
	    if ( list[i].exists() & list[i].getName().indexOf(".") > 0 ) {
		remplire(list[i], collection, fichierConf.getTaille());
	    }
	}


	// On dispose maintenant de la collection remplie
	sauver(collection);

	ThreadUtilisateur th4 = new ThreadUtilisateur("th4", fichierConf, collection);
	th4.run();
	/*
	  Serveur thread2 = new Serveur("String name", collection, fichierConf.getIp(), fichierConf.getPort(), fichierConf.getTmp(), fichierConf.getNbConnexion());
	  thread2.start();
	
	  System.out.println("Programme utilisateur");    
	  try{Thread.sleep(30000);}
	  catch(InterruptedException ite){}		
	
	  threadUtilisateur(collection,fichierConf);

	  System.out.println("Fin du programme utilisateur");    
	  sauver(collection);*/
	System.out.println("Fin du programme Pair");    

    }
    
    
    static  void sauver(Hashtable hash){
	System.out.println("Sauvegarde des fichiers cachés");    
	Enumeration e  = hash.elements();
	while (e.hasMoreElements())
	    ((Fichier) e.nextElement()).saveValue();
    }

    static private void remplire(File fichier, Hashtable hash, int taillePieces){
	String key = FileHashSum.sha1sum(fichier) ;
	Fichier element = new Fichier(fichier.getName(), key, fichier.length() , taillePieces) ;
	
	File fichierCache = new File("Download/."+fichier.getName()) ;
	
	// Si le fichier caché existe on récupère le masque
	if (fichierCache.exists()){
	    
	    try {
		RandomAccessFile raf_fichierCache = new RandomAccessFile(fichierCache,"r") ;
		String strMasque = raf_fichierCache.readLine() ;
		System.out.println(strMasque);
		
		boolean[] masque = new boolean[strMasque.length()];
		for (int i=0 ; i<strMasque.length() ; i++){
		    if ((strMasque.indexOf('0',i)-i)==0){
			masque[i] = false;
		    } else if ((strMasque.indexOf('1',i)-i)==0){
			masque[i] = true;
		    }else {
			System.out.println("Pas cool in Pair.remplire :"+strMasque.charAt(i) + " i:"+ i );
			System.exit(1);
		    }
		}
		element.setMasque(masque);

		String strTaillePiece = raf_fichierCache.readLine() ;
		System.out.println( strTaillePiece);
		element.setTaillePiece(Integer.parseInt(strTaillePiece));
		
		raf_fichierCache.close();
	    } catch (IOException e) {
		System.out.println("ioe in remplire : "+e);
	    }
	}
	hash.put(key, element) ;
    }    
    
}
