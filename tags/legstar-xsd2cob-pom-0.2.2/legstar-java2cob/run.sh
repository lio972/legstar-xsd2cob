#! /bin/sh
##   ---------------------------------------------------------------------------
##   Translate XML schema to COBOL structures 
##   ----------------------------------------
##   type run -h to get help on available options
##   ---------------------------------------------------------------------------

## This is how you would specify which classes to translate to COBOL:
CMD_LINE_ARGS="$@ -i com.legstar.xsdc.test.cases.jvmquery.JVMQueryRequest,com.legstar.xsdc.test.cases.jvmquery.JVMQueryReply"

## This is how you would set the classpath to include your classes:
CMD_LINE_ARGS="$CMD_LINE_ARGS -p java/legstar-test-jvmquery-classes.jar"

##   Use the following to set your own JVM arguments
JVM_ARGS=

##   Update the log4j configuration to set debug mode
JVM_ARGS="$JVM_ARGS -Dlog4j.configuration=file:conf/log4j.properties"

java $JVM_ARGS -jar legstar-java2cob-${project.version}-exe.jar $CMD_LINE_ARGS
