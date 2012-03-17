/*  ServeurThread.java
 *  Blux
 *  27/02/12
 *
 *  Thread(s) 5 : Gère les demandes d'un paire voisin
 */
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Character;
import java.lang.String;
import java.net.SocketException;


public class ServeurThread extends Thread {

    Socket socket;
    
    public ServeurThread(String name, Socket s){
	super(name);
	socket = s;
    }
    
    //throws java.io.IOException{
    public void run(){
	try{
	    System.out.println("Et là on fait des trucs !!!");
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	
	    boolean fin = false;
	    byte[] b = new byte[1000];
	    int res;
	    while (!fin){
		res = in.read(b);
		String string = new String(b);
		// Si end of strem
		if (res == -1){
		    //levé d'exception 
		    throw new SocketException("connection interrompue"); 
		} else {
		    // annalyse de b
		    
		    if (-1 != string.indexOf("have")){
			String[] tab = string.split(" ");
			System.out.print("Yes i have : ");
			for (int i=0 ; i<tab.length ; i++)
			    System.out.print(tab[i] + ", ");
			System.out.println();
			
			
		    } else if (-1 != string.indexOf("interested")){
			// Protocole : "interested $key"
			// "have $key $buffermap" (<- séquence de bit !)
			// renvoie le buffmap correspondant si existe




		    } else if (-1 != string.indexOf("getpieces")){
			// Protocole : "getpieces $key [$index1 $index2 $index3]"
			// "data $key [$index1:$piece1 $index2:$piece2 $index3:$piece3]"
			// renvoie les data demandée




		    } 
		    /*
		      else if (-1 != string.indexOf("FINI")){
		      fin = true;
		      }
		    */
		}
	    }
	    
	    in.close();
	    out.close();
	    socket.close(); 
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

