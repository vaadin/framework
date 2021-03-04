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
package com.vaadin.server;

import java.io.Serializable;

import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;

/**
 * Provides support for triggering an event from a given parts of a component or
 * using various events.
 * <p>
 * Used by features such as {@link FileDownloader} and
 * {@link BrowserWindowOpener} to listen to a given event on a given element on
 * the client side. The component is the one responsible for deciding the
 * element and the event to listen to and can communicate this to the client
 * using {@link #getPartInformation()}.
 * <p>
 * This is the server side interface.
 * <p>
 * If a {@link Component} implements this interface, then the corresponding
 * connector on the client side must implement
 * {@code com.vaadin.client.extensions.EventTrigger}.
 *
 * @since 8.4
 */
public interface EventTrigger extends Serializable {

    /**
     * Gets the connector who will be used to offer the file download. Typically
     * a component containing a certain DOM element, which in turn triggers the
     * download.
     *
     * @return the connector for the file download
     */
    AbstractClientConnector getConnector();

    /**
     * Gets a free form string which identifies which part of the connector that
     * should trigger the download. The string is passed to the connector
     * (FileDownloaderHandler implementor) on the client side.
     * <p>
     * For example, {@link MenuBar} passes the id of a menu item through this
     * method so that the client side can listen to events for that particular
     * item only.
     *
     * @return a free form string which makes sense to the client side connector
     */
    String getPartInformation();

}
