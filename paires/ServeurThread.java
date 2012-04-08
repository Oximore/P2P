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
    
    public ServeurThread(String name, Socket s, Hashtable hash){
	super(name);
	_socket = s;
	_hash = hash;
    }
    
    //throws java.io.IOException{
    public void run(){
	try{
	    System.out.println("Connexion au pair : " + _socket.getInetAddress() + "/" + _socket.getPort() );
	    InputStream in = _socket.getInputStream(); // aviable-close-read-skip
	    OutputStream out = _socket.getOutputStream(); // close-flush-write
	    
	    /**********************************/
	    

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
		    if (0 == question.indexOf("interested") || 0 == question.indexOf("getPieces")){
			key = lectureKey(in);
			
			// Si c'est une action interessted alors ..	
			if (0 == question.indexOf("interested")){
			    // Protocole : "interested $key"
			    // "have $key $buffermap" (<- séquence de bit !)
			    System.out.println("on a passé le actionInterested");
			    actionInterested(key, out);	    
			}
			
			// Sinon c'est une action getPieces
			else if (0 == question.indexOf("getPieces")){
			    // Protocole : "getpieces $key [$index1 $index2 $index3]"
			    // "data $key [$index1:$piece1 $index2:$piece2 $index3:$piece3]"
			    
			    pieces = lecturePieces(in);

			    try {
				actionGetpiece (key, pieces, out);
			    } catch(IOException e){
				System.out.println("Erreur de lecture du fichier demandé : " + key );
				// envoyer de la merde ..
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
	    System.out.println("Perte de la connexion avec la pair : " + _socket.getInetAddress() + "/" + _socket.getPort() );
	    
	} catch(IOException e){
	    System.out.println("ioe exception in ServeurThread: " + e );
	}
    }
    
    /**********************************/
	//     boolean fin = false;
	//     byte[] b = new byte[1000];
	//     int res;
	//     while (!fin){
	// 	// Peut être : lire, voir si contient "have" si il contient prendre le reste ...
	// 	res = in.read(b);
	// 	String string = new String(b);
	// 	System.out.println("??<< " + string);
	// 	// Si end of stream
	// 	if (false){
	// 	//if (res == -1){
	// 	    //levé d'exception
	// 	    throw new SocketException("connection interrompue");
	// 	} else {
	// 	    System.out.println("on a passé le else");

	// 	    if (-1 != string.indexOf("interested")){
	// 		// Protocole : "interested $key"
	// 		// "have $key $buffermap" (<- séquence de bit !)
	// 		// renvoie le buffmap correspondant si existe
	// 		System.out.println("on a passé le actionInterested");
		    
	// 		actionInterested(string.split(" ")[1], out);

	// 	    } else if (-1 != string.indexOf("getpieces")){
	// 		// Protocole : "getpieces $key [$index1 $index2 $index3]"
	// 		// "data $key [$index1:$piece1 $index2:$piece2 $index3:$piece3]"
	// 		// renvoie les data demandée

	// 		actionGetpiece(string, out);



	// 	    }
	// 	}
	//     }

	//     in.close();
	//     out.close();
	//     _socket.close();
	// }
	// catch(SocketException se){
	//     System.out.println("SocketExc exception dans le run : " + se);
	//     System.out.println("Fin de la connection serveur.");
	//     System.exit(1);
	// }
	// catch(IOException e){
	//     System.out.println("Et bim : ioe exception dans le run : " + e);
	//     System.exit(1);
	// }

    private String lectureKey(InputStream in) {
	String key = "";
	byte[] b = {0};
	int res;
	
	res = in.read();
	// On lit un mot
	while (res != -1){
	    b[0] = (byte)res; 
	    key += new String(b);
	    res = in.read();
	    if (key.length()-1 == key.indexOf(" ", key.length()-1))
		res = -1;
	}
	return key;
    }
    
    private String lecturePieces(InputStream in) {
	String pieces = "";
	byte[] b = {0};
	int res;
	
	res = in.read();
	// On lit un mot
	while (res != -1){
	    b[0] = (byte)res; 
	    pieces += new String(b);
	    res = in.read();
	    if (pieces.length()-1 == pieces.indexOf("]", pieces.length()-1))
		res = -1;
	}
	return pieces;
    }
    

    private void actionInterested (String key, OutputStream out) {
	Object o = _hash.get(key);
	System.out.println("on y est !!");
	if (o != null){
	    Fichier f = (Fichier)o;
	    System.out.println("On me demande mon fichier "+key);
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
	    System.out.println("On me demande des pièce de mon fichier " + key);
	    
	    String[] index = ((String)demande.subSequence(demande.indexOf("[")+1,demande.indexOf("]"))).split(" ");
	    String reponse = "data " + key + "[";
	    int i;
	    boolean[] masque = f.getMasque();
	    

	    String reponsePartielle = "";
	    int taille_piece = f.getTaillePiece();
	    byte lecteur[] = new byte[taille_piece];
	    int retour;
	    for ( i=0 ; i<index.length ; i++){
		// Si on a bien le i-ème index dans notre Buffermap
		int id = Integer.parseInt(index[i]); // *TODO* vérif
		if (masque[id]){
		    // on ouvre le fichier dont on extrait les données
		    RandomAccessFile file = new RandomAccessFile("Download/"+f.getName(), "r");
		    file.seek(id*taille_piece);
		    retour = file.read(lecteur);
		}
		reponsePartielle += id + ":" + lecteur + " ";

	    }
	    
	    reponse += reponsePartielle.trim() + "]";
	    System.out.println(">> " + reponse);
	    out.write(reponse.getBytes());
	    out.flush();
	}
    }
}