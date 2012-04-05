J=javac
R=java
JFLAGS=
WFLAGS=-Xlint:unchecked
EXEC=Pair.class
SRC=$(wildcard *.java)

all: $(EXEC)


W: $(EXEC)
	$(J) $(WFLAGS) Pair.java

run: $(EXEC) client
	$(R) Pair

$(EXEC): $(SRC)
	$(J) Pair.java	

client: ServeurEssais.java ServeurEssaisRepete.java
	$(J) $<


.PHONY: clean clean_dwn mrproper

clean:
	rm  *.class *# *~ ; clear ; ls

clean_dwn:
	rm ./.*.txt

