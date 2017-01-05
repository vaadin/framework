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
package com.vaadin.event;

import com.vaadin.server.ClientConnector;
import com.vaadin.shared.Connector;

/**
 * Event listener interface for events originating from server side connectors.
 *
 * @since 6.0
 * @author Vaadin Ltd
 *
 * @see Connector
 * @see ClientConnector
 */
public interface ConnectorEventListener extends SerializableEventListener {

}
