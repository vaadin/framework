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
package com.vaadin.v7.tests.server.component.select;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.ui.Select;

public class SelectListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Select.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Select.class, BlurEvent.class,
                BlurListener.class);
    }
}
