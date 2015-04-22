# Overview #

legstar-xsd2cob is a standalone XML Schema to COBOL Structure translator written in Java. It also provides Java to COBOL Structure translation.

Starting from XML schema, a WSDL file or Java classes, the tool generates 2 outputs:

  * A COBOL-annotated XML Schema (Used as input by other LegStar modules)

  * A COBOL copybook containing a structure definition that matches the input morphology

legstar-xsd2cob is open source and available for [download](https://code.google.com/p/legstar-xsd2cob/wiki/Download) as a zip archive.

There are 3 ways you can use it:

  * [Using the executable jar](#Using_the_executable_jar.md)

  * [Using the Apache ANT Task](#Using_the_Apache_ANT_Task.md)

  * [Calling the API from your own Java code](#Calling_the_API_from_your_own_Java_code.md)

# Using the executable jar #

This is the easiest way to invoke the utility.

The distribution comes with two sets of **run.sh** and **run.bat** command files, one for XML Schema translation, the other one for Java translation.

## XML Schema translation ##

The command files are located under the xsd2cob folder.

By default, run picks up XML Schema and WSDL files from a folder called "schema" and places the results in 2 folders, "cobolschema" and "cobol".

The -h option lists the parameters that you might want to set.

The executable jar is called legstar-xsd2cob-x.y.z-exe.jar where x.y.z is the version number. It depends on the lib and conf subfolders content.

You invoke such an executable the classical way (run.sh is an example of such a call):
```
java -jar legstar-xsd2cob-x.y.z-exe.jar -i schema -ox cobolschema -oc cobol
```

The input can be a folder, in which case all files from that folder are processed, or a URI.

The outputs can be folders, in which case the actual output files will be named after the input URI, or files.

## Java translation ##

The command files are located under the java2cob folder.

There are no default inputs in the case of java2cob. run.sh and run.bat contain hardcoded references to samples provided. You will need to edit these command files in order to translate your own java classes.

By default java2cob places the results in 2 folders, "cobolschema" and "cobol".

The -h option lists the parameters that you might want to set.

The executable jar is called legstar-java2cob-x.y.z-exe.jar where x.y.z is the version number. It depends on the lib and conf subfolders content.

You invoke such an executable the classical way (run.sh is an example of such a call):
```
java -jar legstar-java2cob-x.y.z-exe.jar -i a.b.c.Class1,a.b.c.Class2 -p mylib/abc.jar -ox cobolschema -oc cobol
```

The input is a comma-separated list of fully qualified Java class names (-i) as well as path locations, a comma separated list of jar files, where your classes are archived (-p).

The outputs can be folders, in which case the actual output files will be named after the input package name or Java Class name.

# Using the Apache ANT Task #

## XML Schema translation ##

The sample ant script is located under the xsd2cob folder.

The distribution comes with a **build.xml** which shows how to invoke the ant task.

If you need to set additional parameters, all options from [Xsd2CobModel](http://www.legsem.com/legstar/legstar-xsd2cob-pom/legstar-xsd2cob/apidocs/com/legstar/xsd/def/Xsd2CobModel.html) are available.

The rule is that the ant property name does not need the initial "set" prefix and should start
with a lowercase character. So for instance:
```
setInputXsdUri becomes inputXsdUri
```

## Java translation ##

The sample ant script is located under the java2cob folder.

The distribution comes with a **build.xml** which shows how to invoke the ant task.

If you need to set additional parameters, all options from [Java2CobModel](http://www.legsem.com/legstar/legstar-xsd2cob-pom/legstar-java2cob/apidocs/com/legstar/xsd/java/Java2CobModel.html) are available.

# Calling the API from your own Java code #

## XML Schema translation ##

The distribution xsd2cob/lib folder contains the jars that you might need to add to your classpath.

legstar-xsd2cob-x.y.z.jar is the major one.

This is a snippet of code that shows a typical use of the xsd2cob api:
```
            Xsd2Cob xsd2cob = new Xsd2Cob();
            xsd2cob.getModel()
                    .setInputXsdUri(
                            new URI(
                                    "http://localhost:8080/legstar-test-cultureinfo/getinfo?wsdl"));
            XsdToCobolStringResult results = xsd2cob.translate();
```

## Java translation ##

The distribution java2cob/lib folder contains the jars that you might need to add to your classpath.

legstar-java2cob-x.y.z.jar is the major one.

This is a snippet of code that shows a typical use of the java2cob api:
```
        Java2Cob java2cob = new Java2Cob();
        java2cob.getModel().setNewTargetNamespace(
                "http://jvmquery.cases.test.xsdc.legstar.com/");

        List < String > classNames = Arrays.asList(new String[] {
                "com.legstar.xsdc.test.cases.jvmquery.JVMQueryRequest",
                "com.legstar.xsdc.test.cases.jvmquery.JVMQueryReply" });
        XsdToCobolStringResult results = java2cob.translate(classNames);
```

# More info #

Javadocs available at:

  * [xsd2cob](http://www.legsem.com/legstar/legstar-xsd2cob-pom/legstar-xsd2cob/apidocs/index.html)

  * [java2cob](http://www.legsem.com/legstar/legstar-xsd2cob-pom/legstar-java2cob/apidocs/index.html)

Join us on the [LegStar discussion group](http://groups.google.com/group/legstar-user).