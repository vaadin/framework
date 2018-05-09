package com.vaadin.tests.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SerializerTestTest extends MultiBrowserTest {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss 'GMT'Z yyyy", new Locale("en", "fi"));

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE11 and PhantomJS don't support getting timezone
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testSerialization() {
        // Set up formatting with browsers timezone
        FORMAT.setTimeZone(getBrowserTimeZone());

        openTestURL();
        int logRow = 0;

        assertEquals("sendJson: {\"b\":false,\"s\":\"JSON\"}, null, \"value\"",
                getLogRow(logRow++));
        assertEquals(
                "sendDateArray: January 31, 2013 12:00:00 AM UTC January 31, 2012 12:00:00 AM UTC",
                getLogRow(logRow++));
        assertEquals("sendDate: May 1, 2013 11:12:13 AM UTC",
                getLogRow(logRow++));
        assertEquals("sendDate: January 1, 1970 12:00:00 AM UTC",
                getLogRow(logRow++));
        assertEquals(
                "sendEnum: PREFORMATTED, [HTML, TEXT], [PREFORMATTED, TEXT]",
                getLogRow(logRow++));
        assertEquals(
                "sendWrappedGenerics: {[SimpleTestBean(1)]={1=[SimpleTestBean(42)]}}",
                getLogRow(logRow++));
        assertEquals(
                "sendMap: {a=SimpleTestBean(1)}, [com.vaadin.tests.widgetset.server.SerializerTestExtension=SimpleTestBean(4)], [2=com.vaadin.tests.widgetset.server.SerializerTestExtension], {SimpleTestBean(4)=SimpleTestBean(-4), SimpleTestBean(-5)=SimpleTestBean(5)}",
                getLogRow(logRow++));
        assertEquals(
                "sendSet: [-12, -7, -4], class com.vaadin.tests.serialization.SerializerTest, [SimpleTestBean(2), SimpleTestBean(3)]",
                getLogRow(logRow++));
        assertEquals(
                "sendArrayList: [[2], [2]], [[2, 1], [2, 3]], [[SimpleTestBean(7)]]",
                getLogRow(logRow++));
        assertEquals(
                "sendList: [-234, 5, 8], class com.vaadin.tests.widgetset.server.SerializerTestExtension, class com.vaadin.tests.serialization.SerializerTest, [SimpleTestBean(-568), SimpleTestBean(234)]",
                getLogRow(logRow++));
        assertEquals(
                "sendNestedArray: [[7, 5]], [[SimpleTestBean(2)], [SimpleTestBean(4)]]",
                getLogRow(logRow++));
        assertEquals("sendNull: null, Not null", getLogRow(logRow++));
        assertEquals(
                "sendBean: ComplexTestBean [innerBean1=SimpleTestBean(1), innerBean2=SimpleTestBean(3), innerBeanCollection=[SimpleTestBean(6), SimpleTestBean(0)], privimite=6], SimpleTestBean(0), [SimpleTestBean(7)]",
                getLogRow(logRow++));
        assertEquals(
                "sendConnector: com.vaadin.tests.widgetset.server.SerializerTestExtension",
                getLogRow(logRow++));
        assertEquals("sendString: Taegghiiiinnrsssstt‡, [null, ‡]",
                getLogRow(logRow++));
        assertEquals(
                "sendDouble: 0.423310825130748, 5.859874482048838, [2.0, 1.7976931348623157E308, 4.9E-324]",
                getLogRow(logRow++));
        assertEquals("sendFloat: 1.0000001, 3.14159, [-12.0, 0.0, 57.0]",
                getLogRow(logRow++));
        assertEquals("sendLong: -57841235865, 577431841358, [57, 0]",
                getLogRow(logRow++));
        assertEquals("sendInt: 2, 5, [2147483647, 0]", getLogRow(logRow++));
        assertEquals("sendChar: Å, ∫, [a, b, c, d]", getLogRow(logRow++));
        assertEquals("sendByte: 5, -12, [3, 1, 2]", getLogRow(logRow++));
        assertEquals(
                "sendBoolean: false, false, [false, false, true, false, true, true]",
                getLogRow(logRow++));
        assertEquals("sendBeanSubclass: 43", getLogRow(logRow++));

        // Dates from state
        Date date1 = new Date(1);
        Date date2 = new Date(Date.UTC(2013 - 1900, 4, 1, 11, 12, 13));
        Date[] dateArray = new Date[] { new Date(1), new Date(2) };

        assertEquals(
                "state.dateArray: " + Arrays.stream(dateArray)
                        .map(this::formatDate).collect(Collectors.joining(" ")),
                getLogRow(logRow++));
        assertEquals("state.date2: " + formatDate(date2), getLogRow(logRow++));
        assertEquals("state.date1: " + formatDate(date1), getLogRow(logRow++));

        assertEquals("state.jsonBoolean: false", getLogRow(logRow++));
        assertEquals("state.jsonString: a string", getLogRow(logRow++));
        assertEquals("state.jsonNull: NULL", getLogRow(logRow++));
        assertEquals("state.stringArray: [null, ‡]", getLogRow(logRow++));
        assertEquals("state.string: This is a tesing string ‡",
                getLogRow(logRow++));
        assertEquals("state.doubleArray: [1.7976931348623157e+308, 5e-324]",
                getLogRow(logRow++));
        assertEquals("state.doubleObjectValue: -2.718281828459045",
                getLogRow(logRow++));
        assertEquals("state.doubleValue: 3.141592653589793",
                getLogRow(logRow++));
        assertEquals("state.floatArray: [57, 0, -12]", getLogRow(logRow++));
        assertTrue(getLogRow(logRow++)
                .startsWith("state.floatObjectValue: 1.0000001"));
        assertTrue(getLogRow(logRow++).startsWith("state.floatValue: 3.14159"));
        assertEquals("state.longArray: [-57841235865, 57]",
                getLogRow(logRow++));
        assertEquals("state.longObjectValue: 577431841360",
                getLogRow(logRow++));
        assertEquals("state.longValue: 577431841359", getLogRow(logRow++));
        assertEquals("state.intArray: [5, 7]", getLogRow(logRow++));
        assertEquals("state.intObjectValue: 42", getLogRow(logRow++));
        assertEquals("state.intValue: 2147483647", getLogRow(logRow++));
        assertEquals("state.charArray: aBcD", getLogRow(logRow++));
        assertEquals("state.charObjectValue: å", getLogRow(logRow++));
        assertEquals("state.charValue: ∫", getLogRow(logRow++));
        assertEquals("state.byteArray: [3, 1, 2]", getLogRow(logRow++));
        assertEquals("state.byteObjectValue: -12", getLogRow(logRow++));
        assertEquals("state.byteValue: 5", getLogRow(logRow++));
        assertEquals(
                "state.booleanArray: [true, true, false, true, false, false]",
                getLogRow(logRow++));
    }

    private TimeZone getBrowserTimeZone() {
        // Ask TimeZone from browser
        String browserTimeZone = ((JavascriptExecutor) getDriver())
                .executeScript(
                        "return Intl.DateTimeFormat().resolvedOptions().timeZone;")
                .toString();
        return TimeZone.getTimeZone(browserTimeZone);
    }

    private String formatDate(Date date) {
        // JavaScript formatting drops leading 0 from offset
        return FORMAT.format(date).replaceAll("(GMT[+-])0", "$1");
    }
}
