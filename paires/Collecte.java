/*  Collecte.java
 *  Blux
 *  30/03/12
 *
 *  sert à stocker les informations sur les autres pairs lors d'une requete
 */

import java.util.List;
import java.util.ArrayList;

public class Collecte{
    
    private List<InfoPair> _liste;
    
    Collecte(String infos){
	_liste = new ArrayList<InfoPair>();
	String[] info = infos.split("[")[1].split("]")[0].split(" ");
	for (int i=0; i<info.length ; i+=4){
	    if (info.length <= (i+3)){
		_liste.add(new InfoPair(info[i]+" "+info[i+1]+" "+info[i+2]+" "+info[i+3]));
	    }
	}
    }
}