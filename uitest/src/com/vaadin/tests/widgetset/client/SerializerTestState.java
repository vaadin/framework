/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License; Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing; software
 * distributed under the License is distributed on an "AS IS" BASIS; WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND; either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.widgetset.client;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.label.ContentMode;

import elemental.json.JsonBoolean;
import elemental.json.JsonValue;

public class SerializerTestState extends AbstractComponentState {

    public boolean booleanValue;
    public Boolean booleanObjectValue;
    public boolean[] booleanArray;

    public byte byteValue;
    public Byte byteObjectValue;
    public byte[] byteArray;

    public char charValue;
    public Character charObjectValue;
    public char[] charArray;

    public int intValue;
    public Integer intObjectValue;
    public int[] intArray;

    public long longValue;
    public Long longObjectValue;
    public long[] longArray;

    public float floatValue;
    public Float floatObjectValue;
    public float[] floatArray;

    public double doubleValue;
    public Double doubleObjectValue;
    public double[] doubleArray;

    public String string;
    public String[] stringArray;

    public String nullString;

    public Connector connector;

    public ComplexTestBean complexTestBean;
    public SimpleTestBean simpleTestBean;
    public SimpleTestBean[] simpleTestBeanArray;
    public int[][] nestedIntArray;
    public SimpleTestBean[][] nestedBeanArray;

    public List<Integer> intList;
    public List<Connector> connectorList;
    public List<SimpleTestBean> simpleTestBeanList;

    public List<int[]> primitiveArrayList;
    public List<Integer[]> objectArrayList;
    public List<SimpleTestBean[]> beanArrayList;

    public List<Integer>[] objectListArray;
    public List<SimpleTestBean>[] beanListArray;

    public Set<Integer> intSet;
    public Set<Connector> connectorSet;
    public Set<SimpleTestBean> beanSet;

    public Map<String, SimpleTestBean> stringMap;
    public Map<Connector, SimpleTestBean> connectorMap;
    public Map<Integer, Connector> intMap;
    public Map<SimpleTestBean, SimpleTestBean> beanMap;

    public Map<Set<SimpleTestBean>, Map<Integer, List<SimpleTestBean>>> generics;

    public ContentMode contentMode;
    public ContentMode[] array;
    public List<ContentMode> list;

    public SimpleTestBean bean;

    public Date date1;
    public Date date2;
    public Date[] dateArray;

    public BeanWithAbstractSuperclass beanWithAbstractSuperclass;

    public JsonValue jsonNull = null;
    public JsonValue jsonString = null;
    public JsonBoolean jsonBoolean = null;

}
