package com.legstar.xsd.java;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdToCobolStringResult;

/**
 * Test the Java2Cob API.
 * 
 */
public class Java2CobTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    /**
     * Test the Cultureinfo POJO.
     * 
     * @throws Exception if test fails
     */
    public void testCultureinfo() throws Exception {

        Java2Cob java2cob = new Java2Cob();

        List < String > classNames = Arrays.asList(new String[] {
                "com.legstar.xsdc.test.cases.cultureinfo.CultureInfoRequest",
                "com.legstar.xsdc.test.cases.cultureinfo.CultureInfoReply" });
        XsdToCobolStringResult results = java2cob.translate(classNames);

        check("cultureinfo", "xsd", results.getCobolXsd());
        check("cultureinfo", "cpy", results.getCobolStructure());

    }

    /**
     * Test the JVMQuery POJO.
     * 
     * @throws Exception if test fails
     */
    public void testJVMQuery() throws Exception {

        Java2Cob java2cob = new Java2Cob();
        java2cob.getModel().setNewTargetNamespace(
                "http://jvmquery.cases.test.xsdc.legstar.com/");

        List < String > classNames = Arrays.asList(new String[] {
                "com.legstar.xsdc.test.cases.jvmquery.JVMQueryRequest",
                "com.legstar.xsdc.test.cases.jvmquery.JVMQueryReply" });
        XsdToCobolStringResult results = java2cob.translate(classNames);

        check("jvmquery", "xsd", results.getCobolXsd());
        check("jvmquery", "cpy", results.getCobolStructure());

    }

    public void testDates() throws Exception {

        Java2Cob java2cob = new Java2Cob();
        java2cob.getModel().setNewTargetNamespace(
                "http://datesample.cases.test.xsdc.legstar.com/");

        List < String > classNames = Arrays
                .asList(new String[] { "com.legstar.xsd.java.Java2CobTest$DateSample" });
        XsdToCobolStringResult results = java2cob.translate(classNames);

        check("datesample", "xsd", results.getCobolXsd());
        check("datesample", "cpy", results.getCobolStructure());
    }

    public static class DateSample {
        private Date date;
        private Calendar calendar;

        /**
         * @return the date
         */
        public Date getDate() {
            return date;
        }

        /**
         * @param date the date to set
         */
        public void setDate(Date date) {
            this.date = date;
        }

        /**
         * @return the calendar
         */
        public Calendar getCalendar() {
            return calendar;
        }

        /**
         * @param calendar the calendar to set
         */
        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }
    }

    public void testIssue3() throws Exception {

        Java2Cob java2cob = new Java2Cob();
        java2cob.getModel().setNewTargetNamespace(
                "http://issue3.cases.test.xsdc.legstar.com/");

        List < String > classNames = Arrays.asList(new String[] {
                "com.legstar.xsd.java.Java2CobTest$OuterSample",
                "com.legstar.xsd.java.Java2CobTest$InnerSample" });
        XsdToCobolStringResult results = java2cob.translate(classNames);

        check("outersample", "xsd", results.getCobolXsd());
        check("outersample", "cpy", results.getCobolStructure());
    }

    public static class OuterSample {
        private InnerSample innerSample;

        public InnerSample getInnerSample() {
            return innerSample;
        }

        public void setInnerSample(InnerSample innerSample) {
            this.innerSample = innerSample;
        }
    }

    public static class InnerSample {
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
