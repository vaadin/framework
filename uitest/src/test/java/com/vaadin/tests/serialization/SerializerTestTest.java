/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.serialization;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SerializerTestTest extends MultiBrowserTest {

    @Test
    public void testSerialization() {
        openTestURL();
        int logRow = 0;

        Assert.assertEquals(
                "sendJson: {\"b\":false,\"s\":\"JSON\"}, null, \"value\"",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendDateArray: January 31, 2013 10:00:00 PM UTC January 31, 2012 10:00:00 PM UTC",
                getLogRow(logRow++));
        Assert.assertEquals("sendDate: May 31, 2013 8:12:13 AM UTC",
                getLogRow(logRow++));
        Assert.assertEquals("sendDate: January 1, 1970 12:00:00 AM UTC",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendEnum: PREFORMATTED, [HTML, RAW], [PREFORMATTED, XML]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendWrappedGenerics: {[SimpleTestBean(1)]={1=[SimpleTestBean(42)]}}",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendMap: {a=SimpleTestBean(1)}, [com.vaadin.tests.widgetset.server.SerializerTestExtension=SimpleTestBean(4)], [2=com.vaadin.tests.widgetset.server.SerializerTestExtension], {SimpleTestBean(4)=SimpleTestBean(-4), SimpleTestBean(-5)=SimpleTestBean(5)}",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendSet: [-12, -7, -4], class com.vaadin.tests.serialization.SerializerTest, [SimpleTestBean(2), SimpleTestBean(3)]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendArrayList: [[2], [2]], [[2, 1], [2, 3]], [[SimpleTestBean(7)]]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendList: [-234, 5, 8], class com.vaadin.tests.widgetset.server.SerializerTestExtension, class com.vaadin.tests.serialization.SerializerTest, [SimpleTestBean(-568), SimpleTestBean(234)]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendNestedArray: [[7, 5]], [[SimpleTestBean(2)], [SimpleTestBean(4)]]",
                getLogRow(logRow++));
        Assert.assertEquals("sendNull: null, Not null", getLogRow(logRow++));
        Assert.assertEquals(
                "sendBean: ComplexTestBean [innerBean1=SimpleTestBean(1), innerBean2=SimpleTestBean(3), innerBeanCollection=[SimpleTestBean(6), SimpleTestBean(0)], privimite=6], SimpleTestBean(0), [SimpleTestBean(7)]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendConnector: com.vaadin.tests.widgetset.server.SerializerTestExtension",
                getLogRow(logRow++));
        Assert.assertEquals("sendString: Taegghiiiinnrsssstt‡, [null, ‡]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendDouble: 0.423310825130748, 5.859874482048838, [2.0, 1.7976931348623157E308, 4.9E-324]",
                getLogRow(logRow++));
        Assert.assertEquals(
                "sendFloat: 1.0000001, 3.14159, [-12.0, 0.0, 57.0]",
                getLogRow(logRow++));
        Assert.assertEquals("sendLong: -57841235865, 577431841358, [57, 0]",
                getLogRow(logRow++));
        Assert.assertEquals("sendInt: 2, 5, [2147483647, 0]",
                getLogRow(logRow++));
        Assert.assertEquals("sendChar: Å, ∫, [a, b, c, d]", getLogRow(logRow++));
        Assert.assertEquals("sendByte: 5, -12, [3, 1, 2]", getLogRow(logRow++));
        Assert.assertEquals(
                "sendBoolean: false, false, [false, false, true, false, true, true]",
                getLogRow(logRow++));
        Assert.assertEquals("sendBeanSubclass: 43", getLogRow(logRow++));
        Assert.assertEquals(
                "state.dateArray: Thu Jan 01 02:00:00 GMT+200 1970 Thu Jan 01 02:00:00 GMT+200 1970",
                getLogRow(logRow++));
        Assert.assertEquals("state.date2: Fri May 31 11:12:13 GMT+300 2013",
                getLogRow(logRow++));
        Assert.assertEquals("state.date1: Thu Jan 01 02:00:00 GMT+200 1970",
                getLogRow(logRow++));
        Assert.assertEquals("state.jsonBoolean: false", getLogRow(logRow++));
        Assert.assertEquals("state.jsonString: a string", getLogRow(logRow++));
        Assert.assertEquals("state.jsonNull: NULL", getLogRow(logRow++));
        Assert.assertEquals("state.stringArray: [null, ‡]", getLogRow(logRow++));
        Assert.assertEquals("state.string: This is a tesing string ‡",
                getLogRow(logRow++));
        Assert.assertEquals(
                "state.doubleArray: [1.7976931348623157e+308, 5e-324]",
                getLogRow(logRow++));
        Assert.assertEquals("state.doubleObjectValue: -2.718281828459045",
                getLogRow(logRow++));
        Assert.assertEquals("state.doubleValue: 3.141592653589793",
                getLogRow(logRow++));
        Assert.assertEquals("state.floatArray: [57, 0, -12]",
                getLogRow(logRow++));
        Assert.assertTrue(getLogRow(logRow++).startsWith(
                "state.floatObjectValue: 1.0000001"));
        Assert.assertTrue(getLogRow(logRow++).startsWith(
                "state.floatValue: 3.14159"));
        Assert.assertEquals("state.longArray: [-57841235865, 57]",
                getLogRow(logRow++));
        Assert.assertEquals("state.longObjectValue: 577431841360",
                getLogRow(logRow++));
        Assert.assertEquals("state.longValue: 577431841359",
                getLogRow(logRow++));
        Assert.assertEquals("state.intArray: [5, 7]", getLogRow(logRow++));
        Assert.assertEquals("state.intObjectValue: 42", getLogRow(logRow++));
        Assert.assertEquals("state.intValue: 2147483647", getLogRow(logRow++));
        Assert.assertEquals("state.charArray: aBcD", getLogRow(logRow++));
        Assert.assertEquals("state.charObjectValue: å", getLogRow(logRow++));
        Assert.assertEquals("state.charValue: ∫", getLogRow(logRow++));
        Assert.assertEquals("state.byteArray: [3, 1, 2]", getLogRow(logRow++));
        Assert.assertEquals("state.byteObjectValue: -12", getLogRow(logRow++));
        Assert.assertEquals("state.byteValue: 5", getLogRow(logRow++));
        Assert.assertEquals(
                "state.booleanArray: [true, true, false, true, false, false]",
                getLogRow(logRow++));

    }
}
