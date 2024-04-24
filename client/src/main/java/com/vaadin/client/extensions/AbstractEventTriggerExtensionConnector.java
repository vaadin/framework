/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.extensions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.HasWidget;
import com.vaadin.client.ServerConnector;
import com.vaadin.shared.extension.PartInformationState;

/**
 * An abstract extension connector with trigger support. Implementor's
 * {@link #trigger} method call may be initiated by another {@code Component}
 * without server round-trip. The class is used to overcome browser security
 * limitations. For instance, window may not be open with the round-trip.
 *
 * @author Vaadin Ltd.
 * @since 8.4
 */
public abstract class AbstractEventTriggerExtensionConnector
        extends AbstractExtensionConnector {

    private HandlerRegistration eventHandlerRegistration;

    /**
     * Called whenever a click occurs on the widget (if widget does not
     * implement {@link EventTrigger}) or when the {@link EventTrigger} fires.
     *
     */
    protected abstract void trigger();

    @Override
    public PartInformationState getState() {
        return (PartInformationState) super.getState();
    }

    @Override
    protected void extend(ServerConnector target) {
        Widget targetWidget = ((HasWidget) target).getWidget();
        if (targetWidget instanceof EventTrigger) {
            String partInformation = getState().partInformation;
            eventHandlerRegistration = ((EventTrigger) targetWidget)
                    .addTrigger(this::trigger, partInformation);
        } else {
            eventHandlerRegistration = targetWidget
                    .addDomHandler(e -> trigger(), ClickEvent.getType());
        }
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        if (eventHandlerRegistration != null) {
            eventHandlerRegistration.removeHandler();
            eventHandlerRegistration = null;
        }
    }
}
