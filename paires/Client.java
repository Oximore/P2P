/*  Client.java
 *  Blux
 *  21/02/12
 *
 *  Socket client
 */

//package noeud;

	    //    public boolean envoieMessage(String host, int port, String sender, String receiver) {
        //L'utilisation conjointe des méthodes read() (qui renvoie un entier) et write(byte) couvriront alors tous vos besoins. 

import java.lang.InterruptedException;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;


class Client{
    public static void main(String[] args) {
	Socket socket;
	System.out.println("Début client");
	try {
	    socket = new Socket("127.0.0.1", 55555);
	    if (socket == null){
		System.out.println("Serveur not Found");
		return ;
	    }
	    
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	    
	    out.write("blablabla".getBytes());
	    out.flush();

	    /*
	      int i = 1;
	      while (i++ < 10){
	      System.out.println(i);
	      try{
	      Thread.sleep(1000);
	      }
	      catch(InterruptedException e){
	      }
	      }
	      System.out.println("Fin client");
	    */
	    
	    in.close();
	    out.close();
	} 
	catch (IOException ioe) { 
	    System.out.println("IO exception : " + ioe );
	    return ; 
	}
	return ;
    }
    
}