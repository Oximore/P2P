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
	    String reponse = "";
	    while (!fin){
		res = in.read(b);
		String question = new String(b);
		// Si end of stream
		if (res == -1){
		    //levé d'exception 
		    throw new SocketException("connection interrompue"); 
		} else { 
		    System.out.println("<< " + question );
		    
		    if (0 == question.indexOf("look") )
			reponse = "list [fichier.data 63 8 keykeykeykeykey fichier2.data 63 8 yekyekyekyekyek]";
		    if (0 == question.indexOf("getfile") )
			reponse = "peers keykeykeykeykey [127.0.0.1:60022 127.0.0.1:60022]";
		    if (0 == question.indexOf("interested") )
			reponse = "have keykeykeykeykey 1101 ";
		    if (0 == question.indexOf("getpieces") )
			reponse = "data keykeykeykeykey  [0:aaaaaaaa 1:bbbbbbbb 3:cccccccc]";
		    
		    out.write(reponse.getBytes());
		    out.flush();
		    System.out.println(">> " +  reponse);

		}
	    }
	    
	    in.close(); out.close(); _socket.close(); 
	}     
	catch(SocketException se){
	    System.out.println("SocketExc exception dans le run : " + se);
	    System.out.println("Fin de la connection serveur.");
	}
	catch(IOException e){
	    System.out.println("Et bim : ioe exception dans le run : " + e);
	}
    }
}

