/* RepriseTelechargement.java
 * Blux
 * 03/04/12
 *
 * Thread 6 : Gère les reprises de téléchargement, i.e. reprends les téléchargements en cours lors de la dernière session
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


public class RepriseTelechargement extends Thread {
    private FichierConfiguration _fichierConf;
    private Hashtable _hash;

    public RepriseTelechargement(String name, FichierConfiguration fichierConf, Hashtable hash){
	super(name);
	_fichierConf = fichierConf;
	_hash = hash;
    }
    
    public void run(){
	String [] tabKey;
	String keys = "";
	Fichier file = null;
	Enumeration e  = _hash.elements();
	TelechargementAutomatique ta;
	int nbConnexionRestantes = _fichierConf.getNbConnexion(); 
	int i,j;
	while (e.hasMoreElements()){
	    file = (Fichier) e.nextElement();
	    if (file.isIncomplet())
		keys += file.getKey();
	}
	
	tabKey = keys.split(" ");
	for (i = 0 ; i<tabKey.length ; i++){
	    // *TODO* Ne pas dépasser le nombre de connexion max
	    /*
	      while (nbConnexionRestantes <= 0 ){
	      nbConnexionRestantes = _fichierConf;
	      for (j=0 ; j<i ;j++){
	      if ()
	      }
	      } // */
	    	    
	    ta = new TelechargementAutomatique(file.getName(), _fichierConf, _hash, tabKey[i]);
	    ta.start();
	    // nbConnexionRestantes --;
	}
    }
}