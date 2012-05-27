Overview:
=========
  The java2cob utility translates Java classes to COBOL structure definitions.
  
  The utility is available as an executable jar or as an ANT task.

Prerequisites:
=============

  You need Java JRE 1.5 or higher.
  
  If you want to use the ANT task, you need ANT version 1.6.5 or higher.

Running the sample using the executable jar:
============================================

  1. Go to the folder where you unzipped the distribution file. It should
     contain run.sh (Linux) and run.bat (Windows) command files.
     The schema sub folder is used as input. It contains an XML schema and
     a WSDL samples.
  2. On Linux you pobably need to type chmod +x run.sh to make it executable
  3. Type ./run.sh (Linux) or run (Windows).
  4. Check the cobol sub folder, a COBOL copybook should have been created.
  5. Check the cobolschema sub folder, a COBOL-annotated XML Schema
     file should have been created.

Troubleshooting the executable jar:
===================================

   The conf sub folder contains a log4j configuration file. Set the debug
   level to get more information on errors.

   Checkout the group discussion list at:
   http://groups.google.com/group/legstar-user.
   
   If you can't find a solution, please file a bug report at:
   http://code.google.com/p/legstar-java2cob/issues/list
   
Running the sample using ANT:
=============================

  1. Go to the folder where you unzipped the distribution file. It should
     contain a build.xml ANT script.
  2. Type ant.
  3. Check the cobol sub folder, an set of COBOL copybooks should have been
     created.
  4. Check the cobolschema sub folder, a set of COBOL-annotated XML Schema
     files should have been created.

Troubleshooting ANT:
===================

   The conf sub folder contains a log4j configuration file. Set the debug
   level to get more information on errors.
   
   You can also run ant with the -v option.
   
   Checkout the group discussion list at:
   http://groups.google.com/group/legstar-user.
   
   If you can't find a solution, please file a report at:
   http://code.google.com/p/legstar-java2cob/issues/list
   
Additional information:
=======================
  
  Check the wiki pages: http://code.google.com/p/legstar-xsd2cob/w/list.
  
  Javadoc: http://www.legsem.com/legstar/legstar-java2cob/legstar-java2cob/apidocs/index.html 
  
  Join the discussion group: http://groups.google.com/group/legstar-user.
