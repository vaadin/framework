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
package com.vaadin.v7.tests.server.component.abstractselect;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.PropertySetChangeEvent;
import com.vaadin.v7.data.Container.PropertySetChangeListener;
import com.vaadin.v7.ui.ComboBox;

public class AbstractSelectListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testItemSetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(ComboBox.class, ItemSetChangeEvent.class,
                ItemSetChangeListener.class);
    }

    @Test
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(ComboBox.class, PropertySetChangeEvent.class,
                PropertySetChangeListener.class);
    }
}
