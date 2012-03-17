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

public class Pair{
    
    public static void main(String[] args){
	System.out.println("Lancement du programme Pair");
	
	FichierConfiguration fichierConf = new FichierConfiguration("fichier.txt");

	File repertoire = new File("Download");
	if ( ! repertoire.isDirectory() ) {
	    System.out.println("Le Dossier Download est introuvable");
	    System.exit(1);
	}
	
	// *TODO* filtrer les fichiers cachés   
	// use :"File[] listFiles(FileFilter filter)" 
	File[] list = repertoire.listFiles();
	Hashtable collection = new Hashtable(2*list.length, (float)0.75);
	
	for ( int i = 0; i < list.length; i++) {
	    // Si ce n'est pas un fichier caché
	    if ( list[i].exists() & list[i].getName().indexOf(".") > 0 ) {
		remplire(list[i], collection, fichierConf.getTaille());
	    }
	}

	// On dispose maintenant de la collection remplie

	System.out.println("Sauvegarde des fichiers cachés");    
	Enumeration e  = collection.elements();
	while (e.hasMoreElements())
	    ((Fichier) e.nextElement()).saveValue();


	//	Serveur thread2 = new Serveur("String name", collection, fichierConf.getIp(), fichierConf.getPort(), fichierConf.getTmp(), fichierConf.getNbConnexion());

	System.out.println("Fin du programme Pair");    

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
		for (int i=1 ; i<strMasque.length() ; i += 2){
		    if (strMasque.indexOf("0",i) == 0){
			masque[i] = false;
		    } else if (strMasque.indexOf("1",i) == 0){
			masque[i] = true;
		    }else {
			System.out.println("Pas cool in Pair.remplire :"+strMasque.charAt(i) );
			System.exit(1);
		    }
		}
		element.setMasque(masque);

		String strTaillePiece = raf_fichierCache.readLine() ;
		element.setTaillePiece(Integer.parseInt(strTaillePiece));
		
		raf_fichierCache.close();
	    } catch (IOException e) {
		System.out.println("ioe in remplire : "+e);
	    }
	    
	}
	
	hash.put(key, element) ;
    }    
    
}
