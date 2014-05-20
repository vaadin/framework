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

package com.vaadin.tests.minitutorials.v7a3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.annotations.JavaScript;
import com.vaadin.shared.Connector;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.UI;

@JavaScript("complex_types_connector.js")
public class ComplexTypesComponent extends AbstractJavaScriptComponent {
    public void sendComplexTypes() {
        List<String> list = new ArrayList<String>();
        list.add("First string");
        list.add(null);
        list.add("Another string");

        Map<String, Integer> stringMap = new HashMap<String, Integer>();
        stringMap.put("one", 1);
        stringMap.put("two", 2);

        Map<Integer, String> otherMap = new HashMap<Integer, String>();
        otherMap.put(3, "3");
        otherMap.put(4, "4");

        Map<Connector, String> connectorMap = new HashMap<Connector, String>();
        connectorMap.put(this, "this");
        connectorMap.put(UI.getCurrent(), "root");

        boolean[] bits = { true, true, false, true };

        List<List<Double>> matrix = Arrays.asList(Arrays.asList(1d, 2d),
                Arrays.asList(3d, 4d));

        ComplexTypesBean innerBean = new ComplexTypesBean();
        innerBean.setInteger(-42);

        ComplexTypesBean bean = new ComplexTypesBean();
        bean.setInteger(42);
        bean.setBean(innerBean);

        getRpcProxy(ComplexTypesRpc.class).sendComplexTypes(list, stringMap,
                otherMap, connectorMap, bits, matrix, bean);
    }
}
