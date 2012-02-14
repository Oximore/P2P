/*  BufferMap.java
 *  Blux
 *  07/02/12
 *
 *  Sert à gérer les BufferMap.
 */

package noeud;

class BufferMap{
    private long buffer;
    private int piece;
    
    BufferMap(int taille, int taille_piece){
	this.buffer = 0;// et taille dans tout ça ? un long est il suffisant ?
	this.piece  = taille_piece;
    }

    getBuff(){
	return buffer;
    }

    getTaille(){
    
    }

    getTaillePiece(){
	return piece;
    }
    
    add(int n){
	buffer |= (1<<n);
    }

 
}
