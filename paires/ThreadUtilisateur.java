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
	    int ret = -1, res, ret2;
	    byte[] b = new byte[1000];
	    while (ret != 0){
		Scanner scan = new Scanner(System.in);
		System.out.println("Recherche d'un nom de fichier(1), d'une clé(2), quitter(0) :");
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

			System.out.println("Voici les fichiers disponibles, taper le numéro pour commencer le téléchargement:");

			for (int i=0; i<co.getTab().length ; i++)
			    System.out.println(i + ":\t" + co.getTab()[i]);
			System.out.println(co.getTab().length + 1 +":\tAnnuler");
			
			while (!scan.hasNextInt())
			    str = scan.next();
			ret2 = scan.nextInt();
			
			if (0 <= ret2 && ret2 <= co.getTab().length){
			    // lancer le téléchargement du fichier i
			    
			    //trouver les pairs dispo
			    
			} else if (ret2 == co.getTab().length+1){
			    // quitter
						    
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
    
    // *TODO* check ici si le fichier existe : @key -> @Fichier
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
	    byte[] b = new byte[1000]; // *TODO* Pas assez grand je le crains ...
	    String question = new String("getpieces " + key + " [");
	    for (int i=0 ; i<num.length ; i++)
		question += num[i] + " ";
	    out.write((question.trim() + "]").getBytes());
	    out.flush();
	    
	    while (ret != -1){
		res = in.read(b);
		String string = new String(b);
		// Si end of stream
		if (res == -1){
		    throw new SocketException("connection au pair interrompue"); 
		} else {
		    if (-1 != string.indexOf("data")){
			System.out.println("<< " + string);
			String key_tmp = string.split(" ")[1];
			if (key != null)
			    enregistre((Fichier)_hash.get(key_tmp), string.substring(1+string.indexOf("["),string.indexOf("]")).split(" "));
			ret = -1;
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
    
    
    public void enregistre(Fichier f, String [] infos){
	System.out.println("fonction enregistre");
	String [][] tab = new String[infos.length][2];
	int indice;
	boolean [] masque  = f.getMasque();;
	for (int i=0 ; i<infos.length ; i++)
	    tab[i] = infos[i].split(":"); 
	
	if (f != null){
	    try{
		RandomAccessFile file = new RandomAccessFile("Download/"+f.getName(), "rw");
		for (int i=0 ; i<tab.length ; i++){
		    indice = Integer.parseInt(tab[i][1]);
		    file.seek(indice*f.getTaillePiece());
		    file.writeBytes(tab[i][2]);
		    masque[indice] = true;
		}
		file.close();
	    } catch (IOException ioe) { System.out.println("ioe in enregistrer" + ioe); }
	    
	} else { System.out.println("premier para de enregistrer foireux"); }
	f.setMasque(masque);
    }
}