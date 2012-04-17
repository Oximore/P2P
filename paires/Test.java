/*  Test.java
 *  Blux
 *  14/02/12
 *
 *  Pour tester
 */
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Character;
import java.lang.String;
import java.net.SocketException;
import java.util.Hashtable;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;
import java.net.ServerSocket;


public class Test{
    
    public static void main(String[] args){
	System.out.println("Essais");
	try {
	    // Sélectionne un port libre ? entre "60000" et "60025"
	    ServerSocket serveur = new ServerSocket(55555); // (60022);
	    serveur.setSoTimeout(1000);

	    while (true){
		System.out.println(".");
		attendre(serveur);
	    }
	}
	catch (IOException ioe) {
	    System.err.println("[Cannot initialize Server]\n" + ioe);
	    System.exit(1);
	}
    }
    

    public static int attendre(ServerSocket serv){
	try{
	    Socket s = serv.accept();
	    System.out.println("Demande de connexion");
	    
	    InputStream in   = s.getInputStream();  // aviable-close-read-skip
	    OutputStream out = s.getOutputStream(); // close-flush-write
	    
	    byte[] b = new byte[1000];
	    int res;
	    String string;
	    while (true){
		res = in.read(b);
		string = new String(b);
		// Si end of stream
		if (res == -1){
		    //levé d'exception 
		    throw new SocketException("connection interrompue"); 
		} else {
		    System.out.println(string);
		}
	    }
	}
	catch(IOException e){ 
	    //System.out.println("IOE exception in attendre :" + e);
	    return 1;
	}
    }
    
}


