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
	    System.out.println("Et là on fait des trucs !!!");
	    InputStream in = _socket.getInputStream(); // aviable-close-read-skip
	    OutputStream out = _socket.getOutputStream(); // close-flush-write

	    boolean fin = false;
	    byte[] b = new byte[1000];
	    int res;
	    while (!fin){
		// Peut être : lire, voir si contient "have" si il contient prendre le reste ...
		res = in.read(b);
		String string = new String(b);
		// Si end of stream
		if (res == -1){
		    //levé d'exception
		    throw new SocketException("connection interrompue");
		} else {

		    if (-1 != string.indexOf("interested")){
			// Protocole : "interested $key"
			// "have $key $buffermap" (<- séquence de bit !)
			// renvoie le buffmap correspondant si existe

			actionInterested(string.split(" "), out);

		    } else if (-1 != string.indexOf("getpieces")){
			// Protocole : "getpieces $key [$index1 $index2 $index3]"
			// "data $key [$index1:$piece1 $index2:$piece2 $index3:$piece3]"
			// renvoie les data demandée

			actionGetpiece(string, out);



		    }
		}
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
    

    private void actionInterested (String tab[], OutputStream out) throws IOException {
	Object o = _hash.get(tab[1]);

	if (o != null){
	    Fichier f = (Fichier)o;
	    System.out.println("On me demande mon fichier "+tab[1]);
	    String reponse = "have " + tab[1] + " ";
	    int i;
	    boolean[] masque = f.getMasque();
	    for ( i=0 ; i<masque.length ; i++){
		if (masque[i])
		    reponse += "1";
		else
		    reponse += "0";
	    }
	    out.write(reponse.getBytes());
	    out.flush();
	}
    }
    
    
    private void actionGetpiece (String demande, OutputStream out) throws IOException {
	// Passer directement le string
	String[] tab = demande.split(" ");
	Object o = _hash.get(tab[1]);

	if (o != null){
	    Fichier f = (Fichier)o;
	    System.out.println("On me demande des pièce de mon fichier "+tab[1]);

	    String[] index = ((String)demande.subSequence(demande.indexOf("[")+1,demande.indexOf("]"))).split(" ");
	    String reponse = "data " + tab[1] + "[";
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
	    out.write(reponse.getBytes());
	    out.flush();
	}
    }
}