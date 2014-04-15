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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.widgetset.client.ComplexTestBean;
import com.vaadin.tests.widgetset.client.SerializerTestRpc;
import com.vaadin.tests.widgetset.client.SimpleTestBean;
import com.vaadin.tests.widgetset.server.SerializerTestExtension;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class SerializerTest extends AbstractTestUI {

    private Log log = new Log(40);

    @Override
    protected void setup(VaadinRequest request) {
        final SerializerTestExtension testExtension = new SerializerTestExtension();
        addExtension(testExtension);

        // Don't show row numbers to make it easier to add tests without
        // changing all numbers
        log.setNumberLogRows(false);
        addComponent(log);

        SerializerTestRpc rpc = testExtension
                .getRpcProxy(SerializerTestRpc.class);
        rpc.sendBeanSubclass(new SimpleTestBean() {
            @Override
            public int getValue() {
                return 42;
            }
        });
        rpc.sendBoolean(true, Boolean.FALSE, new boolean[] { true, true, false,
                true, false, false });
        rpc.sendByte((byte) 5, Byte.valueOf((byte) -12), new byte[] { 3, 1, 2 });
        rpc.sendChar('\u222b', Character.valueOf('å'), "aBcD".toCharArray());
        rpc.sendInt(Integer.MAX_VALUE, Integer.valueOf(0), new int[] { 5, 7 });
        rpc.sendLong(577431841358l, Long.valueOf(0), new long[] {
                -57841235865l, 57 });
        rpc.sendFloat(3.14159f, Float.valueOf(Math.nextUp(1)), new float[] {
                57, 0, -12 });
        rpc.sendDouble(Math.PI, Double.valueOf(-Math.E), new double[] {
                Double.MAX_VALUE, Double.MIN_VALUE });
        rpc.sendString("This is a tesing string ‡");
        rpc.sendConnector(this);
        rpc.sendBean(
                new ComplexTestBean(new SimpleTestBean(0),
                        new SimpleTestBean(1), Arrays.asList(
                                new SimpleTestBean(3), new SimpleTestBean(4)),
                        5), new SimpleTestBean(6),
                new SimpleTestBean[] { new SimpleTestBean(7) });
        rpc.sendNull("Not null", null);
        rpc.sendNestedArray(new int[][] { { 5 }, { 7 } },
                new SimpleTestBean[][] { { new SimpleTestBean(4),
                        new SimpleTestBean(2) } });
        rpc.sendList(Arrays.asList(5, 8, -234), Arrays.<Connector> asList(this,
                testExtension), Arrays.asList(new SimpleTestBean(234),
                new SimpleTestBean(-568)));
        rpc.sendArrayList(
                Arrays.asList(new int[] { 1, 2 }, new int[] { 3, 4 }),
                Arrays.asList(new Integer[] { 5, 6 }, new Integer[] { 7, 8 }),
                Collections
                        .singletonList(new SimpleTestBean[] { new SimpleTestBean(
                                7) }));
        // Disabled because of #8861
        // rpc.sendListArray(
        // new List[] { Arrays.asList(1, 2), Arrays.asList(3, 4) },
        // new List[] { Collections.singletonList(new SimpleTestBean(-1)) });
        rpc.sendSet(new HashSet<Integer>(Arrays.asList(4, 7, 12)), Collections
                .singleton((Connector) this), new HashSet<SimpleTestBean>(
                Arrays.asList(new SimpleTestBean(1), new SimpleTestBean(2))));

        rpc.sendMap(new HashMap<String, SimpleTestBean>() {
            {
                put("1", new SimpleTestBean(1));
                put("2", new SimpleTestBean(2));
            }
        }, new HashMap<Connector, SimpleTestBean>() {
            {
                put(testExtension, new SimpleTestBean(3));
                put(getUI(), new SimpleTestBean(4));
            }
        }, new HashMap<Integer, Connector>() {
            {
                put(5, testExtension);
                put(10, getUI());
            }
        }, new HashMap<SimpleTestBean, SimpleTestBean>() {
            {
                put(new SimpleTestBean(5), new SimpleTestBean(-5));
                put(new SimpleTestBean(-4), new SimpleTestBean(4));
            }
        });
        rpc.sendWrappedGenerics(new HashMap<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>>() {
            {
                put(Collections.singleton(new SimpleTestBean(42)),
                        new HashMap<Integer, List<SimpleTestBean>>() {
                            {
                                put(1, Arrays.asList(new SimpleTestBean(1),
                                        new SimpleTestBean(3)));
                            }
                        });
            }
        });

        rpc.sendEnum(ContentMode.TEXT, new ContentMode[] {
                ContentMode.PREFORMATTED, ContentMode.XML },
                Arrays.asList(ContentMode.HTML, ContentMode.RAW));

        testExtension.registerRpc(new SerializerTestRpc() {
            @Override
            public void sendBoolean(boolean value, Boolean boxedValue,
                    boolean[] array) {
                log.log("sendBoolean: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendByte(byte value, Byte boxedValue, byte[] array) {
                log.log("sendByte: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendChar(char value, Character boxedValue, char[] array) {
                log.log("sendChar: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendInt(int value, Integer boxedValue, int[] array) {
                log.log("sendInt: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendLong(long value, Long boxedValue, long[] array) {
                log.log("sendLong: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendFloat(float value, Float boxedValue, float[] array) {
                log.log("sendFloat: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendDouble(double value, Double boxedValue,
                    double[] array) {
                log.log("sendDouble: " + value + ", " + boxedValue + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendString(String value) {
                log.log("sendString: " + value);
            }

            @Override
            public void sendConnector(Connector connector) {
                log.log("sendConnector: " + connector.getClass().getName());
            }

            @Override
            public void sendBean(ComplexTestBean complexBean,
                    SimpleTestBean simpleBean, SimpleTestBean[] array) {
                log.log("sendBean: " + complexBean + ", " + simpleBean + ", "
                        + Arrays.toString(array));
            }

            @Override
            public void sendNull(String value1, String value2) {
                log.log("sendNull: " + value1 + ", " + value2);
            }

            @Override
            public void sendNestedArray(int[][] nestedIntArray,
                    SimpleTestBean[][] nestedBeanArray) {
                log.log("sendNestedArray: "
                        + Arrays.deepToString(nestedIntArray) + ", "
                        + Arrays.deepToString(nestedBeanArray));
            }

            @Override
            public void sendList(List<Integer> intList,
                    List<Connector> connectorList, List<SimpleTestBean> beanList) {
                log.log("sendList: " + intList + ", "
                        + connectorCollectionToString(connectorList) + ", "
                        + beanList);
            }

            private String connectorCollectionToString(
                    Collection<Connector> collection) {
                StringBuilder sb = new StringBuilder();

                for (Connector connector : collection) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(connector.getClass());
                }

                String string = sb.toString();
                return string;
            }

            @Override
            public void sendArrayList(List<int[]> primitiveArrayList,
                    List<Integer[]> objectArrayList,
                    List<SimpleTestBean[]> beanArrayList) {
                log.log("sendArrayList: "
                        + Arrays.deepToString(primitiveArrayList.toArray())
                        + ", " + Arrays.deepToString(objectArrayList.toArray())
                        + ", " + Arrays.deepToString(beanArrayList.toArray()));
            }

            @Override
            public void sendListArray(List<Integer>[] objectListArray,
                    List<SimpleTestBean>[] beanListArray) {
                log.log("sendArrayList: " + Arrays.toString(objectListArray)
                        + ", " + Arrays.toString(beanListArray));
            }

            @Override
            public void sendSet(Set<Integer> intSet,
                    Set<Connector> connectorSet, Set<SimpleTestBean> beanSet) {
                log.log("sendSet: " + intSet + ", "
                        + connectorCollectionToString(connectorSet) + ", "
                        + beanSet);
            }

            @Override
            public void sendMap(Map<String, SimpleTestBean> stringMap,
                    Map<Connector, SimpleTestBean> connectorMap,
                    Map<Integer, Connector> intMap,
                    Map<SimpleTestBean, SimpleTestBean> beanMap) {
                StringBuilder sb = new StringBuilder();
                for (Entry<Connector, SimpleTestBean> entry : connectorMap
                        .entrySet()) {
                    if (sb.length() == 0) {
                        sb.append('[');
                    } else {
                        sb.append(", ");
                    }
                    sb.append(entry.getKey().getClass().getName());
                    sb.append('=');
                    sb.append(entry.getValue());
                }
                sb.append(']');
                String connectorMapString = sb.toString();

                sb = new StringBuilder();
                for (Entry<Integer, Connector> entry : intMap.entrySet()) {
                    if (sb.length() == 0) {
                        sb.append('[');
                    } else {
                        sb.append(", ");
                    }
                    sb.append(entry.getKey());
                    sb.append('=');
                    sb.append(entry.getValue().getClass().getName());
                }
                sb.append(']');
                String intMapString = sb.toString();

                log.log("sendMap: " + stringMap + ", " + connectorMapString
                        + ", " + intMapString + ", " + beanMap);
            }

            @Override
            public void sendWrappedGenerics(
                    Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>> generics) {
                log.log("sendWrappedGenerics: " + generics.toString());
            }

            @Override
            public void sendEnum(ContentMode contentMode, ContentMode[] array,
                    List<ContentMode> list) {
                log.log("sendEnum: " + contentMode + ", "
                        + Arrays.toString(array) + ", " + list);
            }

            @Override
            public void sendBeanSubclass(SimpleTestBean bean) {
                log.log("sendBeanSubclass: " + bean.getValue());
            }

        });
    }

    @Override
    protected String getTestDescription() {
        return "Test for lots of different cases of encoding and decoding variuos data types";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8655);
    }

}
