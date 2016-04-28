/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.util;

import java.util.LinkedList;

import com.vaadin.server.ClientConnector;
import com.vaadin.ui.Component;

/**
 * Provides various helper methods for connectors. Meant for internal use.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class ConnectorHelper {

    /**
     * Creates a string containing debug info for the connector
     * 
     * @since 7.1
     * @param connector
     *            The connector to print debug info about
     * @return A string with debug information
     */
    public static String getDebugInformation(ClientConnector connector) {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Debug details of a connector:  *** \n");
        sb.append("Type: ");
        sb.append(connector.getClass().getName());
        sb.append("\nId:");
        sb.append(connector.getConnectorId());
        if (connector instanceof Component) {
            Component component = (Component) connector;
            if (component.getCaption() != null) {
                sb.append("\nCaption:");
                sb.append(component.getCaption());
            }
        }
        writeHierarchyInformation(connector, sb);
        return sb.toString();
    }

    /**
     * Creates a string containing hierarchy information for the connector
     * 
     * @since 7.1
     * @param connector
     *            The connector to get hierarchy information for
     * @param builder
     *            The StringBuilder where the information should be written
     */
    public static void writeHierarchyInformation(ClientConnector connector,
            StringBuilder builder) {
        LinkedList<ClientConnector> h = new LinkedList<ClientConnector>();
        h.add(connector);
        ClientConnector parent = connector.getParent();
        while (parent != null) {
            h.addFirst(parent);
            parent = parent.getParent();
        }

        builder.append("\nConnector hierarchy:\n");

        int l = 0;
        for (ClientConnector connector2 : h) {
            if (l != 0) {
                builder.append("\n");
                for (int i = 0; i < l; i++) {
                    builder.append("  ");
                }
            }
            l++;
            Class<? extends ClientConnector> connectorClass = connector2
                    .getClass();
            Class<?> topClass = connectorClass;
            while (topClass.getEnclosingClass() != null) {
                topClass = topClass.getEnclosingClass();
            }
            builder.append(connectorClass.getName());
            builder.append("(");
            builder.append(topClass.getSimpleName());
            builder.append(".java:1)");
        }
    }

}
