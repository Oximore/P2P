/* ThreadUtilisateur.java
 * Blux
 * 03/04/12
 *
 * Thread 4 : Gère les demandes de l'utilisateur du service
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


//   *TODO* *MEMENTO* compter la longueur des fichiers en bytes pas en String !!!


public class ThreadUtilisateur extends Thread {
    private FichierConfiguration _fichierConf;
    private Hashtable _hash;

    public ThreadUtilisateur(String name, FichierConfiguration fichierConf, Hashtable hash){
	super(name);
	_fichierConf = fichierConf;
	_hash = hash;
    }
    
    public void run(){
	System.out.println("Bienvenue dans le programme utilisateur !");
	Scanner scan = new Scanner(System.in);
	String question = "", reponse = "" , compteur = "";
	int ret = -1, i = 0, j, resultat = -1;
	String key_to_dl , name_file_to_dl;
	Collecte co;
	InfoPair [] tab;
	String [] info;
	boolean bool;
	int port_tmp, taille_tmp, taille_piece_tmp;

	while (ret != 0){
	    while (! (0 <= ret && ret <= 2) ){
		System.out.println("0 :\tQuitter");
		System.out.println("1 :\tRechercher par nom de fichier");
		System.out.println("2 :\tRechercher par clé");
		while (!scan.hasNextInt())
		    compteur = scan.next();
		ret = scan.nextInt();
	    }
	    if (ret == 0){
		// On quitte ce thread
		System.out.println("Bye bye !");
		return ;
	    }
	    try {
		
		if (ret == 1){
		    System.out.println("tapez le nom du fichier recherché :");
		    compteur = scan.next();
		    question = "look [filename=\"" + compteur + "\"]";
		}
		else if (ret == 2){
		    System.out.println("tapez la clé du fichier recherché :");
		    compteur = scan.next();
		    question = "look [key=\"" + compteur + "\"]";
		}
		ret = -1;

		reponse = demandeCrochet(_fichierConf.getIp(), _fichierConf.getPort(), question, 5, 1000);
	    
		if (0 != reponse.indexOf("list"))
		    throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);

		co = new Collecte(reponse);
		tab = co.getTab();
	    

		//  *TODO*
		while (! (0<=resultat && resultat <= co.getTab().length+1)){ 
		    System.out.println("Voici les fichiers correspondant à vos critères de recherche:");
		    for (i=0; i<co.getTab().length ; i++)
			System.out.println((1+i) + ":\t" + co.getTab()[i]);
		    System.out.println("Tapez 'n' pour commencer le téléchargement ou '0' pour annuler.");
		
		    while (!scan.hasNextInt())
			compteur = scan.next();
		    resultat = scan.nextInt();
		}
		if (resultat != 0){
		    resultat--;
		    key_to_dl = co.getTab()[resultat].getKey();
		    name_file_to_dl = co.getTab()[resultat].getName();
		    taille_tmp = co.getTab()[resultat].getTaille();
		    taille_piece_tmp = _fichierConf.getTaille();
		
		    co.epurer(key_to_dl);
		    // on recherche le fichier de clé key_to_dl
		    question = "getfile " + key_to_dl;
		
		    reponse = demandeCrochet(_fichierConf.getIp(), _fichierConf.getPort(), question, 5 , 1000);
		
		    if (0 != reponse.indexOf("peers") || !key_to_dl.equals(reponse.split(" ")[1]))
			throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);

		    info = reponse.substring(1+reponse.indexOf("["),reponse.indexOf("]")).split(" ");
		    boolean [][] tabMasque = createTabMasque(info,key_to_dl);
		
		
		    for ( i=0 ; i<tabMasque.length ; i++) {
			bool = false;
			for ( j=0 ; j<tabMasque[0].length ; j++){
			    if (!bool && tabMasque[i][j])
				bool = true;
			    else
				tabMasque[i][j] = false;
			}
		    }

		    System.out.println("Création du fichier Download/" + name_file_to_dl);
		    Fichier newfichier = new Fichier(name_file_to_dl , key_to_dl, taille_tmp , taille_piece_tmp);
		    _hash.put(key_to_dl, newfichier);
		    System.out.println("Fichier crée.");
		    
		    for ( i=0 ; i<tabMasque.length ; i++ ){
			port_tmp = Integer.parseInt(info[i].split(":")[1]);
			telecharger(info[i].split(":")[0], port_tmp, key_to_dl, tabMasque[i]);    
			// sauve les données ?
		    }
		
		    System.out.println("Fichier téléchargé");
		}
		resultat = -1;
	    } catch (UnknownServiceException se) {
		System.out.println("Un serveur n'as pas pu être trouvé : " + se);
		//      return ;
	    } catch (CharConversionException ce) {
		System.out.println("Réponse d'un serveur mal formée : " + ce);
		//	    return ;
	    }
	}		
    }    
    
    // @info : couples Ip/Port 
    // @key  : clé du fichier souhaité
    public boolean [][] createTabMasque(String [] info, String key) throws UnknownServiceException, CharConversionException {
	int i, j, k, port;
	String ip, masque, question, reponse = "";
	boolean [][] tabMasque = null;
	
	
	for ( i=0 ; i<info.length ; i++){ 
	    ip   = info[i].split(":")[0];
	    port = Integer.parseInt(info[i].split(":")[1]);
	    
	    question =  "interested " + key;
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
	    for ( j=0 ; j<masque.length()-1 ; j++){
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
    public void telecharger(String ip, int port, String key, boolean [] masque) throws UnknownServiceException, CharConversionException {
	String reponse = "";
	String question = new String("getpieces " + key + " [");
	for (int i=0 ; i<masque.length ; i++)
	    if (masque[i])
		question += i + " ";
	question = question.trim() + "]";
	
	reponse = demandeCrochet(ip, port, question, 5 , 1000);
	
	String key_tmp = reponse.split(" ")[1];
	if (0 != reponse.indexOf("data") || key == null || !key.equals(key_tmp))
	    throw new CharConversionException("réponse inapropriée :\n>> " + question + "\n<< " + reponse);
	
	enregistre((Fichier)_hash.get(key_tmp), reponse.substring(1+reponse.indexOf("["),reponse.indexOf("]")).split(" "));
    }
    
    
    public void enregistre(Fichier f, String [] infos){
	System.out.println("fonction enregistre");
	String [][] tab = new String[infos.length][2];
	int indice;
	boolean [] masque  = f.getMasque();
	 
	for (int i=0 ; i<infos.length ; i++)
	    tab[i] = infos[i].split(":"); 

	if (f != null){
	    try{
		RandomAccessFile file = new RandomAccessFile("Download/"+f.getName(), "rw");
		for (int i=0 ; i<tab.length ; i++){
		    //System.out.println("indice : " + tab[i][0]);
		    indice = Integer.parseInt(tab[i][0]);
		    file.seek(indice*f.getTaillePiece());
		    file.writeBytes(tab[i][1]);
		    //file.write(tab[i][1].getBytes());
		    masque[indice] = true;
		}
		file.close();
	    } catch (IOException ioe) { System.out.println("ioe in enregistrer" + ioe); }

	} else { System.out.println("premier para de enregistrer foireux"); }
	f.setMasque(masque);
    }

    // @ tmpAttente en ms
    public String demandeCrochet(String ip, int port, String question, int nbIteration, int tmpAttente) throws UnknownServiceException {
	return demandeIntermediare(ip, port, question, true, 0, nbIteration, tmpAttente);
    }

    public String demandeMot(String ip, int port, String question, int nbIteration, int tmpAttente, int nbMots) throws UnknownServiceException {
	return demandeIntermediare(ip, port, question, false, nbMots, nbIteration, tmpAttente);
    }
    
    
    public String demandeIntermediare(String ip, int port, String question, boolean attCrochets, int nombreMot, int nbIteration, int tmpAttente ) throws UnknownServiceException {
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

    
    public String demanderQuestion(String ip, int port, String question, boolean attCrochets, int nombreMot) throws IOException {
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
	//b[0] = res; 
	try{Thread.sleep(100);}
	catch(InterruptedException ite){}		
	
	while (res != -1){
	    b[0] = (byte)res; 
	    reponse += new String(b);
	    // compte le nombre d'espace ou de crochets fermant
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

}