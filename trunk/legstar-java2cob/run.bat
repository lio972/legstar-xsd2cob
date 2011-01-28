@echo off
rem ---------------------------------------------------------------------------
rem Translate XML schema to COBOL structures
rem ----------------------------------------
rem type run -h to get help on available options
rem ---------------------------------------------------------------------------

set CMD_LINE_ARGS=

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
