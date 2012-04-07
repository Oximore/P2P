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
import java.lang.Math;


class Fichier{
    private String _nom;
    private String _key;
    private int _taille_piece;
    private long _taille;
    private boolean[] _masque;
    
    static private String _directory = "Download/";
    
    Fichier(String nomFichier, String key, long taille, int taille_piece){
	_nom          = nomFichier ;
	_key          = key ;
	_taille       = taille ;
	_taille_piece = taille_piece ;
		
	int l = (int) Math.ceil( ((double)taille) / taille_piece);

	_masque = new boolean[l] ;
	for (int i=0; i<_masque.length ; i++){
	    _masque[i] = false;
	}
    }

    public boolean[] getMasque() { return _masque ; }
    public String getKey() { return _key; }
    public String getName() { return _nom; }
    public long getTaille() { return _taille; }
    public int  getTaillePiece() { return _taille_piece; }
    
    public void setMasque(boolean[] buffer) { _masque = buffer.clone(); }
    public void setTaillePiece(int taillePieces) { _taille_piece = taillePieces; }
    public void setTaille(int taille) { _taille = taille; }
    
    public boolean isIncomplet() { return (!this.isComplet()); }
    public boolean isComplet(){
	for (int i=0; i<_masque.length ; i++){
	    if (!_masque[i])
		return false; 
	}
	return true;
    }
    
    

    public void saveValue(){
	try{
	    RandomAccessFile fileHidden = new RandomAccessFile(_directory+"."+_nom,"rw");
	    // Sauve le masque sous forme de 0 et 1
	    for (int i=0; i<_masque.length ; i++){
		if (_masque[i])
		    fileHidden.write('1');
		else fileHidden.write('0');
	    }
	    fileHidden.write('\n');
	    
	    // Sauver la taille de découpage de la pièce
	    fileHidden.writeBytes(_taille_piece+"\n");
	    fileHidden.writeBytes(_taille+"\n");
	    	    
	    fileHidden.close();
	}
	catch(IOException e){}
    }

    public String toString(){
	return _nom + " : key=" + _key + ", taille=" + _taille + ", taillePieces=" + _taille_piece;
    }

    // redefinir equals avec _key ?
        
}
