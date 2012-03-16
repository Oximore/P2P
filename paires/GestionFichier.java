/*  GestionFichier.java
 *  Blux
 *  15/03/12
 *
 *  Gère les data Fichier et leur corespondance avec les fichiers cachés
 */
import java.util.Hashtable;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.*;

public class GestionFichier{
    
    public static void main(String[] args){
	System.out.println("Gestion Fichier");
	
	Hashtable collection = new Hashtable(); // (< 2* nombre de fichier dans le dossier Download>)
	
	File repertoire = new File("Download");
	boolean fichierExiste = false;
	    
	if ( repertoire.isDirectory() ) {
	    File[] list = repertoire.listFiles();
	    if (list != null){  // nécessaire ? (avec le for en suivant ?)
		for ( int i = 0; i < list.length; i++) {
		    // Si ce n'est pas un fichier caché
		    if ( list[i].exists() & list[i].getName().indexOf(".") > 0 ) {
			remplire(list[i], collection);
		    }
		}
	    }   
	}
	// On dispose maintenant de la collection remplie
	// System.out.println(collection.toString());

	Enumeration e  = collection.elements();
	while (e.hasMoreElements())
	    ((Fichier) e.nextElement()).saveValue();
	System.out.println("Fin Gestion Fichier");    
    }


    static private void remplire(File fichier, Hashtable hash){
	String key = FileHashSum.sha1sum(fichier) ;
	// Changer le 10 en valeur du fichier de conf
	Fichier element = new Fichier(fichier.getName(), key, fichier.length() , 10) ;
	
	File fichierCache = new File("Download/."+fichier.getName()) ;
	
	// Si le fichier caché existe on récupère le masque
	if (fichierCache.exists()){
	    
	    try {
		RandomAccessFile raf_fichierCache = new RandomAccessFile(fichierCache,"r") ;
		String strMasque = raf_fichierCache.readLine() ;
		System.out.println(strMasque);
		raf_fichierCache.close();
		
		
		boolean[] masque = new boolean[strMasque.length()];
		for (int i=1 ; i<strMasque.length() ; i += 2){
		    if (strMasque.indexOf("0",i) == 0){
			masque[i] = false;
		    } else if (strMasque.indexOf("1",i) == 0){
			masque[i] = true;
		    }else {
			System.out.println("Pas cool :"+strMasque.charAt(i) );
		    }
		}
		element.setMasque(masque);
		
	    } catch ( IOException e) {}
	    
	    
	    //System.out.println(fichierCache.getName()+" : "+fichierCache.length());
	}
	
	hash.put(key, element) ;
    }
    
    
}
