/*  Main.java
 *  Blux
 *  07/02/12
 *
 *  Pour tester
 */

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class Main{
    public static void main(String[] args){
	System.out.println("Essais");

	int tab[] = new int[5];
	for (int i=0;i<5;i++)
	    tab[i] = 2*i;
	System.out.println(tab);

	

	List<int> liste = new ArrayList<int>();
	
	for (int i=0;i<5;i++)
	    liste.add(tab[i]);
	
	System.out.println(liste);
	// */
	
	//String g = "Le petit chat";
	//String[] d = g.split(" ");
	//System.out.println("->"+d[0]+d[2]);

	//for (int i=0;i<args.length;i++)
	//   System.out.println(args[i]);
	
	//	MiseAJour maj = new MiseAJour("Mise à jour","127.0.0.1",55555,5);
	//maj.start();
	
    }
}
