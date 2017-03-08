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
package com.vaadin.tests.server.component.abstractcomponentcontainer;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class AbstractComponentContainerListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testComponentDetachListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(HorizontalLayout.class,
                ComponentDetachEvent.class, ComponentDetachListener.class);
    }

    @Test
    public void testComponentAttachListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(VerticalLayout.class,
                ComponentAttachEvent.class, ComponentAttachListener.class);
    }
}
