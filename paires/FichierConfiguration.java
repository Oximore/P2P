/*  FichierConfiguration.java
 *  Blux
 *  14/02/12
 *
 *  Sert à communiquer avec le fichier de configuration
 */

//package noeud;

class FichierConfiguration{
    private int _ip;
    private int _port;
    private int _nb_connexion;
    private int _taille;
    private int _tmp_refresh;

    FichierConfiguration(String nom){
	try{
	    RandomAccessFile f = new RandomAccessFile("fichier.txt","r");
	    String s = f.readLine();
	    while(s != null){
		if (s.indexOf("IP")>=0){
		    s = f.readLine();
		    this.ip = Interger.parseInt(s); 
		    

		}
		else if (0){
		
		}
		
		
		s = f.readLine();
	    }
	}
	catch(IOException e){
	    e.printStackTrace();
	}
	finally{
	    f.close();
	}


    
}
