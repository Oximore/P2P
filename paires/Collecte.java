/*  Collecte.java
 *  Blux
 *  30/03/12
 *
 *  sert à stocker les informations sur les autres pairs lors d'une requete
 */

import java.util.List;
import java.util.ArrayList;

public class Collecte{
    
    //public List<InfoPair> _liste;
    private InfoPair _tab[];
    
    Collecte(String infos){
	//_liste = new ArrayList<InfoPair>();
	String[] info = infos.substring(1+infos.indexOf("["),infos.indexOf("]")).split(" ");
	_tab = new InfoPair[(int)(info.length / 4)];
	for (int i=0; i<info.length ; i+=4){
	    if (info.length <= (i+3)){
		_tab[i/4] = new InfoPair(info[i]+" "+info[i+1]+" "+info[i+2]+" "+info[i+3]);
		//		_liste.add(new InfoPair(info[i]+" "+info[i+1]+" "+info[i+2]+" "+info[i+3]));
	    }
	}
	
	for (int i=0 ; i<_tab.length ; i++)
	    System.out.println("!<3! :" + _tab[i].toString());
	
    }

    public InfoPair[] getTab() { return _tab; }
    //    public List<InfoPair> getList(){ return _e; }
}