#!/usr/make
#
# Makefile for jlemon
#

# The toplevel directory of the source tree.  This is the directory
# that contains this "Makefile".
#
TOP = .

# The directory where parser is created.
#
GEN = $(TOP)/src/main/java

# C Compiler and options for use in building executables that
# will run on the platform that is doing the build.
#
BCC = gcc -g -O2

# Filename extensions
#
BEXE =

# This is the default Makefile target.  The objects listed here
# are what get build when you type just "make" with no arguments.
#
all: jlemon$(EXE) yyParser.java

# Rules to build the LEMON compiler generator
#
jlemon$(BEXE): $(TOP)/lemon.c $(TOP)/lempar.c
	$(BCC) -o $@ $(TOP)/lemon.c

# Rules to build yyParser.java and TokenType.java - the outputs of jlemon.
#
TokenType.java: yyParser.java
yyParser.java: jlemon$(BEXE) $(TOP)/src/main/java/org/sqlite/parser/parse.y
	./jlemon$(BEXE) -DSQLITE_ENABLE_UPDATE_DELETE_LIMIT $(TOP)/src/main/java/org/sqlite/parser/parse.y
	cpp -P $(TOP)/src/main/java/org/sqlite/parser/parse.j > $(GEN)/org/sqlite/parser/yyParser.java

simple: jlemon$(BEXE)
	./jlemon $(TOP)/src/test/java/simple/parser.y
	cpp -P src/test/java/simple/parser.j > src/test/java/simple/yyParser.java

clean:
	-rm -rf jlemon.dSYM
	-rm -f jlemon$(BEXE)
#	rm -f $(GEN)/org/sqlite/parser/yyParser.java
	-rm -f $(TOP)/src/main/java/org/sqlite/parser/*.h
	-rm -f $(TOP)/src/main/java/org/sqlite/parser/*.j
	-rm -f $(TOP)/src/main/java/org/sqlite/parser/*.out
#	rm -f $(GEN)/simple/yyParser.java
	-rm -f $(TOP)/src/test/java/simple/*.h
	-rm -f $(TOP)/src/test/java/simple/*.j
	-rm -f $(TOP)/src/test/java/simple/*.out