/*  Test.java
 *  Blux
 *  14/02/12
 *
 *  Pour tester
 */

import java.util.Hashtable;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.lang.String;
//import java.lang.Character;
import java.io.File;

public class Test{
    
    public static void main(String[] args){
	System.out.println("Essais");
	

	/*
	  FileHashSum test = new FileHashSum();
	  
	  File f1 = new File("toto.txt");
	  File f2 = new File("tata.txt");
	  String sum1 = FileHashSum.sha1sum(f1);
	  String sum2 = FileHashSum.sha1sum(f2);
	  
	  System.out.println("1: " + sum1);
	  System.out.println("2: " + sum2);
	  System.out.println("3: " + FileHashSum.compareSha1sum(f1,sum1));
	  System.out.println("4: " + FileHashSum.compareSha1sum(f1,sum2));
	  System.out.println("5: " + FileHashSum.compareMd5sum(f1,FileHashSum.md5sum(f2)));
	*/  
	
	
	//Fichier fichier = new Fichier("screu",424242,42000,42);
	//fichier.saveValue();
	/*
	  try{
	    
	    File repertoire = new File("Download");
	    System.out.println ( repertoire.getAbsolutePath());
	    //f.close();
	    if ( repertoire.isDirectory() ) {
                File[] list = repertoire.listFiles();
                if (list != null){
		    for ( int i = 0; i < list.length; i++) {
			// Appel récursif sur les sous-répertoires
			//			listeRepertoire( list[i]);
			System.out.println ( list[i].getName());
		    } 
                } else {
		    System.err.println(repertoire + " : Erreur de lecture.");
                }
	    } 
	    
	    RandomAccessFile file = new RandomAccessFile("Fichier.txt","rw");
	    
	    int b;
	    b = file.read();
	    while (b != -1){
		System.out.print((char)b);
		b = file.read();
	    }	
	    
	    
	    
	    file.close();
	}
	catch(IOException e){
	    e.printStackTrace();
	}	
	*/	
	
	Hashtable h = new Hashtable(30,(float)0.6);
	
	//	put(Object key, Object value) 
	Object i;
	
	i = h.put("one", new Integer(42));
	if (i != null)
	    System.out.println("i n'est pas nul");
	i = h.put("two", new Integer(2));
	if (i != null)
	    System.out.println("i n'est pas nul");
	i = h.put("three", new Integer(3));
	if (i != null)
	    System.out.println("i n'est pas nul");

	i = h.put("one", new Integer(1));
	if (i != null)
	    System.out.println("*i n'est pas nul : " + i);
	
	
	Integer n = (Integer)h.get("two");
	if (n != null) {
	    System.out.println("two = " + n);
	}
	
	
    }
    
}

