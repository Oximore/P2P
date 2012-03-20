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
	try {
	    Socket socket = new Socket(_ip, _port);
	    if (socket == null){
		System.out.println("Serveur not Found in MiseAJour");
		return ;
	    }
	    
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	    
	    String listeFichiers = new String();
	    Enumeration e  = _hash.elements();
	    while (e.hasMoreElements())
		listeFichiers += 
		    e.getName() + " "
		    + e.getTaille() + " "
		    + e.getTaillePiece() + " "
		    + e.getKey() + " ";

	    out.write("announce listen " + _port_serveur + " have ["
		      + listeFichiers.trim() // pour enlevé l'espace en trop à la fin
		      + "]");
	    waitOk(in);

	    // On ferme la connexion
	    out.close();
	    in.close();
	    socket.close();
	    
	    while (true) {
		try{Thread.sleep(_tmp * 1000);}
		catch(InterruptedException e){}		
		
		// Création d'une nouvelle connexion au serveur
		Socket socket = new Socket(_ip, _port);
		if (socket == null){
		    System.out.println("Serveur not Found in MiseAJour");
		    return ;}
		InputStream in   = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
				
		String seed  = new String();
		String leech = new String();    
		
		// hash
		Enumeration e  = _hash.elements();
		while (e.hasMoreElements()){
		
		    // *TODO*
		    // remplit seed or leech
		    // avec : e.getKey() + " ";
		}
		
		
		out.write(("update seed ["
			  + seed.trim() 
			  + "] leech ["
			  + leech.trim()
			   + "]").getBytes());
		out.flush();
		waitOk(in);
		
		out.close();
		in.close();
		socket.close();
	    }
	} catch (IOException ioe) {System.out.println("IOE exception : "+ioe);return ;}
    }
    
    private void waitOk(InputStream in){
	// *TODO*
    
    }
}

