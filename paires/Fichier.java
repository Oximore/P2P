/*  Fichier.java
 *  Blux
 *  07/02/12
 *
 *  Implémente la structure de donnée des data.
 */
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.String;
import java.io.File;

class Fichier{
    private String _nom;
    private int _key;
    private int _taille_piece;
    private int _taille;
    private byte[] _masque;  // BufferMap ?

    private static String _directory = "Download/";

    Fichier(String nomFichier){
	try{

	    _nom = nomFichier; // si on change nomFichier apres que ce passe t'il ?
	    
	    File repertoire = new File(_directory);
	    boolean fichierExiste = false;
	    if ( repertoire.isDirectory() ) {
                File[] list = repertoire.listFiles();
                if (list != null){
		    for ( int i = 0; i < list.length; i++) {
			if ( nomFichier.equals("." + list[i].getName()) ){
			    fichierExiste = true;
			}
		    } 
                } 
		else {
		    // levé d'exception ?
		    System.err.println(repertoire + " : Erreur de lecture.");
                }
				
		if (fichierExiste){

		    // if open nomFichier do ...
		    
		    // On charge le fichier existant
		    RandomAccessFile fileHidden = new RandomAccessFile(_directory + "." + nomFichier,"r");

		    // Vérifier la validitée de ces informations ?
		    _taille = recupererValeureInt(fileHidden,"taille : ") ;
		    _taille_piece = recupererValeureInt(fileHidden,"taille_piece : ");
		    _key = recupererValeureInt(fileHidden,"key : ");
		    _masque = new byte[42]; // créer un array de byte ?
		    
		    fileHidden.close();
		}
	    } 
	    	    
	}
	catch(IOException e){
	    e.printStackTrace();
	}

    }

    Fichier(String nomFichier, int taille_piece){
	_nom          = nomFichier ;
	_taille_piece = taille_piece ;
	
	// créer le .nomFichier
    }


    Fichier(String nomFichier, int key, int taille, int taille_piece){
	_nom          = nomFichier ;
	_key          = key ;
	_taille       = taille ;
	_taille_piece = taille_piece ;
	
	int l = taille/taille_piece +1 ; // à modif
	_masque = new byte[l] ;
    }

    public void initFichier(){
	
	
    }
    
    

    public byte[] getMasque(){
	return _masque;
    }
    
    public void setMasque(byte[] buffer){
	_masque = buffer; // copie, warning ?
    }

    public void saveValue(){
	try{
	    RandomAccessFile fileH = new RandomAccessFile(_directory+"."+_nom,"rw");
	    fileH.writeChars("key : "+_key+"\ntaille : "+_taille+"\ntaille_piece : "+_taille_piece+"\nmasque : "+"un auuuutre truc"+"\n");
	    fileH.close();
	}
	catch(IOException e){}

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
	    }
	    s = f.readLine();
	}
	// levée d'exception
	return "????";
    }

    
    public String toString(){
	return _nom + " : key=" + _key + ", taille=" + _taille + ", taillePieces=" + _taille_piece;
    }

    
    
    
}
