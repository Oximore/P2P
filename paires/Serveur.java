/*  Serveur.java
 *  Blux
 *  21/02/12
 *
 *  Socket serveur
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.lang.String;

class Serveur{
    
    public static void main(String[] args) {
	System.out.println("Début serveur");
	try {
	    ServerSocket serveur = new ServerSocket(55555);
	    serveur.setSoTimeout(1000);

	    // Lancer Thread 3 now ?

	    while (true){
		attendre(serveur);
		System.out.println(".");
	    }
	}
	catch (IOException ioe) {
	    System.err.println("[Cannot initialize Server]\n" + ioe);
	    System.exit(1);
	}
    }



    public static int attendre(ServerSocket serv){
	try{
	    Socket s = serv.accept(); // s.close() quelque part
	    System.out.println("trucmachin");
	    ServeurThread st = new ServeurThread("" + s.getPort(), s);
	    st.start();
	    return 0;
	}
	catch(IOException e){ 
	    //System.out.println("IOE exception in attendre :" + e);
	    return 1;
	}
    }

}

