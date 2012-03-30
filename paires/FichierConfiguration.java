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
import java.lang.IllegalArgumentException;


class FichierConfiguration{
    private String _ip;
    private int _port;
    private int _nb_connexion;
    private int _taille;
    private int _tmp_refresh;

    FichierConfiguration(String fileName){
	try {
	    RandomAccessFile file = new RandomAccessFile(fileName,"r");
	    
	    _ip           = recupererValeure(file,"IP : ");
	    _port         = recupererValeureInt(file,"Port : ");
	    _nb_connexion = recupererValeureInt(file,"Nb Connexion : ");
	    _tmp_refresh  = recupererValeureInt(file,"Tmp Refresh : ");
	    _taille       = recupererValeureInt(file,"taille : ");
	    file.close();
	}
	catch(IOException e){
	    System.out.println("ioe in FichierConfiguration" + e);
	    e.printStackTrace();
	}
	//finally{ file.close();}
    }


    public int    getPort() { return _port; }
    public String getIp() { return _ip; }
    public int    getNbConnexion() { return _nb_connexion; }
    public int    getTmp() { return _tmp_refresh; }
    public int    getTaille() { return _taille; }
    
    public void setNbConnexion(int nb){
	if (nb<0) throw new IllegalArgumentException("nombre de connexion négatif");
	_nb_connexion = nb;
    }
    
    public void setTaille(int taille){
	if (taille<0) throw new IllegalArgumentException("taille de découpe des fichiers négative");
	_taille = taille;
    }

    public void saveValues(String fileName){
	try{
	    RandomAccessFile file = new RandomAccessFile(fileName,"rw");
	    
	    file.writeBytes("IP : " + _ip + "\n");
	    file.writeBytes("Port : " + _port + "\n");
	    file.writeBytes("Nb Connexion : " + _nb_connexion + "\n");
	    file.writeBytes("Tmp Refresh : " + _tmp_refresh + "\n");
	    file.writeBytes("taille : " + _taille + "\n");
	    
	    file.close();
	}
	catch(IOException e){ 
	    System.out.println("ioe exception in saveValues : " + e) ; 
	}
	//	finally{ file.close(); }
    }


    static private int recupererValeureInt(RandomAccessFile f, String champ)throws java.io.IOException {
	System.out.println(recupererValeure(f, champ));
	return Integer.parseInt(recupererValeure(f, champ));
    }

    static private String recupererValeure(RandomAccessFile f, String champ) throws java.io.IOException {
	f.seek(0);
	String s = f.readLine();
	while(s != null){
	    if (s.indexOf(champ)>=0){
		int t = s.indexOf(champ)+champ.length();
		return String.copyValueOf(s.toCharArray(),t,s.length()-t);
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
