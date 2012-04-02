/*  ServeurEssais.java
 *  Blux
 *  02/04/12
 *
 *  Répète tout ce qu'on lui dit
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.lang.String;
import java.util.Hashtable;


public class ServeurEssais {
    private static String _ip;
    private static int _port;

    public static void main(String[] args){
	
	System.out.println("Début serveur rèpete jaco");
	try {
	    ServerSocket serveur = new ServerSocket(60022); // (60022);
	    serveur.setSoTimeout(1000);
	    _ip = new String("127.0.0.1");
	    _port = new Integer(serveur.getLocalPort());
	    System.out.println("Mon port d'écoute est : " + serveur.getLocalPort());
	    while (true){
		System.out.println(".");
		try{
		    Socket s = serveur.accept();
		    System.out.println("Demande de connexion");
		    // Lancement du Thread de répétition
		    ServeurEssaisRepete st = new ServeurEssaisRepete("" + s.getPort(), s);
		    st.start();
		}
		catch(IOException e){ /*System.out.println("IOE exception in attendre :" + e); */}
	    }
	}
	catch (IOException ioe) {
	    System.err.println("[Cannot initialize Server]\n" + ioe);
	    System.exit(1);
	}
    }
}

