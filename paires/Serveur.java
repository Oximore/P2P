/* Serveur.java
 * Blux
 * 21/02/12
 *
 * Thread 2 : Socket serveur
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.lang.String;
import java.util.Hashtable;


public class Serveur extends Thread {
    
    private Hashtable _hash;
    private FichierConfiguration _fichierConf;
    private int _localPort;
    
    // private int _connexions_max;
    // private int _tmp_refresh;
    // private String _ip_tracker;
    // private int _port_tracker;

    // @localPort : 0 pour ne pas choisir
    public Serveur(String name, Hashtable hash, FichierConfiguration fichierConf, int localPort) { //String ip, int port, int tmp_refresh, int connexions_max){
	super(name);
	_hash = hash;
	_fichierConf = fichierConf;
	_localPort = localPort;
	// _connexions_max = connexions_max;
	// _tmp_refresh = tmp_refresh;
	// _ip_tracker = ip;
	// _port_tracker = port;
    }

    
    public void run() {
	System.out.println("Début serveur");
	try {
	    // Sélectionne un port libre ? entre "60000" et "60025"
	    ServerSocket serveur = new ServerSocket(_localPort); // (60022);
	    serveur.setSoTimeout(1000);
	    System.out.println("Mon port d'écoute est : " + serveur.getLocalPort());

	    // Lancement du Thread 3
	    MiseAJour maj = new MiseAJour("Thread 3", _hash, _fichierConf.getIp(), _fichierConf.getPort(), serveur.getLocalPort(), _fichierConf.getTmp());
	    maj.start();

	    while (true){
		//System.out.println(".");
		attendre(serveur);
	    }
	}
	catch (IOException ioe) {
	    System.err.println("[Cannot initialize Server]\n" + ioe);
	    System.exit(1);
	}
    }


    public int attendre(ServerSocket serv){
	try{
	    Socket s = serv.accept();
	    System.out.println("Demande de connexion");
	    // Lancement du Thread 5
	    ServeurThread st = new ServeurThread("" + s.getPort(), s, _hash);
	    st.start();
	    return 0;
	}
	catch(IOException e){ 
	    //System.out.println("IOE exception in attendre :" + e);
	    return 1;
	}
    }

}

