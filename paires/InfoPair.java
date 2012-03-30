/*  InfoPair.java
 *  Blux
 *  30/03/12
 *
 *  sert à stocker les informations d'un pair lors d'une requete
 */


public class InfoPair{

    private String _nom;
    private String _key;
    private int _taille;
    private int _taillePiece;
    
    InfoPair(String info){
	String tab[] = info.split(" ");
	_nom = tab[0];
	_taille = Integer.parseInt(tab[1]);
	_taillePiece = Integer.parseInt(tab[2]);
	_key = tab[3];
    }
    
    public String getName(){ return _nom;}
    public String getKey() { return _key;}
    public int getTaille() { return _taille;}
    public int getTaillePiece(){ return _taillePiece;}
}