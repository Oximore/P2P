/*  Client.java
 *  Blux
 *  21/02/12
 *
 *  Socket client
 */
import java.lang.InterruptedException;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;


class Client {
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
	    
	    int i=0;
	    while (i++<5) {
		out.write("have 42 43".getBytes());
		out.flush();
		try{Thread.sleep(1500);}
		catch(InterruptedException e){}
	    }
	    out.write("FINI".getBytes());
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
	System.out.println("Fin client");
	} 
	catch (IOException ioe) { 
	  
	}
	return ;
    }
    
}