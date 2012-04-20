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
import java.util.ArrayList;


public class ToolsTelechargementThread extends Thread {
    // Quelle portée ?
    static FichierConfiguration _fichierConf;
    Hashtable<String,Fichier> _hash;
    static boolean _estMisAssertion;
    
    public ToolsTelechargementThread(String name, FichierConfiguration fichierConf, Hashtable<String,Fichier> hash){
	super(name);
	_fichierConf = fichierConf;
	_hash        = hash;
	_estMisAssertion = false;
	assert _estMisAssertion = true;
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
	    
	    if (i == 0) {
		tabMasque  = new boolean[info.length][masque.length()];
		for ( j=0 ; j<tabMasque.length ; j++)
		    for ( k=0 ; k<tabMasque[0].length ; k++)
			tabMasque[j][k] = false;
	    }
	    for ( j=0 ; j<masque.length() ; j++){
		if (j == masque.indexOf("0", j))
		    tabMasque[i][j] = false;
		else if (j == masque.indexOf("1", j))
		    tabMasque[i][j] = true;
		else 
		    throw new CharConversionException("masque mal formé");
	    }
	}
	return tabMasque;
    }
	
	    
    // *TODO* check ici si le fichier existe : @key -> @Fichier
    // *TODO* télécharger par combien ?
    void telecharger(String ip, int port, String key, boolean [] masque) throws UnknownServiceException, CharConversionException {
	int i = 0 , max;
	String reponse = "", question = "";
	List<String[]> reponse_partielle;
	boolean bool;	
	Fichier file = (Fichier)_hash.get(key);

	while (i < masque.length) {	
	    reponse = "";
	    question = new String("getpieces " + key + " [");
	    max = 25; // Combien par combien je télécharge
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
		if (file == null)
		    System.out.println("Gros problème !");
		
		reponse_partielle =  demanderPiece(ip, port, question, 5, 1000, (int) file.getTaille() );
		enregistre(file, reponse_partielle);
		System.out.println(file.getPourcentage() + "%");

		// *TODO* à virer ?
		try{Thread.sleep(100);}
		catch(InterruptedException ite){ 
		    ite.printStackTrace(); 
		}		
	    }
	}
    }
    
    static void enregistre(Fichier f, List<String[]> infos){ // String [][] infos){
	//System.out.println("fonction enregistre");
	int indice;
	boolean [] masque  = f.getMasque();

	if (f != null){
	    // *TODO* ressérer le try pour enregistrer le masque en partie ?
	    try{
		RandomAccessFile file = new RandomAccessFile("Download/"+f.getName(), "rw");
		for (int i=0 ; i<infos.size() ; i++){
		    indice = Integer.parseInt(infos.get(i)[0]);
		    file.seek(indice*f.getTaillePiece());
		    file.writeBytes(infos.get(i)[1]);
		    masque[indice] = true;
		}
		file.close();
	    } catch (IOException ioe) { 
		System.out.println("ioe in enregistrer" + ioe);
		ioe.printStackTrace();
	    }

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
		    catch(InterruptedException ite){
			ite.printStackTrace();
		    }				
	    }
	}
	if (tmp <= nbIteration)
	    throw new UnknownServiceException(ip + " " + port);
	    
	return reponse;
    }	

    
    static String demanderQuestion(String ip, int port, String question, boolean attCrochets, int nombreMot) throws IOException {
	if (_estMisAssertion) 
	    System.out.println("Essai de connexion au serveur " + ip + " " + port + " ...");
	Socket socket = new Socket(ip,port); 	
	if (_estMisAssertion) 
	    System.out.println("Connexion au serveur " + ip + " " + port);
	InputStream in   = socket.getInputStream();  // read-skip-close
	OutputStream out = socket.getOutputStream(); // write-flush-close
	
	out.write(question.getBytes());
	out.flush();
	if (_estMisAssertion) 
	    System.out.println(">> " + question);
	String reponse = new String("");
	byte[] b = {0};
	int res, compteur = 0;
	res = in.read();
	// on laisse un peu de temps pour réceptioner toutes les données
	try{Thread.sleep(100);}
	catch(InterruptedException ite){
	    ite.printStackTrace();
	}		
	
	while (res != -1){
	    b[0] = (byte)res; 
	    reponse += new String(b);

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
	if (_estMisAssertion) 
	    System.out.println("<< " + reponse);	
	if (_estMisAssertion) 
	    System.out.println("Déco du serveur " + ip + "/" + port);
	return reponse;
    }

    static List<String[]> demanderPiece(String ip, int port, String question, int nbIteration, int tmpAttente, int taille_totale) throws UnknownServiceException, CharConversionException {    
	List<String[]> reponse = null;
	int tmp = 0;
	while (tmp < nbIteration){
	    try { 
		reponse = demanderPieceIntermediaire(ip, port, question, taille_totale);
		tmp = nbIteration*2; 
	    } catch (IOException ioe) { 
		tmp++; 
		if (tmpAttente>0)
		    try{Thread.sleep(tmpAttente);}
		    catch(InterruptedException ite){}				
		ioe.printStackTrace();
	    }
	}
	if (tmp <= nbIteration)
	    throw new UnknownServiceException(ip + "/" + port);
	
	return reponse;
    }
    
    static List<String[]> demanderPieceIntermediaire(String ip, int port, String question, int taille_totale) throws IOException, CharConversionException {
	if (_estMisAssertion) 
	    System.out.println("Essai de connexion au serveur " + ip + " " + port + " ...");
	Socket socket = new Socket(ip,port); 	
	if (_estMisAssertion)
	    System.out.println("Connexion au serveur " + ip + " " + port);
	InputStream in   = socket.getInputStream();  // read-skip-close
	OutputStream out = socket.getOutputStream(); // write-flush-close
	
	out.write(question.getBytes());
	out.flush();
	if (_estMisAssertion) 
	    System.out.println(">> " + question);
	String reponse = new String(""); 
	List<String[]> reponse_partielle = new ArrayList<String[]>();
	
	
	byte[] b = {0};
	byte[] piece = new byte[_fichierConf.getTaille()];
	int res, compteur = 0;
	int last_indice = (int) Math.floor( ((double)taille_totale) / _fichierConf.getTaille());
	byte[] last_piece = new byte[taille_totale % _fichierConf.getTaille() ];
	int i = 0;

	res = in.read();
	// on laisse un peu de temps pour réceptioner toutes les données
	try{Thread.sleep(100);}
	catch(InterruptedException ite){
	    ite.printStackTrace();
	}		
	

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
	    reponse_partielle.add(i,new String[2]);
	    // On lit l'indice
	    reponse_partielle.get(i)[0] = new String("");
	    while (res != -1 && '0'<=res && res<='9') {
		b[0] = (byte)res; 
		reponse_partielle.get(i)[0] += new String(b);
		res = in.read(); 
	    }
	    

	    if (res != ':')
		throw new CharConversionException("message data erroné");
	    // On lit la taille d'une pièce
	    if (last_indice != Integer.parseInt(reponse_partielle.get(i)[0])){
		res = in.read(piece); 		
		reponse_partielle.get(i)[1] = new String(piece);
		//System.out.println("rep partielle "+ i + " :" + reponse_partielle.get(i)[1]);
	    }
	    else {
		res = in.read(last_piece);
		reponse_partielle.get(i)[1] = new String(last_piece);
		//System.out.println("rep partielle "+ i + " :'" + reponse_partielle.get(i)[1] + "'");
	    }
	    // On lit un espace ou un crochet
	    res = in.read(); 
	    if (res != ' ' && res != ']')    
		System.out.println("problème de lecture : '" + (char)res + "' ou " + res);
		//throw new CharConversionException("message data erroné");
	    if (res == ']') 
		res = -1;
	    else res = in.read(); 
	    i++;
	}
	
	out.close(); in.close(); socket.close();
	if (_estMisAssertion)  {
	    System.out.print("<< " + reponse);
	    for (i=0 ; i<reponse_partielle.size() ; i++) {
		System.out.print(reponse_partielle.get(i)[0] + ":" + reponse_partielle.get(i)[1]);
		if (i<reponse_partielle.size()-1)
		    System.out.print(" ");
		else System.out.println("]");
	    }
	}
	if (_estMisAssertion) 		
	    System.out.println("Déco du serveur " + ip + " " + port);
	return reponse_partielle;
    }

}