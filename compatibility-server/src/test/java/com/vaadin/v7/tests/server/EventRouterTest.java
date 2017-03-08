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
package com.vaadin.v7.tests.server;

import org.junit.Test;

import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.TextField;

public class EventRouterTest {

    int innerListenerCalls = 0;

    @Test
    public void testAddInEventListener() {
        final TextField tf = new TextField();

        final ValueChangeListener outer = new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                ValueChangeListener inner = new ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        innerListenerCalls++;
                        System.out.println("The inner listener was called");
                    }
                };

                tf.addListener(inner);
            }
        };

        tf.addListener(outer);
        tf.setValue("abc"); // No inner listener calls, adds one inner
        tf.setValue("def"); // One inner listener call, adds one inner
        tf.setValue("ghi"); // Two inner listener calls, adds one inner
        assert (innerListenerCalls == 3);
    }
}
