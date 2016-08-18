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
package com.vaadin.tests.contextclick;

import com.vaadin.annotations.Theme;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

@Theme("valo")
public abstract class AbstractContextClickUI<T extends AbstractComponent, E extends ContextClickEvent>
        extends AbstractTestUIWithLog {

    private final class ListenerHandler implements Button.ClickListener {
        private boolean hasListener = false;
        private final ContextClickListener listener;

        public ListenerHandler(ContextClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void buttonClick(ClickEvent event) {
            if (!hasListener) {
                testComponent.addContextClickListener(listener);
                event.getButton().setDescription("Remove listener");
                hasListener = true;
            } else {
                testComponent.removeContextClickListener(listener);
                event.getButton().setDescription("Add listener");
                hasListener = false;
            }
        }
    }

    protected T testComponent;
    private ContextClickListener defaultListener = new ContextClickListener() {

        @Override
        public void contextClick(ContextClickEvent event) {
            log("ContextClickEvent: (" + event.getClientX() + ", "
                    + event.getClientY() + ")");
        }
    };

    private ContextClickListener typedListener = new ContextClickListener() {

        @Override
        public void contextClick(ContextClickEvent event) {
            try {
                E typedEvent = (E) event;
                handleContextClickEvent(typedEvent);
            } catch (Exception e) {
                log("UNEXPECTED EVENT TYPE!");
            }
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        testComponent = createTestComponent();
        testComponent.setId("testComponent");

        addComponent(testComponent);
        addComponent(createContextClickControls());
    }

    protected abstract T createTestComponent();

    protected abstract void handleContextClickEvent(E event);

    protected HorizontalLayout createContextClickControls() {
        HorizontalLayout contextClickControls = new HorizontalLayout();
        contextClickControls
                .addComponent(new Button("Add/Remove default listener",
                        new ListenerHandler(defaultListener)));
        contextClickControls
                .addComponent(new Button("Add/Remove typed listener",
                        new ListenerHandler(typedListener)));
        return contextClickControls;
    }
}
