import java.util.Hashtable;
import java.io.File;
import java.io.RandomAccessFile;

public class GestionFichier{
    
    public static void main(String[] args){
	System.out.println("Gestion Fichier");
	
	Hashtable collection = new Hashtable(); // (< 2* nombre de fichier dans le dossier Download>)
	
	try {
	    File repertoire = new File("Download");
	    boolean fichierExiste = false;
	    
	    if ( repertoire.isDirectory() ) {
		File[] list = repertoire.listFiles();
		if (list != null){
		    for ( int i = 0; i < list.length; i++) {
			// Si ce n'est pas un fichier caché
			if ( list[i].exists() & list[i].getName().indexOf(".") > 0 ) {
			    // Si le fichier caché existe
			    File fichierCache = new File(repertoire, "."+list[i].getName());
			    if (fichierCache.exists()){
				remplire(list[i].getName, collection, fichierCache);
			    }
			    // Si le fichier caché n'existe pas
			    else {
				remplireSansFichier(list[i].getName, collection, fichierCache);
			    }
			}
		    }
		}
		
		// On dispose maintenant de la collection remplie
		
	    }
	    
	} catch (IOException e) { System.out.println("erreur gestion fichier") ; }
    }


    private void remplire(String nom, Hashtable hash){
	File fichier = new File(nom);
	
	RandomAccessFile fichierCache = new RandomAccessFile("."+nom, "r") ;
	
	String key = FileHashSum()
    }
    
    private void remplireSansFichier(String nom, Hashtable hash){

    }
    
    

}
