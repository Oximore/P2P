CC=javac
CFLAGS=
LDFLAGS=
EXEC=Pair.class

all: $(EXEC) 

W: Pair.java FichierConfiguration.class Serveur.class Fichier.class FileHashSum.class
	$(CC) -Xlint:unchecked $<

Pair.class: Pair.java FichierConfiguration.class Serveur.class Fichier.class FileHashSum.class
	$(CC) $<

FichierConfiguration.class: FichierConfiguration.java
	$(CC) $<

Serveur.class: Serveur.java MiseAJour.class ServeurThread.class
	$(CC) $<

ServeurThread.class: ServeurThread.java 
	$(CC) $<

MiseAJour.class: MiseAJour.java Fichier.class
	$(CC) $<

FileHashSum.class: FileHashSum.java
	$(CC) $<

Fichier.class: Fichier.java 
	$(CC) $<

GestionFichier.class: GestionFichier.java 
	$(CC) $<

.PHONY: clean clean_dwn mrproper

clean:
	rm  *.class *# *~ ; clear ; ls

clean_dwn:
	rm ./.*.txt