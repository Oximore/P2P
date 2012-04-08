/* TelechargementAutomatique.java
 * Blux
 * 03/04/12
 *
 * Thread 7 : Télécharge un fichier donné
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
import java.net.UnknownServiceException;
import java.io.CharConversionException;



public class TelechargementAutomatique extends ToolsTelechargementThread {
    private String _key;

    public TelechargementAutomatique(String name, FichierConfiguration fichierConf, Hashtable hash, String key){
	super(name, fichierConf, hash);
	_key = key;
    }
    
    public void run(){
	System.out.println("Début du téléchargement automatique du fichier " + _key);
	String question = "", reponse = "";
	boolean [][] tabMasque;
	int i, j, port_tmp;
	String [] info;
	boolean bool;
	
	try {
	    // Quels pairs ont ce fichiers ?
	    question = "getfile " + _key;
	    reponse = demandeCrochet(_fichierConf.getIp(), _fichierConf.getPort(), question, 5 , 1000);
	    if (0 != reponse.indexOf("peers") || !_key.equals(reponse.split(" ")[1]))
		throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);
	
	
	    info = reponse.substring(1+reponse.indexOf("["),reponse.indexOf("]")).split(" ");
	    tabMasque = createTabMasque(info, _key);
	    // Tri des données à récupérées sur quels pairs
	    for ( i=0 ; i<tabMasque.length ; i++) {
		bool = false;
		for ( j=0 ; j<tabMasque[0].length ; j++){
		    if (!bool && tabMasque[i][j])
			bool = true;
		    else
			tabMasque[i][j] = false;
		}
	    }
	    
	
	    for ( i=0 ; i<tabMasque.length ; i++ ){
		port_tmp = Integer.parseInt(info[i].split(":")[1]);
		try {
		    telecharger(info[i].split(":")[0], port_tmp, _key, tabMasque[i]);    
		    // sauve les données ?
		} catch (UnknownServiceException se) {
		    System.out.println("Un serveur pair n'a pas pu être trouvé : " + se);
		} catch (CharConversionException ce) {
		    System.out.println("Réponse d'un pair mal formée : " + ce);
		}
	    }
	    
	    System.out.println("Fichier téléchargé");
	} catch (UnknownServiceException se) {
	    System.out.println("Un serveur n'a pas pu être trouvé : " + se);
	} catch (CharConversionException ce) {
	    System.out.println("Réponse d'un serveur mal formée : " + ce);
	}
    }
}