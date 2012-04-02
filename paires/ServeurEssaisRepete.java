/*  ServeurEssaisRepete.java
 *  Blux
 *  02/04/12
 *
 *  Répète tout sur le terminal
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


public class ServeurEssaisRepete extends Thread {
    private Socket _socket;
    
    public ServeurEssaisRepete(String name, Socket s){
	super(name);
	_socket = s;
    }
    
    //throws java.io.IOException{
    public void run(){
	try{
	    System.out.println("Je répète :");
	    InputStream in   = _socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = _socket.getOutputStream(); // close-flush-write
	
	    boolean fin = false;
	    byte[] b = new byte[1000];
	    int res;
	    while (!fin){
		res = in.read(b);
		String string = new String(b);
		// Si end of stream
		if (res == -1){
		    //levé d'exception 
		    throw new SocketException("connection interrompue"); 
		} else 
		    System.out.println( string );
	    }
	    
	    in.close();
	    out.close();
	    _socket.close(); 
	}     
	catch(SocketException se){
	    System.out.println("SocketExc exception dans le run : " + se);
	    System.out.println("Fin de la connection serveur.");
	    System.exit(1);
	}
	catch(IOException e){
	    System.out.println("Et bim : ioe exception dans le run : " + e);
	    System.exit(1);
	}
    }
}

