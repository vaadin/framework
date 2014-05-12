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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.label.ContentMode;

@SuppressWarnings("javadoc")
public interface SerializerTestRpc extends ServerRpc, ClientRpc {
    public void sendBoolean(boolean value, Boolean boxedValue, boolean[] array);

    public void sendByte(byte value, Byte boxedValue, byte[] array);

    public void sendChar(char value, Character boxedValue, char[] array);

    public void sendInt(int value, Integer boxedValue, int[] array);

    public void sendLong(long value, Long boxedValue, long[] array);

    public void sendFloat(float value, Float boxedValue, float[] array);

    public void sendDouble(double value, Double boxedValue, double[] array);

    public void sendString(String value);

    public void sendConnector(Connector connector);

    public void sendBean(ComplexTestBean complexBean,
            SimpleTestBean simpleBean, SimpleTestBean[] array);

    public void sendNull(String value1, String value2);

    public void sendNestedArray(int[][] nestedIntArray,
            SimpleTestBean[][] nestedBeanArray);

    public void sendList(List<Integer> intList, List<Connector> connectorList,
            List<SimpleTestBean> beanList);

    public void sendArrayList(List<int[]> primitiveArrayList,
            List<Integer[]> objectArrayList,
            List<SimpleTestBean[]> beanArrayList);

    public void sendListArray(List<Integer>[] objectListArray,
            List<SimpleTestBean>[] beanListArray);

    public void sendSet(Set<Integer> intSet, Set<Connector> connectorSet,
            Set<SimpleTestBean> beanSet);

    public void sendMap(Map<String, SimpleTestBean> stringMap,
            Map<Connector, SimpleTestBean> connectorMap,
            Map<Integer, Connector> intMap,
            Map<SimpleTestBean, SimpleTestBean> beanMap);

    public void sendWrappedGenerics(
            Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>> generics);

    public void sendEnum(ContentMode contentMode, ContentMode[] array,
            List<ContentMode> list);

    public void sendBeanSubclass(SimpleTestBean bean);

    public void sendDate(Date date);

    public void log(String string);
}
