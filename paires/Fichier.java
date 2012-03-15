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
    private String _key;
    private int _taille_piece;
    private int _taille;
    private boolean[] _masque;
    
    static private String _directory = "Download/";
    
    Fichier(String nomFichier, String key, int taille, int taille_piece){
	_nom          = nomFichier ;
	_key          = key ;
	_taille       = taille ;
	_taille_piece = taille_piece ;
		
	int l = taille/taille_piece +1 ; // à modif
	_masque = new boolean[l] ;
	for (int i=0; i<_masque.length ; i++){
	    _masque[i] = false;
	}
    }

    public boolean[] getMasque() { return _masque ; }
    
    public void setMasque(boolean[] buffer) { _masque = buffer.clone() ; }

    public void saveValue(){
	try{
	    RandomAccessFile fileHidden = new RandomAccessFile(_directory+"."+_nom,"rw");
	    for (int i=0; i<_masque.length ; i++){
		if (_masque[i])
		    fileHidden.writeChar('1');
		else fileHidden.writeChar('0');
	    }
	    
	    fileHidden.close();
	}
	catch(IOException e){}
    }

    public String toString(){
	return _nom + " : key=" + _key + ", taille=" + _taille + ", taillePieces=" + _taille_piece;
    }

    // redefinir equals avec _key ?
        
}
