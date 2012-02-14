/*  Main.java
 *  Blux
 *  14/02/12
 *
 *  Pour tester
 */

import java.io.RandomAccessFile;
import java.io.IOException;



public class Test{
    public static void main(String[] args){
	System.out.println("Essais");
	try{
	
	RandomAccessFile f = new RandomAccessFile("fichier.txt","r");
	String s = f.readLine();
	while(s != null){
	    if (s.indexOf("Truc")>=0){
		System.out.println("Truc trouvé");
	    }
	    s = f.readLine();
	}
	f.close();
	}
	catch(IOException e){
	    e.printStackTrace();
	}
    }
}

