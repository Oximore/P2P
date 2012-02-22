/*  Main.java
 *  Blux
 *  14/02/12
 *
 *  Pour tester
 */

import java.io.RandomAccessFile;
import java.io.IOException;
import java.lang.String;


public class Test{

    public static void main(String[] args){
	System.out.println("Essais");
	
	try{
	    
	    String mot = new String("Truc : ");
	    RandomAccessFile file = new RandomAccessFile("fichier.txt","r");
	    
	    System.out.println("Truc : " + recupererValeure(file,"Truc : "));
	    System.out.println("Machin : " + recupererValeure(file,"Machin : "));

	    file.close();
	}
	catch(IOException e){
	    e.printStackTrace();
	}
	System.out.println("Fin test");
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
    
}

