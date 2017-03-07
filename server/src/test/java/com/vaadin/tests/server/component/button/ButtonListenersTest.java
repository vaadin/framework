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
package com.vaadin.tests.server.component.button;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ButtonListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, BlurEvent.class,
                BlurListener.class);
    }

    @Test
    public void testClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, ClickEvent.class,
                ClickListener.class);
    }
}
