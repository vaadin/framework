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

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Item.PropertySetChangeEvent;
import com.vaadin.v7.data.Item.PropertySetChangeListener;
import com.vaadin.v7.data.util.PropertysetItem;

public class PropertysetItemListenersTest
        extends AbstractListenerMethodsTestBase {
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(PropertysetItem.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class);
    }
}
