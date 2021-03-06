/* ServeurThread.java
 * Blux
 * 27/02/12
 *
 * Thread(s) 5 : Gère les demandes d'un paire voisin
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
//import java.net.UnknownServiceException;
import java.io.CharConversionException;



public class ServeurThread extends Thread {
    private Socket _socket;
    private Hashtable _hash;
    private boolean _estMisAssertion;

    public ServeurThread(String name, Socket s, Hashtable hash){
	super(name);
	_socket = s;
	_hash = hash;
	_estMisAssertion = false;
	assert _estMisAssertion = true;
    }
    
    //throws java.io.IOException{
    public void run(){
	try{
	    if (_estMisAssertion) 
		System.out.println("Connexion au pair : " + _socket.getInetAddress() + " " + _socket.getPort() );
	    InputStream in = _socket.getInputStream();    // read-close-skip
	    OutputStream out = _socket.getOutputStream(); // write-close-flush
	    
	    boolean fin = false;
	    String question = "", reponse = "", key = "", pieces = "";
	    byte[] b = {0};
	    int res;
	    
	    res = in.read();
	   	
	    
	    // On lit un mot
	    while (res != -1){
		b[0] = (byte)res; 
		question += new String(b);
		// Si on a un espace on vérifie la conformité de l'expression
		if (question.length()-1 == question.indexOf(" ", question.length()-1)){
		    // Si le mot est reconnu
		    if (0 == question.indexOf("interested") || 0 == question.indexOf("getpieces")){
			key = lectureKey(in);
			
			if (0 == question.indexOf("interested")){
			    // Protocole : "interested $key"
			    // "have $key $buffermap" 
			    if (_estMisAssertion) 
				System.out.println("<< " + question + key);
			    actionInterested(key, out);	    
			}
			
			else if (0 == question.indexOf("getpieces")){
			    // Protocole : "getpieces $key [$index1 $index2 $index3]"
			    // "data $key [$index1:$piece1 $index2:$piece2 $index3:$piece3]"
			    pieces = lecturePieces(in);
			    if (_estMisAssertion) 
				System.out.println("<< " + question + key + " " + pieces);
			    try {
				actionGetpiece (key, pieces, out);
			    } catch(IOException e){
				System.out.println("Erreur de lecture du fichier demandé : " + key );
			    }
			}
		    }
		    // Sinon on ignore
		    else{
			question = "";
			throw new CharConversionException("message invalide : " + question );
		    }
		}
		
		res = in.read(); 
	    }
	    in.close(); out.close(); _socket.close();
	}
	catch(SocketException se){
	    System.out.println("Perte de la connexion avec le pair : " + _socket.getInetAddress() + " " + _socket.getPort() );
	    
	} catch(IOException e){
	    System.out.println("ioe exception in ServeurThread: " + e );
	}
    }
    
    private String lectureKey(InputStream in) throws IOException {
	String key = "";
	byte[] b = {0};
	int res;

	res = in.read();
	// On lit un mot
	while (res != -1){
	    b[0] = (byte)res; 
	    key += new String(b);
	    if (key.length()-1 == key.indexOf(" ", key.length()-1))
		res = -1;
	    else
		res = in.read();
	}
	return key.trim();
    }
    
    private String lecturePieces(InputStream in) throws IOException {
	String pieces = "";
	byte[] b = {0};
	int res;
	
	res = in.read();
	// On lit un mot
	while (res != -1){
	    b[0] = (byte)res; 
	    pieces += new String(b);
	    if (pieces.length()-1 == pieces.indexOf("]", pieces.length()-1))
		res = -1;
	    else
		res = in.read();
	}
	return pieces;
    }
    

    private void actionInterested (String key, OutputStream out) throws IOException {
	Object o = _hash.get(key);
	if (o != null){
	    Fichier f = (Fichier)o;
	    if (_estMisAssertion) 
		System.out.println("On me demande mon fichier " + key);
	    String reponse = "have " + key + " ";
	    int i;
	    boolean[] masque = f.getMasque();
	    for ( i=0 ; i<masque.length ; i++){
		if (masque[i])
		    reponse += "1";
		else
		    reponse += "0";
	    }
	    reponse += " ";
	    if (_estMisAssertion) 
		System.out.println(">> " + reponse);
	    out.write(reponse.getBytes());
	    out.flush();
	}
	else
	    System.out.println("Clé inexistante ...");
    }
    
    
    
    private void actionGetpiece (String key, String demande, OutputStream out) throws IOException {
	// Passer directement le string
	String[] tab = demande.split(" ");
	Object o = _hash.get(key);

	if (o != null){
	    Fichier f = (Fichier)o;
	    if (_estMisAssertion) 
		System.out.println("On me demande des pièce de mon fichier " + key);
	    
	    String[] index = ((String)demande.subSequence(demande.indexOf("[")+1,demande.indexOf("]"))).split(" ");
	    String reponse = "data " + key + " [";
	    int i;
	    boolean[] masque = f.getMasque();
	    
	    String reponsePartielle = "";
	    int taille_piece        = f.getTaillePiece();
	    byte lecteur[]          = new byte[taille_piece];
	    int retour;
	    int last_indice         = (int) (f.getTaille()/f.getTaillePiece());
	    int last_taille         = (int)f.getTaille() % f.getTaillePiece();
	    byte [] last_lecteur       = new byte[last_taille]; 



	    for ( i=0 ; i<index.length ; i++){
		// Si on a bien le i-ème index dans notre Buffermap
		int id = Integer.parseInt(index[i]);
		if (masque[id]){
		    // on ouvre le fichier dont on extrait les données
		    RandomAccessFile file = new RandomAccessFile("Download/" + f.getName(), "r");
		    file.seek(id*taille_piece);
		    if (id == last_indice) {
			retour = file.read(last_lecteur);
		    	reponsePartielle += id + ":" + new String(last_lecteur) + " ";
		    } else {
			retour = file.read(lecteur);
			reponsePartielle += id + ":" + new String(lecteur) + " ";
		    }
		}
	    }
	    
	    if (_estMisAssertion) 
		System.out.println(reponsePartielle.length() +":'"+reponsePartielle +"'");
	    reponse += reponsePartielle.substring(0,reponsePartielle.length() -1) + "]";
	    if (_estMisAssertion) 	    
		System.out.println(">> " + reponse);
	    out.write(reponse.getBytes());
	    out.flush();
	}
    }
}