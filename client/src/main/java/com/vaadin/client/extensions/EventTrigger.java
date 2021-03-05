/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Provides support for triggering an event from a given parts of a component or
 * using various events.
 * <p>
 * Used by features such as {@link FileDownloaderConnector} and
 * {@link BrowserWindowOpenerConnector} to listen to a given event on a given
 * element. The component is the one responsible for deciding the element and
 * the event to listen to.
 * <p>
 * This is the client side interface.
 * <p>
 * If the component on the server side implements
 * {@code com.vaadin.server.EventTrigger} then this interface should be
 * implemented by the {@link Widget} used by the client side connector.
 *
 * @since 8.4
 */
public interface EventTrigger {

    /**
     * Adds an appropriate event handler on the correct element inside the
     * widget and invokes the given file downloader when the event occurs.
     *
     * @param command
     *            The command to execute when the event occurs
     * @param partInformation
     *            Information passed from the server, typically telling which
     *            element to attach the DOM handler to
     * @return a registration handler which can be used to remove the handler
     */
    public HandlerRegistration addTrigger(Command command,
            String partInformation);

}
