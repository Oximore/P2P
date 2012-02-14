/*  Fichier.java
 *  Blux
 *  07/02/12
 *
 *  Implémente la structure de donnée des data.
 */

package noeud;

class Fichier{
    String nom;
    int key;
    int taille_piece;
    int taille;
    BufferMap buffer;
    file* data;
    
    Fichier(){
	RandomAccessFile f = new RandomAccessFile(NOM_FICHIER,MODE);     
    }
    
    
}
