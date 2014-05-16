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
package com.vaadin.client.debug.internal;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.UnknownComponentConnector;

/**
 * Optimized widgetset view panel of the debug window.
 * 
 * @since 7.1.4
 */
public class OptimizedWidgetsetPanel extends FlowPanel {

    /**
     * Update the panel contents based on the connectors that have been used so
     * far on this execution of the application.
     */
    public void update() {
        clear();
        HTML h = new HTML("Getting used connectors");
        add(h);

        String s = "";
        for (ApplicationConnection ac : ApplicationConfiguration
                .getRunningApplications()) {
            ApplicationConfiguration conf = ac.getConfiguration();
            s += "<h1>Used connectors for " + conf.getServiceUrl() + "</h1>";

            for (String connectorName : getUsedConnectorNames(conf)) {
                s += connectorName + "<br/>";
            }

            s += "<h2>To make an optimized widgetset based on these connectors, do:</h2>";
            s += "<h3>1. Add to your widgetset.gwt.xml file:</h2>";
            s += "<textarea rows=\"3\" style=\"width:90%\">";
            s += "<generate-with class=\"OptimizedConnectorBundleLoaderFactory\">\n";
            s += "      <when-type-assignable class=\"com.vaadin.client.metadata.ConnectorBundleLoader\" />\n";
            s += "</generate-with>";
            s += "</textarea>";

            s += "<h3>2. Add the following java file to your project:</h2>";
            s += "<textarea rows=\"5\" style=\"width:90%\">";
            s += generateOptimizedWidgetSet(getUsedConnectorNames(conf));
            s += "</textarea>";
            s += "<h3>3. Recompile widgetset</h2>";

        }

        h.setHTML(s);
    }

    private Set<String> getUsedConnectorNames(
            ApplicationConfiguration configuration) {
        int tag = 0;
        Set<String> usedConnectors = new HashSet<String>();
        while (true) {
            String serverSideClass = configuration
                    .getServerSideClassNameForTag(tag);
            if (serverSideClass == null) {
                break;
            }
            Class<? extends ServerConnector> connectorClass = configuration
                    .getConnectorClassByEncodedTag(tag);
            if (connectorClass == null) {
                break;
            }

            if (connectorClass != UnknownComponentConnector.class) {
                usedConnectors.add(connectorClass.getName());
            }
            tag++;
            if (tag > 10000) {
                // Sanity check
                VConsole.error("Search for used connector classes was forcefully terminated");
                break;
            }
        }
        return usedConnectors;
    }

    public String generateOptimizedWidgetSet(Set<String> usedConnectors) {
        String s = "import java.util.HashSet;\n";
        s += "import java.util.Set;\n";

        s += "import com.google.gwt.core.ext.typeinfo.JClassType;\n";
        s += "import com.vaadin.client.ui.ui.UIConnector;\n";
        s += "import com.vaadin.server.widgetsetutils.ConnectorBundleLoaderFactory;\n";
        s += "import com.vaadin.shared.ui.Connect.LoadStyle;\n\n";

        s += "public class OptimizedConnectorBundleLoaderFactory extends\n";
        s += "            ConnectorBundleLoaderFactory {\n";
        s += "    private Set<String> eagerConnectors = new HashSet<String>();\n";
        s += "    {\n";
        for (String c : usedConnectors) {
            s += "            eagerConnectors.add(" + c
                    + ".class.getName());\n";
        }
        s += "    }\n";
        s += "\n";
        s += "    @Override\n";
        s += "    protected LoadStyle getLoadStyle(JClassType connectorType) {\n";
        s += "            if (eagerConnectors.contains(connectorType.getQualifiedBinaryName())) {\n";
        s += "                    return LoadStyle.EAGER;\n";
        s += "            } else {\n";
        s += "                    // Loads all other connectors immediately after the initial view has\n";
        s += "                    // been rendered\n";
        s += "                    return LoadStyle.DEFERRED;\n";
        s += "            }\n";
        s += "    }\n";
        s += "}\n";

        return s;
    }

}
