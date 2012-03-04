import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.Character;
import java.lang.String;

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
	    byte[] b = new byte[500];
	    int res;
	    while (!fin){
		res = in.read(b);
		String string = new String(b);
		if (res == -1){
		    System.out.println("***WTF***");
		    try{Thread.sleep(1500);}
		    catch(InterruptedException e){}
		}
		else {
		    // annalyse de b
		    if (-1 != string.indexOf("have")){
			String[] tab = string.split(" ");
			System.out.println("Yes i have : "+tab[2]+", "+tab[1]);
		    }
			else if (-1 != string.indexOf("FINI")){
			fin = true;
		    }
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
    
    //    private String readSentence(InputStream in){    }


}

