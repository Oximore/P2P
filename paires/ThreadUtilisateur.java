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
	int ret = -1, i = 0, j, resultat = -1, tmp = 0;
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

	    tmp = 0;
	    while (tmp < 5){
		try { reponse = demanderQuestion(_fichierConf.getIp(), _fichierConf.getPort(), question, true, 0);
		    tmp = 10; } catch (IOException ioe) { tmp++; }
	    }
	    if (0 != reponse.indexOf("list"))
		System.exit(-1); // *TODO* levé d'ex
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
		
		tmp = 0;
		while (tmp < 5){
		    try { reponse = demanderQuestion(_fichierConf.getIp(), _fichierConf.getPort(), question, true , 0);
			tmp = 10; } catch (IOException ioe) { tmp++; }
		}
		if (0 != reponse.indexOf("peers") || !key_to_dl.equals(reponse.split(" ")[1]))
		    System.exit(-1);
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
	}		
    }
    
    // @info : couples Ip/Port 
    // @key  : clé du fichier souhaité
    public boolean [][] createTabMasque(String [] info, String key){
	int i, j, k, tmp, port;
	String ip, masque, reponse = "";
	boolean [][] tabMasque = null;
	
	
	for ( i=0 ; i<info.length ; i++){ 
	    ip   = info[i].split(":")[0];
	    port = Integer.parseInt(info[i].split(":")[1]);
	    
	    tmp = 0;
	    while (tmp < 5){
		try { reponse = demanderQuestion(ip, port, "interested "+key, false, 3);
		    tmp = 10; } catch (IOException ioe) { tmp++; }
	    }
	    if (0!= reponse.indexOf("have") || !key.equals(reponse.split(" ")[1]))
		System.exit(-1);
	    
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
		    System.exit(-1);
	    }
	}
	return tabMasque;
    }
	
	    
    // *TODO* check ici si le fichier existe : @key -> @Fichier
    public void telecharger(String ip, int port, String key, boolean [] masque){
	String reponse = "";
	String question = new String("getpieces " + key + " [");
	for (int i=0 ; i<masque.length ; i++)
	    if (masque[i])
		question += i + " ";
	question = question.trim() + "]";
	




	int tmp = 0;
	while (tmp < 5){
	    try { reponse = demanderQuestion(ip, port, question, true , 0);
		tmp = 10; } catch (IOException ioe) { tmp++; }
	}
	
	String key_tmp = reponse.split(" ")[1];
	if (0 != reponse.indexOf("data") || key == null || !key.equals(key_tmp))
	    System.exit(-1);
	
	enregistre((Fichier)_hash.get(key_tmp), reponse.substring(1+reponse.indexOf("["),reponse.indexOf("]")).split(" "));
    }
    
    
    public void enregistre(Fichier f, String [] infos){
	System.out.println("fonction enregistre");
	String [][] tab = new String[infos.length][2];
	int indice;
	boolean [] masque  = f.getMasque();
	
	/** /
	System.out.print("masque getM : ");
	for (int i=0 ; i<masque.length ; i++)
	    if (masque[i])
		System.out.print("1");
	    else
		System.out.print("0");
	System.out.print("\n"); // */



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
		    masque[indice] = true;
		}
		file.close();
	    } catch (IOException ioe) { System.out.println("ioe in enregistrer" + ioe); }
	    
	} else { System.out.println("premier para de enregistrer foireux"); }
	f.setMasque(masque);
    }
    
    
    public String demanderQuestion(String ip, int port, String question, boolean attCrochets, int nombreMot) throws IOException {
	System.out.println("Essai de connexion au serveur " + ip + " " + port + " ...");
	Socket socket = new Socket(ip,port); 	
	System.out.println("Connexion au serveur " + ip + " " + port);
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
	System.out.println("Déco du serveur " + ip + port);
	return reponse;
    }

}