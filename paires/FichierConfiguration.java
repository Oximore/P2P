/*  FichierConfiguration.java
 *  Blux
 *  14/02/12
 *
 *  Sert à communiquer avec le fichier de configuration
 */

//package noeud;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.lang.String;

class FichierConfiguration{
    private String _ip;
    private int _port;
    private int _nb_connexion;
    private long _taille;
    private int _tmp_refresh;

    FichierConfiguration(String fileName){
	try{
	    
	    String mot = new String("Truc : ");
	    RandomAccessFile file = new RandomAccessFile("fichier.txt","r");
	    
	    _ip = new String(recupererValeure(file,"IP : "));

	    _port = recupererValeureInt(file,"Port : ");
	    _nb_connexion = recupererValeureInt(file,"Nb Connexion : ");
	    _tmp_refresh  = recupererValeureInt(file,"Tmp Refresh : ");
	    _taille = file.length(); // Faux, rassurez moi ? c'est la taille des découpes
	    file.close();
	}
	catch(IOException e){
	    e.printStackTrace();
	}
	finally{
	    //	    file.close();
	}
    }


    public int getPort(){
	return _port;

    }
    
    public String getIp(){
	return _ip;
    }
    
    public int getNbConnexion(){
	return _nb_connexion;
    }
    
    public int getTmp(){
	return _tmp_refresh;
    }
    
    public long getTaille(){
	return _taille;
    }
    
    public void setNbConnexion(int nb){ // relève erreur si <0?
	_nb_connexion = nb;
    }
    
    public void setTaille(int taille){
	_taille = taille;
    }

    public void saveValues(String fileName){
	try{
	    RandomAccessFile file = new RandomAccessFile(fileName,"rw");
	    
	    file.writeChars("IP : " + _ip + "\n");
	    file.writeChars("Port : " + _port + "\n");
	    file.writeChars("Nb Connexion : " + _nb_connexion + "\n");
	    file.writeChars("Tmp Refresh : " + _tmp_refresh + "\n");
	    file.writeChars("taille : " + _taille + "\n");
	    
	    file.close();
	}
	catch(IOException e){
	    e.printStackTrace();
	}
	finally{
	    //	    file.close();
	}
    }


    static private int recupererValeureInt(RandomAccessFile f, String champ)throws java.io.IOException {
	return Integer.parseInt(recupererValeure(f, champ));
    }

    static private String recupererValeure(RandomAccessFile f, String champ) throws java.io.IOException {
	f.seek(0);
	String s = f.readLine();
	while(s != null){
	    if (s.indexOf(champ)>=0){
		int t = s.indexOf(champ)+champ.length();
		return String.copyValueOf(s.toCharArray(),t,s.length()-t);
		//		String truc = String.copyValueOf(s.toCharArray(),t,s.length()-t);
		//		return truc;
		//System.out.println(i);
		
	    }
	    s = f.readLine();
	}
	// levée d'exception
	return "";
    }

       
    public String toString(){
	return 
	    "Option de configuration:\n" 
	    + "Tracker : Ip: " + _ip +", port: " + _port + "\n"
	    + "Nombre de connexion simultanées max : " + _nb_connexion + "\n"
	    + "Taille de découpe des fichiers : " + _taille + "\n"
	    + "Temps de refresh : " + _tmp_refresh + "\n";
    }

    
    
    
}
