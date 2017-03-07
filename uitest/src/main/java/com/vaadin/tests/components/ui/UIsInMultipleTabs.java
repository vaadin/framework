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
package com.vaadin.tests.components.ui;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.AbstractTestUIProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class UIsInMultipleTabs extends AbstractTestUIProvider {
    // No cleanup -> will leak, but shouldn't matter for tests
    private static ConcurrentHashMap<VaadinSession, AtomicInteger> numberOfUIsOpened = new ConcurrentHashMap<>();

    public static class TabUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
            VaadinSession application = VaadinSession.getCurrent();
            AtomicInteger count = numberOfUIsOpened.get(application);
            if (count == null) {
                numberOfUIsOpened.putIfAbsent(application, new AtomicInteger());
                // Get our added instance or another instance that was added by
                // another thread between previous get and putIfAbsent
                count = numberOfUIsOpened.get(application);
            }
            int currentCount = count.incrementAndGet();
            String message = "This is UI number " + currentCount;

            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            setContent(layout);

            layout.addComponent(new Label(message));
        }
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return TabUI.class;
    }

    @Override
    protected String getTestDescription() {
        return "Opening the same application again (e.g. in a new tab) should create a new UI.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7894);
    }
}
