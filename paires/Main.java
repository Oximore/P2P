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
	boolean estMisAssertion = true;
	assert estMisAssertion = false;
	if (!estMisAssertion) {
	    System.out.println("Execution impossible sans l'option -ea"); 
	    return; 
	}

	char c = ' ', cr = ']';
	System.out.println("un espace et un crochet:" + c + cr );
	
	char a = 4, b = 3;
	System.out.println("3 et 4:" + a + " et " + b );

	String mot = "Blablabla";
	System.out.println(mot + " sans la dernière lettre est : " + mot.substring(0,mot.length()-1));


	System.out.println(" 3 % 5 = " + (3%5));
	System.out.println(" 14 % 5 = " + (14%5));
	int i = 10, j = 5;
	
	//	System.out.println("truc".indexOf("t"));
	//System.out.println("truc".indexOf("t",0));
	
	/*	
	System.out.println("12/5 \t\t\t\t= " + (i/j));
	System.out.println("(int)12/5 \t\t\t= " + ((int)i/j));
	System.out.println("((double)12)/5 \t\t\t= " + (((double) i)/j));
	
	System.out.println("Math.ceil(((double)12)/5)\t= " + Math.ceil(((double)i)/j));
	*/

	/*
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
