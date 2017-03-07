/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.util;

import java.util.HashMap;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class TestClickListener implements Button.ClickListener {

    private static final HashMap<String, Integer> buttonListeners = new HashMap<>();

    String name = "";
    int count = 0;

    public TestClickListener(String name) {
        Integer count = null;
        try {
            count = buttonListeners.get(name);
            count = new Integer(count.intValue() + 1);
            buttonListeners.put(name, count);
        } catch (Exception e) {
            count = new Integer(1);
            buttonListeners.put(name, count);
        }

        this.name = name;
        this.count = count.intValue();

        System.out.println("Created listener " + name + ", id=" + count);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        System.out
                .println("ClickEvent from listener " + name + ", id=" + count);
    }

}
