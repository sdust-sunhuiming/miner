#!/bin/sh

###
## ProM specific
###
PROGRAM=ProM
CP=./dist/ProM-Framework.jar:./dist/ProM-Contexts.jar:./dist/ProM-Models.jar:./dist/ProM-Plugins.jar
LIBDIR=./lib
MAIN=org.processmining.contexts.uitopia.UI

####
## Environment options
###
JAVA=java

###
## Main program
###

add() {
	CP=${CP}:$1
}


for lib in $LIBDIR/*.jar
do
	add $lib
done

$JAVA -classpath ${CP} -Djava.library.path=${LIBDIR} -Xmx1G -XX:MaxPermSize=256m ${MAIN}
