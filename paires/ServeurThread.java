import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Character;

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
	
	    boolean pasfini = true;
	    int deja = 0;
	    int res;
	    while (pasfini){
		// read(b)  where byte[] b
		res = in.read();
		if (res == -1){
		    if (deja == 1)
			pasfini = false;
		}
		else {
		    System.out.print((char)res);
		    deja = 1;
		}
	    }

	    in.close();
	    out.close();
	//socket.close(); apparement pas là
	}     
	catch(IOException e){
	    System.out.println("Et bim : ioe exception dans le run : " + e);
	    System.exit(1);
	}  
    }
}

