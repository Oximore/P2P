import java.net.Socket;

public class ServeurThread extends Thread {

    Socket socket;
    
    public TestThread(String name, Socket s){
	super(name);
	socket = s;
    }
    
    public void run(){
	System.out.println("Et là on fait des trucs !!!");
	socket.close();
    }       
}


