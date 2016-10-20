JFLAGS = -d . 
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Utils.java \
        Report.java \
        cppcheckWRP.java \
        inferWRP.java \
        cbmcWRP.java \
        StaticAnalysis.java \
        ModelChecking.java \
        CodeReviewProcess.java \
        Framework.java

default: classes engine

all: classes engine

classes: $(CLASSES:.java=.class)

engine: 
	javac engine.java 

clean:
	rm -rf etc && rm -f engine.class && rm -rf TEMP

cleanLib:
	rm -rf etc

cleanEng:
	rm -f engine.class

cleanTemp:
	rm -rf TEMP

