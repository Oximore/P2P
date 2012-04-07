CJ=javac
R=java
JFLAGS=
WFLAGS=-Xlint:unchecked
EXEC=Pair.class
SRC=$(wildcard *.java)

all: $(EXEC)


W: $(EXEC)
	$(CJ) $(WFLAGS) Pair.java

run: $(EXEC) client
	$(R) Pair

$(EXEC): $(SRC)
	$(CJ) Pair.java	

client: ServeurEssais.java ServeurEssaisRepete.java
	$(CJ) $<


.PHONY: clean clean_dwn mrproper

clean:
	rm  *.class *# *~ ; clear ; ls

clean_dwn:
	touch Download/.truc.data
	touch Download/.truc.txt
	rm ./Download/.*.txt ./Download/.*.data
	ls -a Download/
