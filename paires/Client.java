/*  Client.java
 *  Blux
 *  21/02/12
 *
 *  Socket client
 */

//package noeud;

class Client{
 
    public boolean envoieMessage(String host, int port, String sender, String receiver) {
	Socket smtpPipe;
	
	try {
	    smtpPipe = new Socket(host, port);
	    if (smtpPipe == null)
		return false;

	    InputStream in = socket.getInputStream();
	    OutputStream out = socket.getOutputStream();

	    //L'utilisation conjointe des méthodes read() (qui renvoie un entier) et write(byte) couvriront alors tous vos besoins. 



	} 
	catch (IOException ioe) { 
	    return false; 
	}
	return true;
    }
    
}