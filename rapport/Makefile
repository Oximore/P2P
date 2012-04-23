CC  =pdflatex
PDF =rapport.pdf
TEX =$(wildcard *.tex)
name=$(PDF:.pdf=)

#  $ make [name=<new_name>]

all: $(PDF)
ifneq ($(name).pdf,$(PDF))
ifneq ($(name),)
	@-cp $(PDF) $(name).pdf
endif
endif

$(PDF): $(TEX)
	$(CC) rapport.tex
	$(CC) rapport.tex

.PHONY: clean mrproper clean_pdf

clean_pdf:
	@-rm -i *.pdf

clean:
	@-rm -f *.aux *.toc *.log *.out *# *~ 

mrproper: clean
	@-rm -f $(PDF)
