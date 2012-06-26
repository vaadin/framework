/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.widgetset.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

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
            Map<Connector, Boolean> connectorMap,
            Map<Integer, Connector> intMap,
            Map<SimpleTestBean, SimpleTestBean> beanMap);

    public void sendWrappedGenerics(
            Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>> generics);

}
