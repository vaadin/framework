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

package com.vaadin.tests.widgetset.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.widgetset.server.SerializerTestExtension;

@Connect(SerializerTestExtension.class)
public class SerializerTestConnector extends AbstractExtensionConnector {

    private SerializerTestRpc rpc = getRpcProxy(SerializerTestRpc.class);

    public SerializerTestConnector() {
        registerRpc(SerializerTestRpc.class, new SerializerTestRpc() {
            @Override
            public void sendWrappedGenerics(
                    Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>> generics) {
                Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>> updated = new HashMap<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>>();

                SimpleTestBean firstValue = generics.values().iterator().next()
                        .get(Integer.valueOf(1)).get(0);
                Set<SimpleTestBean> key = new HashSet<SimpleTestBean>(Arrays
                        .asList(firstValue));

                Map<Integer, List<SimpleTestBean>> value = new HashMap<Integer, List<SimpleTestBean>>();
                Set<SimpleTestBean> firstKeyValue = generics.keySet()
                        .iterator().next();
                value.put(Integer.valueOf(1), new ArrayList<SimpleTestBean>(
                        firstKeyValue));

                updated.put(key, value);

                rpc.sendWrappedGenerics(updated);
            }

            @Override
            public void sendString(String value) {
                char[] chars = value.toCharArray();
                Arrays.sort(chars);
                rpc.sendString(new String(chars));
            }

            @Override
            public void sendSet(Set<Integer> intSet,
                    Set<Connector> connectorSet, Set<SimpleTestBean> beanSet) {

                beanSet.iterator().next().setValue(intSet.size());
                Set<Integer> updatedIntSet = new HashSet<Integer>();

                for (Integer integer : intSet) {
                    updatedIntSet.add(Integer.valueOf(-integer.intValue()));
                }
                rpc.sendSet(updatedIntSet,
                        Collections.singleton(getUIConnector()), beanSet);
            }

            @Override
            public void sendNestedArray(int[][] nestedIntArray,
                    SimpleTestBean[][] nestedBeanArray) {
                rpc.sendNestedArray(new int[][] { { nestedIntArray[1][0],
                        nestedIntArray[0][0] } }, new SimpleTestBean[][] {
                        { nestedBeanArray[0][1] }, { nestedBeanArray[0][0] } });
            }

            @Override
            public void sendMap(Map<String, SimpleTestBean> stringMap,
                    Map<Connector, SimpleTestBean> connectorMap,
                    Map<Integer, Connector> intMap,
                    Map<SimpleTestBean, SimpleTestBean> beanMap) {
                Map<SimpleTestBean, SimpleTestBean> updatedBeanMap = new HashMap<SimpleTestBean, SimpleTestBean>();
                for (Entry<SimpleTestBean, SimpleTestBean> entry : beanMap
                        .entrySet()) {
                    updatedBeanMap.put(entry.getValue(), entry.getKey());
                }

                rpc.sendMap(Collections.singletonMap("a", stringMap.get("1")),
                        Collections.singletonMap(getThisConnector(),
                                connectorMap.get(getUIConnector())),
                        Collections.singletonMap(
                                Integer.valueOf(stringMap.size()),
                                getThisConnector()), updatedBeanMap);
            }

            @Override
            public void sendLong(long value, Long boxedValue, long[] array) {
                rpc.sendLong(array[0], Long.valueOf(value), new long[] {
                        array[1], boxedValue.longValue() });
            }

            @Override
            public void sendList(List<Integer> intList,
                    List<Connector> connectorList, List<SimpleTestBean> beanList) {
                Collections.sort(intList);
                Collections.reverse(beanList);
                rpc.sendList(intList,
                        Arrays.asList(getThisConnector(), getUIConnector()),
                        beanList);
            }

            @Override
            public void sendInt(int value, Integer boxedValue, int[] array) {
                rpc.sendInt(array.length, Integer.valueOf(array[0]), new int[] {
                        value, boxedValue.intValue() });
            }

            @Override
            public void sendFloat(float value, Float boxedValue, float[] array) {
                Arrays.sort(array);
                rpc.sendFloat(boxedValue.floatValue(), Float.valueOf(value),
                        array);
            }

            @Override
            public void sendDouble(double value, Double boxedValue,
                    double[] array) {
                rpc.sendDouble(value + boxedValue.doubleValue(),
                        Double.valueOf(value - boxedValue.doubleValue()),
                        new double[] { array.length, array[0], array[1] });
            }

            @Override
            public void sendConnector(Connector connector) {
                rpc.sendConnector(getThisConnector());
            }

            @Override
            public void sendChar(char value, Character boxedValue, char[] array) {
                rpc.sendChar(Character.toUpperCase(boxedValue.charValue()),
                        Character.valueOf(value), new String(array)
                                .toLowerCase().toCharArray());
            }

            @Override
            public void sendByte(byte value, Byte boxedValue, byte[] array) {
                // There will most certainly be a bug that is not discovered
                // because this particular method doesn't do anything with it's
                // values...
                rpc.sendByte(value, boxedValue, array);
            }

            @Override
            public void sendBoolean(boolean value, Boolean boxedValue,
                    boolean[] array) {
                boolean[] inverseArray = new boolean[array.length];
                for (int i = 0; i < array.length; i++) {
                    inverseArray[i] = !array[i];
                }
                rpc.sendBoolean(boxedValue == Boolean.TRUE,
                        Boolean.valueOf(!value), inverseArray);
            }

            @Override
            public void sendBean(ComplexTestBean complexBean,
                    SimpleTestBean simpleBean, SimpleTestBean[] array) {
                SimpleTestBean updatedSimpleBean = new SimpleTestBean();
                updatedSimpleBean.setValue(complexBean.getInnerBean1()
                        .getValue());

                ComplexTestBean updatedComplexBean = new ComplexTestBean();
                updatedComplexBean.setInnerBean1(complexBean.getInnerBean2());
                updatedComplexBean.setInnerBean2(complexBean
                        .getInnerBeanCollection().get(0));
                updatedComplexBean.setInnerBeanCollection(Arrays.asList(
                        simpleBean, updatedSimpleBean));
                updatedComplexBean.setPrivimite(complexBean.getPrivimite() + 1);

                ArrayList<SimpleTestBean> arrayList = new ArrayList<SimpleTestBean>(
                        Arrays.asList(array));
                Collections.reverse(arrayList);

                rpc.sendBean(updatedComplexBean, updatedSimpleBean,
                        arrayList.toArray(new SimpleTestBean[array.length]));
            }

            @Override
            public void sendArrayList(List<int[]> primitiveArrayList,
                    List<Integer[]> objectArrayList,
                    List<SimpleTestBean[]> beanArrayList) {
                Collections.reverse(beanArrayList);
                List<Integer[]> updatedObjectArrayList = new ArrayList<Integer[]>();
                for (int[] array : primitiveArrayList) {
                    updatedObjectArrayList.add(new Integer[] {
                            Integer.valueOf(array.length),
                            Integer.valueOf(array[0]) });
                }

                rpc.sendArrayList(Arrays.asList(
                        new int[] { primitiveArrayList.size() },
                        new int[] { objectArrayList.get(0).length }),
                        updatedObjectArrayList, beanArrayList);
            }

            @Override
            public void sendNull(String value1, String value2) {
                rpc.sendNull(value2, value1);
            }

            @Override
            public void sendListArray(List<Integer>[] objectListArray,
                    List<SimpleTestBean>[] beanListArray) {
                rpc.sendListArray(new List[] { objectListArray[1],
                        objectListArray[0] }, new List[] { Collections
                        .singletonList(beanListArray[0].get(0)) });
            }

            @Override
            public void sendEnum(ContentMode contentMode, ContentMode[] array,
                    List<ContentMode> list) {
                ContentMode nextContentMode = ContentMode.values()[contentMode
                        .ordinal() + 1];
                rpc.sendEnum(nextContentMode,
                        list.toArray(new ContentMode[list.size()]),
                        Arrays.asList(array));
            }

            @Override
            public void sendBeanSubclass(final SimpleTestBean bean) {
                rpc.sendBeanSubclass(new SimpleTestBean() {
                    @Override
                    public int getValue() {
                        return bean.getValue() + 1;
                    }
                });
            }

            @Override
            public void sendDate(Date date) {
                rpc.sendDate(date);
            }

            @Override
            public void log(String message) {
                // Do nothing, used only in the other direction
            }
        });
    }

    private Connector getUIConnector() {
        return getConnection().getUIConnector();
    }

    private Connector getThisConnector() {
        // Cast to Connector for use in e.g. Collections.singleton() to get a
        // Set<Connector>
        return this;
    }

    @Override
    public SerializerTestState getState() {
        return (SerializerTestState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        rpc.log("state.booleanValue: " + getState().booleanValue);
        rpc.log("state.booleanObjectValue: " + getState().booleanObjectValue);
        rpc.log("state.booleanArray: "
                + Arrays.toString(getState().booleanArray));

        rpc.log("state.byteValue: " + getState().byteValue);
        rpc.log("state.byteObjectValue: " + getState().byteObjectValue);
        rpc.log("state.byteArray: " + Arrays.toString(getState().byteArray));

        rpc.log("state.charValue: " + getState().charValue);
        rpc.log("state.charObjectValue: " + getState().charObjectValue);
        rpc.log("state.charArray: " + String.valueOf(getState().charArray));

        rpc.log("state.intValue: " + getState().intValue);
        rpc.log("state.intObjectValue: " + getState().intObjectValue);
        rpc.log("state.intArray: " + Arrays.toString(getState().intArray));

        rpc.log("state.longValue: " + getState().longValue);
        rpc.log("state.longObjectValue: " + getState().longObjectValue);
        rpc.log("state.longArray: " + Arrays.toString(getState().longArray));

        rpc.log("state.floatValue: " + getState().floatValue);
        rpc.log("state.floatObjectValue: " + getState().floatObjectValue);
        rpc.log("state.floatArray: " + Arrays.toString(getState().floatArray));

        rpc.log("state.doubleValue: " + getState().doubleValue);
        rpc.log("state.doubleObjectValue: " + getState().doubleObjectValue);
        rpc.log("state.doubleArray: " + Arrays.toString(getState().doubleArray));

        /*
         * TODO public double doubleValue; public Double DoubleValue; public
         * double[] doubleArray; ;
         * 
         * public String string;
         * 
         * public String nullString;
         * 
         * public Connector connector;
         * 
         * public ComplexTestBean complexTestBean; public SimpleTestBean
         * simpleTestBean; public SimpleTestBean[] simpleTestBeanArray; public
         * int[][] nestedIntArray; public SimpleTestBean[][] nestedBeanArray;
         * 
         * public List<Integer> intList; public List<Connector> connectorList;
         * public List<SimpleTestBean> simpleTestBeanList;
         * 
         * public List<int[]> primitiveArrayList; public List<Integer[]>
         * objectArrayList; public List<SimpleTestBean[]> beanArrayList;
         * 
         * public List<Integer>[] objectListArray; public List<SimpleTestBean>[]
         * beanListArray;
         * 
         * public Set<Integer> intSet; public Set<Connector> connectorSet;
         * public Set<SimpleTestBean> beanSet;
         * 
         * public Map<String, SimpleTestBean> stringMap; public Map<Connector,
         * SimpleTestBean> connectorMap; public Map<Integer, Connector> intMap;
         * public Map<SimpleTestBean, SimpleTestBean> beanMap;
         * 
         * public Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>>
         * generics;
         * 
         * public ContentMode contentMode; public ContentMode[] array; public
         * List<ContentMode> list;
         * 
         * public SimpleTestBean bean;
         * 
         * public Date date1; public Date date2;
         */
    }

    @Override
    protected void extend(ServerConnector target) {
        // TODO Auto-generated method stub

    }

}
