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
    private int _ip;
    private int _port;
    private int _nb_connexion;
    private long _taille;
    private int _tmp_refresh;
    
    FichierConfiguration(String nom){
	try{
	    
	    String mot = new String("Truc : ");
	    RandomAccessFile file = new RandomAccessFile("fichier.txt","r");
	    
	    _ip = recupererValeure(file,"IP : ");
	    _port = recupererValeure(file,"Port : ");
	    _nb_connexion = recupererValeure(file,"Nb Connexion : ");
	    _tmp_refresh  = recupererValeure(file,"Tmp Refresh : ");
	    _taille = file.length();

	}
	catch(IOException e){
	    e.printStackTrace();
	}
	finally{
	    f.close();
	}

    }
    
    static private int recupererValeure(RandomAccessFile f,String champ) throws java.io.IOException {
	f.seek(0);
	String s = f.readLine();
	while(s != null){
	    if (s.indexOf(champ)>=0){
		int t = s.indexOf(champ)+champ.length();
		String truc = String.copyValueOf(s.toCharArray(),t,s.length()-t);
		int i = Integer.parseInt(truc);
		return i;
		//System.out.println(i);
		
	    }
	    s = f.readLine();
	}
	// levée d'exception
	return -1;
    }

    static public toString(){
	System.out.println();


	
    }
    
}
