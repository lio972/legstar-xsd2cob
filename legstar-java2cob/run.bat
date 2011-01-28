@echo off
rem ---------------------------------------------------------------------------
rem Translate Java classes to COBOL structures
rem ----------------------------------------
rem type run -h to get help on available options
rem ---------------------------------------------------------------------------

set CMD_LINE_ARGS=

rem This is how you would set the classpath to include your classes:
set CMD_LINE_ARGS=%CMD_LINE_ARGS% -p java/legstar-test-jvmquery-classes.jar

rem This is how you would specify which classes to translate to COBOL:
set CMD_LINE_ARGS=%CMD_LINE_ARGS% -i com.legstar.xsdc.test.cases.jvmquery.JVMQueryRequest,com.legstar.xsdc.test.cases.jvmquery.JVMQueryReply

:setupArgs
if %1a==a goto doneStart
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart

rem Use the following to set your own JVM arguments
set JVM_ARGS=

rem Update the log4j configuration to set debug mode
set JVM_ARGS=%JVM_ARGS% -Dlog4j.configuration=file:conf/log4j.properties

java %JVM_ARGS% -jar legstar-java2cob-${project.version}-exe.jar %CMD_LINE_ARGS%
