<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:cb="http://www.legsem.com/legstar/xml/cobol-binding-1.0.1.xsd" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb">
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!--  Change the AppID cobol picture clause -->
    <xsl:template match="*/cb:cobolElement[@cobolName='AppID']">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="picture">X(40)</xsl:attribute>
        </xsl:copy>
    </xsl:template>

    <!--  Change the Query cobol picture clause -->
    <xsl:template match="*/cb:cobolElement[@cobolName='Query']">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="picture">X(128)</xsl:attribute>
        </xsl:copy>
    </xsl:template>

    <!--  Change the Description cobol picture clause -->
    <xsl:template match="*/cb:cobolElement[@cobolName='Description']">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="picture">X(256)</xsl:attribute>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>