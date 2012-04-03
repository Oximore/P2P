/* ThreadUtilisateur.java
 * Blux
 * 03/04/12
 *
 * Thread 4 : Gère les demandes de l'utilisateur du service
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


public class ThreadUtilisateur extends Thread {
    private FichierConfiguration _fichierConf;
    private Hashtable _hash;

    public ThreadUtilisateur(String name, FichierConfiguration fichierConf, Hashtable hash){
	super(name);
	_fichierConf = fichierConf;
	_hash = hash;
    }
    
    public void run(){
	try {
	    Socket socket = new Socket(_fichierConf.getIp(), _fichierConf.getPort());
	    if (socket == null){
	    	System.out.println("Serveur not Found in MiseAJour");
	    	return ;
	    }
	    System.out.println("Bienvenue !");
	    
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	    
	    String str;
	    int ret = 0, res;
	    byte[] b = new byte[1000];
	    while (ret != 101){
		Scanner scan = new Scanner(System.in);
		System.out.println("Recherche d'un nom de fichier(1) ou d'une clé(2) :");
		while (!scan.hasNextInt())
		    str = scan.next();
		ret = scan.nextInt();
		if (ret == 1){
		    System.out.println("tapez le nom du fichier recherché :");
		    str = scan.next();
		    out.write(("look ["
			       + "filename=\"" + str + "\""
			       +"]").getBytes());
		    out.flush();
		}
		else if (ret == 2){
		    System.out.println("tapez la clé du chichier recherché :");
		    str = scan.next();
		    out.write(("look ["
			       + "key=\"" + str + "\""
			       +"]").getBytes());
		    out.flush();
		}
		
		
		
		res = in.read(b);
		String string = new String(b);
		// Si end of stream
		if (res == -1){
		    // throw new SocketException("connection interrompue");
		} else {
		    if (-1 != string.indexOf("list")){
			System.out.println("<< " + string);
			
			Collecte co = new Collecte(string);
			InfoPair tab[] = co.getTab();

			System.out.println("Voici les fichiers disponibles, taper le numéro pour commencer le téléchargement :");

			for (int i=0; i<co.getTab().length ; i++){
			    System.out.println(i + ":\t" + co.getTab()[i]);
			    System.out.println(co.getTab().length + 1 +":\tAnnuler");
			    
			    while (!scan.hasNextInt())
				str = scan.next();
			    ret = scan.nextInt();
			
			    if (0 <= ret && ret <= co.getTab().length){
				// lancer le téléchargement du fichier i
				
				
			    } else if (ret == co.getTab().length+1){
				// quitter
			    
			    }
			}
		    }		
		}
		
	    }		
	    out.close();
	    in.close();
	    socket.close();
	} catch (IOException ioe) {
	    System.out.println("IOE exception in Pair : "+ioe);
	    return ;
	}
	System.out.println("Bye bye !");
    }
    
    
    public void telecharger(String ip, int port, String key, int num[]){
	// établire le lien avec l'autre pair
	try {
	    Socket socket = new Socket(ip,port);
	    if (socket == null){
		System.out.println("Serveur not Found in MiseAJour");
		return ;
	    }
	    System.out.println("Connexion au pair " + ip + port);
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	    
	    String str;
	    int ret = 0, res;
	    byte[] b = new byte[1000];
	    
	    for (int i=0 ; i<num.length ; i++){
		
		out.write(("getpieces " + key + " [").getBytes());
		out.flush();
		    
		res = in.read(b);
		String string = new String(b);
		// Si end of stream
		if (res == -1){
		    throw new SocketException("connection au pair interrompue"); 
		} else {
		    if (-1 != string.indexOf("list")){
			System.out.println("<< " + string);
			
		    }		    
		}
	    }
		
	    out.close();
	    in.close();
	    socket.close();
	} catch (IOException ioe) {
	    System.out.println("IOE exception in Pair : "+ioe);
	    return ;
	}
	System.out.println("Déco d'un pair");
    }


}