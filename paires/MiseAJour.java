/*  MiseAJour.java
 *  Blux
 *  27/02/12
 *
 *  Thread 3 : Met à jour régulièrement le Tracker
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


    public MiseAJour(String name, String ip, int port, int port_serveur, int tmp_miseAJour){
	super(name) ;
	_ip   = ip;
	_port = port;
	_tmp  = tmp_miseAJour;
	_port_serveur = port_serveur;
    }
    
    public void run(){
	System.out.println("Mise à jour lancée sur : ip:" + _ip + ", port:" + _port);
	try {
	    Socket socket = new Socket(_ip, _port);
	    //System.out.println("On est passé par là 2 ! (miseAJour)");
	    if (socket == null){
		System.out.println("Serveur not Found in MiseAJour");
		return ;
	    }
	    
	    InputStream in   = socket.getInputStream();  // aviable-close-read-skip
	    OutputStream out = socket.getOutputStream(); // close-flush-write
	    
	    // On se déclare au tracker avec _port_serveur
	    

	    
	    // ok pour l'infini ?
	    while (true) {
		out.write("have 424543".getBytes());
		//out.write("update seed [$key1 $key2] leech [$key10 $key11]".getBytes());
		out.flush();
		try{Thread.sleep(_tmp * 1000);}
		catch(InterruptedException e){}
	    }
	    //	    out.close();
	} catch (IOException ioe) {System.out.println("IOE exception : "+ioe);return ;}
	
	
    }
}

