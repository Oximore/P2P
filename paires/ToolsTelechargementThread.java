/* ToolsTelechargementThread.java
 * Blux
 * 07/04/12
 *
 * Classe servant de boite à outils 
 */
import java.util.Hashtable;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;
import java.net.SocketException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.UnknownServiceException;
import java.io.CharConversionException;



public class ToolsTelechargementThread extends Thread {
    // Quelle portée ?
    static FichierConfiguration _fichierConf;
    Hashtable<String,Fichier> _hash;

    public ToolsTelechargementThread(String name, FichierConfiguration fichierConf, Hashtable<String,Fichier> hash){
	super(name);
	_fichierConf = fichierConf;
	_hash        = hash;
    }
    
    // @info : couples Ip/Port 
    // @key  : clé du fichier souhaité
    static boolean [][] createTabMasque(String [] info, String key) throws UnknownServiceException, CharConversionException {
	int i, j, k, port;
	String ip, masque, question, reponse = "";
	boolean [][] tabMasque = null;
	
	
	for ( i=0 ; i<info.length ; i++){ 
	    ip   = info[i].split(":")[0];
	    port = Integer.parseInt(info[i].split(":")[1]);
	    
	    question =  "interested " + key + " ";
	    reponse = demandeMot(ip, port, question, 5, 1000, 3);
	    
	    if (0!= reponse.indexOf("have") || !key.equals(reponse.split(" ")[1]))
		throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);
	    	    
	    masque = reponse.split(" ")[2];
	    //System.out.println("Masque :" + masque +"!");
	    if (i == 0) {
		tabMasque  = new boolean[info.length][masque.length()];
		// initialisation inutile ???
		for ( j=0 ; j<tabMasque.length ; j++)
		    for ( k=0 ; k<tabMasque[0].length ; k++)
			tabMasque[j][k] = false;
	    }
	    //	    for ( j=0 ; j<masque.length()-1 ; j++){
	    for ( j=0 ; j<masque.length() ; j++){
		if (j == masque.indexOf("0", j))
		    tabMasque[i][j] = false;
		else if (j == masque.indexOf("1", j))
		    tabMasque[i][j] = true;
		else 
		    throw new CharConversionException("masque mal formé");
	    }
	    // for ( j=0 ; j<tabMasque[0].length ; j++)
	    // 	if (tabMasque[i][j])
	    // 	    System.out.print(1);
	    // 	else System.out.print(0);
	    // System.out.println("!!!!");
	}
	return tabMasque;
    }
	
	    
    // *TODO* check ici si le fichier existe : @key -> @Fichier
    // *TODO* télécharger 10 par 10 plutôt :x
    void telecharger(String ip, int port, String key, boolean [] masque) throws UnknownServiceException, CharConversionException {
	int i = 0 , max;
	String reponse = "", question = "", key_tmp = "";
	String [][] reponse_partielle;
	boolean bool;
	
	while (i < masque.length) {	
	    reponse = "";
	    question = new String("getpieces " + key + " [");
	    max = 150;
	    bool = false;
	    
	    for (int j = i; j<masque.length & 0<max ; j++ ){
		i++;
		max--;
		if (masque[j]) {
		    question += j + " ";
		    bool = true;
		}
	    }
	    question = question.trim() + "]";
	    
	    if (bool){

		// static String demanderPiece(String ip, int port, String question) throws IOException {
		/** /
		reponse = demandeCrochet(ip, port, question, 5 , 1000);
		String key_tmp = reponse.split(" ")[1];
		if (0 != reponse.indexOf("data") || key == null || !key.equals(key_tmp))
		    throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);
		
		reponse_partielle = reponse.substring(1+reponse.indexOf("["),reponse.indexOf("]"));
		String [][] infos = ;
		enregistre((Fichier)_hash.get(key_tmp), reponse.substring(1+reponse.indexOf("["),reponse.indexOf("]")).split(" "));
		// */

		reponse_partielle =  demanderPiece(ip, port, question, 5, 1000);
		enregistre((Fichier)_hash.get(key_tmp), reponse_partielle);
		

		// *TODO* à virer ?
		try{Thread.sleep(100);}
		catch(InterruptedException ite){}		
	    }
	}
    }
    
    //longeur = _fichierConf.getTaille();
    static void enregistre(Fichier f, String [][] infos){ // String [][] infos){
	System.out.println("fonction enregistre");
	//String [][] tab = new String[infos.length][2];
	int indice;
	boolean [] masque  = f.getMasque();
	 
	//	for (int i=0 ; i<infos.length ; i++)
	//   infos[i] = infos[i].split(":"); 
	// TODO améliorée : le premier ':' !

	if (f != null){
	    // *TODO* ressérer le try pour enregistrer le masque en partie ?
	    try{
		RandomAccessFile file = new RandomAccessFile("Download/"+f.getName(), "rw");
		for (int i=0 ; i<infos[0].length ; i++){
		    //System.out.println("indice : " + infos[i][0]);
		    indice = Integer.parseInt(infos[0][i]);
		    file.seek(indice*f.getTaillePiece());
		    file.writeBytes(infos[0][i]);
		    //file.write(infos[i][1].getBytes());
		    masque[indice] = true;
		}
		file.close();
	    } catch (IOException ioe) { System.out.println("ioe in enregistrer" + ioe); }

	} else { System.out.println("premier para de enregistrer foireux"); }
	f.setMasque(masque);
    }

    // @ tmpAttente en ms
    static String demandeCrochet(String ip, int port, String question, int nbIteration, int tmpAttente) throws UnknownServiceException {
	return demandeIntermediare(ip, port, question, true, 0, nbIteration, tmpAttente);
    }

    static String demandeMot(String ip, int port, String question, int nbIteration, int tmpAttente, int nbMots) throws UnknownServiceException {
	return demandeIntermediare(ip, port, question, false, nbMots, nbIteration, tmpAttente);
    }
    
    
    static String demandeIntermediare(String ip, int port, String question, boolean attCrochets, int nombreMot, int nbIteration, int tmpAttente ) throws UnknownServiceException {
	String reponse = "";
	int tmp = 0;
	while (tmp < nbIteration){
	    try { reponse = demanderQuestion(ip, port, question, attCrochets, nombreMot);
		tmp = nbIteration*2; 
	    } catch (IOException ioe) { 
		tmp++; 
		if (tmpAttente>0)
		    try{Thread.sleep(tmpAttente);}
		    catch(InterruptedException ite){}				
	    }
	}
	if (tmp <= nbIteration)
	    throw new UnknownServiceException(ip + "/" + port);
	    
	return reponse;
    }	

    
    static String demanderQuestion(String ip, int port, String question, boolean attCrochets, int nombreMot) throws IOException {
	System.out.println("Essai de connexion au serveur " + ip + " " + port + " ...");
	Socket socket = new Socket(ip,port); 	
	System.out.println("Connexion au serveur " + ip + "/" + port);
	InputStream in   = socket.getInputStream();  // read-skip-close
	OutputStream out = socket.getOutputStream(); // write-flush-close
	
	out.write(question.getBytes());
	out.flush();
	System.out.println(">> " + question);
	String reponse = new String("");
	byte[] b = {0};
	int res, compteur = 0;
	res = in.read();
	// on laisse un peu de temps pour réceptioner toutes les données (??)
	try{Thread.sleep(100);}
	catch(InterruptedException ite){}		
	
	while (res != -1){
	    b[0] = (byte)res; 
	    reponse += new String(b);

	    // compte le nombre d'espace ou de crochets fermant // Et si on a un ']' dans le texte ? -.-"
	    if (attCrochets){
		if (reponse.length()-1 == reponse.indexOf("]", reponse.length()-1))
		    res = -1;
		else res = in.read(); 
	    }
	    else {
		if (reponse.length()-1 == reponse.indexOf(" ", reponse.length()-1))
		    compteur++;
		if (compteur >= nombreMot)
		    res = -1;
		else res = in.read(); 
	    }
	}
	
	out.close(); in.close(); socket.close();
	System.out.println("<< " + reponse);	
	System.out.println("Déco du serveur " + ip + "/" + port);
	return reponse;
    }

    
    // _fichierConf.getTaille()
    static String [][] demanderPiece(String ip, int port, String question, int nbIteration, int tmpAttente) throws UnknownServiceException, CharConversionException {    
	String [][] reponse = null;
	int tmp = 0;
	while (tmp < nbIteration){
	    try { 
		reponse = demanderPieceIntermediaire(ip, port, question);
		tmp = nbIteration*2; 
	    } catch (IOException ioe) { 
		tmp++; 
		if (tmpAttente>0)
		    try{Thread.sleep(tmpAttente);}
		    catch(InterruptedException ite){}				
	    }
	}
	if (tmp <= nbIteration)
	    throw new UnknownServiceException(ip + "/" + port);
	
	return reponse;
    }
    
    static String [][] demanderPieceIntermediaire(String ip, int port, String question) throws IOException, CharConversionException {
	System.out.println("Essai de connexion au serveur " + ip + " " + port + " ...");
	Socket socket = new Socket(ip,port); 	
	System.out.println("Connexion au serveur " + ip + "/" + port);
	InputStream in   = socket.getInputStream();  // read-skip-close
	OutputStream out = socket.getOutputStream(); // write-flush-close
	
	out.write(question.getBytes());
	out.flush();
	System.out.println(">> " + question);
	String reponse = new String("");
	String [][] reponse_partielle = new String[2][];
	byte[] b = {0};
	byte[] piece = new byte[_fichierConf.getTaille()];
	int res, compteur = 0;
	int i = 0;

	res = in.read();
	// on laisse un peu de temps pour réceptioner toutes les données (??)
	try{Thread.sleep(100);}
	catch(InterruptedException ite){}		
	

	while (res != -1 && compteur < 2){
	    b[0] = (byte)res; 
	    reponse += new String(b);
	    // on compte 2 d'espaces 
	    if (reponse.length()-1 == reponse.indexOf(" ", reponse.length()-1))
		compteur++;
	    res = in.read(); 
	}
	if (res == -1)
	    throw new CharConversionException("message data erroné");
	if (0 != reponse.indexOf("data "))
	    throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);
	
	b[0] = (byte)res; 
	reponse += new String(b);
	if (reponse.length()-1 != reponse.indexOf("[", reponse.length()-1))
	    throw new CharConversionException("message data erroné");
	res = in.read(); 
	while (res != -1) {
	    // On initialise
	    System.out.println("i =" + i);
	    reponse_partielle[0][i] = new String(""); 
	    reponse_partielle[1][i] = new String("");

	    // On lit l'indice
	    while (res != -1 && '0'<=res && res<='9') {
		b[0] = (byte)res; 
		reponse_partielle[0][i] = new String(b);
	        res = in.read(); 
	    }
	    if (res != ':')
		throw new CharConversionException("message data erroné");
	    // On lit la taille d'une pièce
	    res = in.read(piece); 
	    reponse_partielle[1][i] = new String(piece);
	    // On lit un espace
	    res = in.read(); 
	    b[0] = (byte)res; 
	    reponse += new String(b);
	    if (reponse.length()-1 != reponse.indexOf(" ", reponse.length()-1))
		throw new CharConversionException("message data erroné");
	    if (reponse.length()-1 == reponse.indexOf("]", reponse.length()-1))
		res = -1;
	    else res = in.read(); 
	    i++;
	}
	
	out.close(); in.close(); socket.close();
	System.out.println("<< " + reponse);	
	System.out.println("Déco du serveur " + ip + " " + port);
	return reponse_partielle;
    }

}