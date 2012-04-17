/*  MiseAJour.java
 *  Blux
 *  27/02/12
 *
 *  Thread 3 : Déclare le pair au tracker et met à jour régulièrement le Tracker
 */
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Character;
import java.lang.String;
import java.util.Hashtable;
import java.util.*;

public class MiseAJour extends Thread {

    private String _ip;
    private int _port;
    private int _tmp;
    private int _port_serveur;
    private Hashtable _hash;

    public MiseAJour(String name, Hashtable hash, String ip, int port, int port_serveur, int tmp_miseAJour){
	super(name) ;
	_hash = hash;
	_ip   = ip;
	_port = port;
	_tmp  = tmp_miseAJour;
	_port_serveur = port_serveur;
    }
    
    public void run(){
	// On se déclare au tracker avec _port_serveur
	System.out.println("Début Mise à Jour");
	try {
	    Socket socket = new Socket(_ip, _port);
	    if (socket == null){
		System.out.println("Serveur not Found in MiseAJour");
		return ;
	    }
	    System.out.println("Connexion établie in MiseAJour");
	
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	    
	    String listeFichiers = fichiersPresents(true);
	    out.write(("announce listen " + _port_serveur + " " + listeFichiers).getBytes());
	    out.flush();
	    waitOk(in);

	    // On ferme la connexion
	    out.close(); in.close(); socket.close();
	    	    
	    while (true) {
		try{Thread.sleep(_tmp * 60 * 1000);}
		catch(InterruptedException ite){}		
		
		// Création d'une nouvelle connexion au serveur
		listeFichiers = fichiersPresents(false);
		socket = new Socket(_ip, _port);
		if (socket == null){
		    System.out.println("Serveur not Found in MiseAJour");
		    return ;}
		in   = socket.getInputStream();
		out = socket.getOutputStream();
		
		out.write(("update " + listeFichiers).getBytes());
		out.flush();
		waitOk(in);
		
		out.close(); in.close(); socket.close();
	    }
	} catch (IOException ioe) {
	    // modifier ce truc *TODO*
	    System.out.println("IOE exception in MiseAJour : "+ioe);
	    return ;
	}
	//	System.out.println("Fin Mise à Jour");
    }

    // bool == true => afficher tous les éléments
    private String fichiersPresents(boolean bool){
	Fichier file;
	Enumeration e  = _hash.elements();
	
	String seed    = new String("seed [");
	String leech   = new String("leech [");    
	String enCours = new String();
	while (e.hasMoreElements()){
	    file = (Fichier) e.nextElement();
	    enCours = "";
	    if (bool)
		enCours += file.getName() + " "
		    + file.getTaille() + " "
		    + file.getTaillePiece() + " ";
	    enCours += 
		file.getKey() + " ";
	    if (file.isComplet())
		seed += enCours;
	    else leech += enCours;
	}
	
	return seed.trim() + "] " + leech.trim() + "]";
    }
    
    
        
    private void waitOk (InputStream in) throws IOException{
	boolean ok = false;
	int ret = (int) in.read();
	while (!ok){
	    while ('o' == ret)
		ret = (int) in.read();
	    ret = in.read();
	    if (ret == 'k')
		ok = true;
	}
	//try{Thread.sleep(1000);}
	//catch(InterruptedException ite){}		
    }
}

