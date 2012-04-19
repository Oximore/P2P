CJ=javac
R=java
JFLAGS=
WFLAGS=-Xlint:unchecked
EXEC=Pair.class
SRC=$(wildcard *.java)
EXECS=$(SRC:.java=.class )


all: $(SRC)
	-rm *.class
	$(CJ) Pair.java

W: $(EXEC)
	-rm *.class
	$(CJ) $(WFLAGS) Pair.java

run: $(EXEC) client
	$(R) Pair

client: ServeurEssais.java ServeurEssaisRepete.java
	$(CJ) $<


.PHONY: clean clean_dwn mrproper

clean:
	-rm  *.class *# *~ ; clear ; ls

clean_dwn:
	-rm ./Download/.*.txt ./Download/.*.data ./Download/*.data
	ls -a Download/

copy:
	-rm -r ../trucmuche
	cp -r ../paires ../trucmuche
	make clean_dwn ../trucmuche
	-rm ../trucmuche/Download/*.txt ../trucmuche/Download/.*.txt
	echo "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbcccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" >> ../trucmuche/Download/foo.txt