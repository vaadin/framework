/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.ui;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;

import com.vaadin.server.ClientConnector;

import elemental.json.JsonObject;

/**
 * Base class for component unit tests, providing helper methods for e.g.
 * invoking RPC and updating diff state.
 */
public class ComponentTest {

    /**
     * Perform operations on the component similar to what would be done when
     * the component state is communicated to the client, e.g. update diff state
     * and mark as clean.
     *
     * @param component
     *            the component to update
     */
    public static void syncToClient(AbstractComponent component) {
        updateDiffState(component);
        component.getUI().getConnectorTracker().markClean(component);
    }

    /**
     * Checks if the connector has been marked dirty.
     *
     * @param connector
     *            the connector to check
     * @return <code>true</code> if the connector has been marked dirty,
     *         <code>false</code> otherwise
     */
    public static boolean isDirty(ClientConnector connector) {
        return connector.getUI().getConnectorTracker().isDirty(connector);
    }

    /**
     * Updates the stored diff state from the current component state.
     *
     * @param rta
     *            the component to update
     */
    public static void updateDiffState(AbstractComponent component) {
        component.getUI().getSession().getCommunicationManager()
                .encodeState(component, component.getState());

    }

    /**
     * Asserts the set of properties that would be sent as state changes for the
     * given connector.
     *
     * @param connector
     *            the connector that has state changes
     * @param message
     *            the message to show if the properties are not as expected
     * @param expectedProperties
     *            names of the expected properties
     */
    public static void assertEncodedStateProperties(ClientConnector connector,
            String message, String... expectedProperties) {
        assert connector.isAttached();

        JsonObject encodeState = connector.encodeState();

        // Collect to HashSet so that order doesn't matter
        Assert.assertEquals(message,
                new HashSet<>(Arrays.asList(expectedProperties)),
                new HashSet<>(Arrays.asList(encodeState.keys())));
    }

    private ComponentTest() {
    }

}
