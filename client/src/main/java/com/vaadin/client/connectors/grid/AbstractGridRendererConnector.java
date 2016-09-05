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

package com.vaadin.client.connectors.grid;

import com.vaadin.client.connectors.AbstractRendererConnector;

/**
 * An abstract base class for renderer connectors. A renderer connector is used
 * to link a client-side {@link Renderer} to a server-side
 * {@link com.vaadin.client.renderers.Renderer Renderer}. As a connector, it can
 * use the regular Vaadin RPC and shared state mechanism to pass additional
 * state and information between the client and the server. This base class
 * itself only uses the basic {@link com.vaadin.shared.communication.SharedState
 * SharedState} and no RPC interfaces.
 *
 * @param <T>
 *            the presentation type of the renderer
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract class AbstractGridRendererConnector<T>
        extends AbstractRendererConnector<T> {
    // getRowKey and getColumnId will be needed here later on
}
