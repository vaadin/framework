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
package com.vaadin.tests.server.component.popupview;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;

public class PopupViewListenersTest extends AbstractListenerMethodsTestBase {
    public void testPopupVisibilityListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(PopupView.class, PopupVisibilityEvent.class,
                PopupVisibilityListener.class, new PopupView("", new Label()));
    }
}
