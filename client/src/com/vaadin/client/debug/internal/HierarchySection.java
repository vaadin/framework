/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.SimpleTree;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ValueMap;
import com.vaadin.client.ui.UnknownComponentConnector;
import com.vaadin.shared.AbstractComponentState;

/**
 * Provides functionality for examining the UI component hierarchy.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
class HierarchySection implements Section {

    private final DebugButton tabButton = new DebugButton(Icon.HIERARCHY,
            "Examine compoent hierarchy");

    private final FlowPanel content = new FlowPanel();
    private final FlowPanel controls = new FlowPanel();

    private final Button find = new DebugButton(Icon.HIGHLIGHT,
            "Select a component on the page to inspect it");
    private final Button analyze = new DebugButton(Icon.ANALYZE,
            "Check layouts for potential problems");
    private final Button generateWS = new DebugButton(Icon.OPTIMIZE,
            "Show used connectors and how to optimize widgetset");

    private HandlerRegistration highlightModeRegistration = null;

    public HierarchySection() {
        controls.add(find);
        find.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        find.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleFind();
            }
        });

        controls.add(analyze);
        analyze.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        analyze.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                stopFind();
                analyzeLayouts();
            }
        });

        controls.add(generateWS);
        generateWS.setStylePrimaryName(VDebugWindow.STYLENAME_BUTTON);
        generateWS.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                generateWidgetset();
            }
        });

        content.setStylePrimaryName(VDebugWindow.STYLENAME + "-hierarchy");
    }

    @Override
    public DebugButton getTabButton() {
        return tabButton;
    }

    @Override
    public Widget getControls() {
        return controls;
    }

    @Override
    public Widget getContent() {
        return content;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
        stopFind();
    }

    @Override
    public void log(Level level, String msg) {
        // TODO
    }

    private void generateWidgetset() {

        content.clear();
        HTML h = new HTML("Getting used connectors");
        content.add(h);

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

    private void analyzeLayouts() {
        content.clear();
        content.add(new Label("Analyzing layouts..."));
        List<ApplicationConnection> runningApplications = ApplicationConfiguration
                .getRunningApplications();
        for (ApplicationConnection applicationConnection : runningApplications) {
            applicationConnection.analyzeLayouts();
        }
    }

    public void meta(ApplicationConnection ac, ValueMap meta) {
        content.clear();
        JsArray<ValueMap> valueMapArray = meta
                .getJSValueMapArray("invalidLayouts");
        int size = valueMapArray.length();

        if (size > 0) {
            SimpleTree root = new SimpleTree("Layouts analyzed, " + size
                    + " top level problems");
            for (int i = 0; i < size; i++) {
                printLayoutError(ac, valueMapArray.get(i), root);
            }
            root.open(false);
            content.add(root);
        } else {
            content.add(new Label("Layouts analyzed, no top level problems"));
        }

        Set<ComponentConnector> zeroHeightComponents = new HashSet<ComponentConnector>();
        Set<ComponentConnector> zeroWidthComponents = new HashSet<ComponentConnector>();
        findZeroSizeComponents(zeroHeightComponents, zeroWidthComponents,
                ac.getUIConnector());
        if (zeroHeightComponents.size() > 0 || zeroWidthComponents.size() > 0) {
            content.add(new HTML("<h4> Client side notifications</h4>"
                    + " <em>The following relative sized components were "
                    + "rendered to a zero size container on the client side."
                    + " Note that these are not necessarily invalid "
                    + "states, but reported here as they might be.</em>"));
            if (zeroHeightComponents.size() > 0) {
                content.add(new HTML(
                        "<p><strong>Vertically zero size:</strong></p>"));
                printClientSideDetectedIssues(zeroHeightComponents, ac);
            }
            if (zeroWidthComponents.size() > 0) {
                content.add(new HTML(
                        "<p><strong>Horizontally zero size:</strong></p>"));
                printClientSideDetectedIssues(zeroWidthComponents, ac);
            }
        }

    }

    private void printClientSideDetectedIssues(
            Set<ComponentConnector> zeroSized, ApplicationConnection ac) {

        // keep track of already highlighted parents
        HashSet<String> parents = new HashSet<String>();

        for (final ComponentConnector connector : zeroSized) {
            final ServerConnector parent = connector.getParent();
            final String parentId = parent.getConnectorId();

            final Label errorDetails = new Label(Util.getSimpleName(connector)
                    + "[" + connector.getConnectorId() + "]" + " inside "
                    + Util.getSimpleName(parent));

            if (parent instanceof ComponentConnector) {
                final ComponentConnector parentConnector = (ComponentConnector) parent;
                if (!parents.contains(parentId)) {
                    parents.add(parentId);
                    Highlight.show(parentConnector, "yellow");
                }

                errorDetails.addMouseOverHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        Highlight.hideAll();
                        Highlight.show(parentConnector, "yellow");
                        Highlight.show(connector);
                        errorDetails.getElement().getStyle()
                                .setTextDecoration(TextDecoration.UNDERLINE);
                    }
                });
                errorDetails.addMouseOutHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        Highlight.hideAll();
                        errorDetails.getElement().getStyle()
                                .setTextDecoration(TextDecoration.NONE);
                    }
                });
                errorDetails.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        printState(connector);
                        Highlight.show(connector);
                    }
                });

            }

            Highlight.show(connector);
            content.add(errorDetails);

        }
    }

    private void printLayoutError(ApplicationConnection ac, ValueMap valueMap,
            SimpleTree root) {
        final String pid = valueMap.getString("id");

        // find connector
        final ComponentConnector connector = (ComponentConnector) ConnectorMap
                .get(ac).getConnector(pid);

        if (connector == null) {
            root.add(new SimpleTree("[" + pid + "] NOT FOUND"));
            return;
        }

        Highlight.show(connector);

        final SimpleTree errorNode = new SimpleTree(
                Util.getSimpleName(connector) + " id: " + pid);
        errorNode.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                Highlight.hideAll();
                Highlight.show(connector);
                ((Widget) event.getSource()).getElement().getStyle()
                        .setTextDecoration(TextDecoration.UNDERLINE);
            }
        }, MouseOverEvent.getType());
        errorNode.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                Highlight.hideAll();
                ((Widget) event.getSource()).getElement().getStyle()
                        .setTextDecoration(TextDecoration.NONE);
            }
        }, MouseOutEvent.getType());

        errorNode.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeEvent().getEventTarget().cast() == errorNode
                        .getElement().getChild(1).cast()) {
                    printState(connector);
                }
            }
        }, ClickEvent.getType());

        VerticalPanel errorDetails = new VerticalPanel();

        if (valueMap.containsKey("heightMsg")) {
            errorDetails.add(new Label("Height problem: "
                    + valueMap.getString("heightMsg")));
        }
        if (valueMap.containsKey("widthMsg")) {
            errorDetails.add(new Label("Width problem: "
                    + valueMap.getString("widthMsg")));
        }
        if (errorDetails.getWidgetCount() > 0) {
            errorNode.add(errorDetails);
        }
        if (valueMap.containsKey("subErrors")) {
            HTML l = new HTML(
                    "<em>Expand this node to show problems that may be dependent on this problem.</em>");
            errorDetails.add(l);
            JsArray<ValueMap> suberrors = valueMap
                    .getJSValueMapArray("subErrors");
            for (int i = 0; i < suberrors.length(); i++) {
                ValueMap value = suberrors.get(i);
                printLayoutError(ac, value, errorNode);
            }

        }
        root.add(errorNode);
    }

    private void findZeroSizeComponents(
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents,
            ComponentConnector connector) {
        Widget widget = connector.getWidget();
        ComputedStyle computedStyle = new ComputedStyle(widget.getElement());
        if (computedStyle.getIntProperty("height") == 0) {
            zeroHeightComponents.add(connector);
        }
        if (computedStyle.getIntProperty("width") == 0) {
            zeroWidthComponents.add(connector);
        }
        List<ServerConnector> children = connector.getChildren();
        for (ServerConnector serverConnector : children) {
            if (serverConnector instanceof ComponentConnector) {
                findZeroSizeComponents(zeroHeightComponents,
                        zeroWidthComponents,
                        (ComponentConnector) serverConnector);
            }
        }
    }

    @Override
    public void uidl(ApplicationConnection ac, ValueMap uidl) {
        // NOP
    }

    private boolean isFindMode() {
        return (highlightModeRegistration != null);
    }

    private void toggleFind() {
        if (isFindMode()) {
            stopFind();
        } else {
            startFind();
        }
    }

    private void startFind() {
        Highlight.hideAll();
        if (!isFindMode()) {
            highlightModeRegistration = Event
                    .addNativePreviewHandler(highlightModeHandler);
            find.addStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        }
    }

    private void stopFind() {
        if (isFindMode()) {
            highlightModeRegistration.removeHandler();
            highlightModeRegistration = null;
            find.removeStyleDependentName(VDebugWindow.STYLENAME_ACTIVE);
        }
    }

    private void printState(ComponentConnector connector) {
        Highlight.show(connector);
        AbstractComponentState state = connector.getState();

        String html = getRowHTML("Id", connector.getConnectorId());
        html += getRowHTML("Connector", Util.getSimpleName(connector));
        html += getRowHTML("Widget", Util.getSimpleName(connector.getWidget()));
        html += getRowHTML("Caption", state.caption);
        html += getRowHTML("Description", state.description);
        html += getRowHTML("Width", state.width + " (actual: "
                + connector.getWidget().getOffsetWidth() + "px)");
        html += getRowHTML("Height", state.height + " (actual: "
                + connector.getWidget().getOffsetHeight() + "px)");
        html += getRowHTML("Enabled", state.enabled);
        html += getRowHTML("Read only", state.readOnly);
        html += getRowHTML("Immediate", state.immediate);
        html += getRowHTML("Error message", state.errorMessage);
        html += getRowHTML("Primary stylename", state.primaryStyleName);
        html += getRowHTML("Styles", state.styles);
        html += getRowHTML("Resources", state.resources);

        content.clear();
        content.add(new HTML(html));
    }

    private String getRowHTML(String caption, Object value) {
        return "<div class=\"" + VDebugWindow.STYLENAME
                + "-row\"><span class=\"caption\">" + caption
                + "</span><span class=\"value\">" + value + "</span></div>";
    }

    private final NativePreviewHandler highlightModeHandler = new NativePreviewHandler() {

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {

            if (event.getTypeInt() == Event.ONKEYDOWN
                    && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                stopFind();
                Highlight.hideAll();
                return;
            }
            if (event.getTypeInt() == Event.ONMOUSEMOVE) {
                Highlight.hideAll();
                Element eventTarget = Util.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                if (VDebugWindow.get().getElement().isOrHasChild(eventTarget)) {
                    content.clear();
                    return;
                }

                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    ComponentConnector connector = Util.getConnectorForElement(
                            a, a.getUIConnector().getWidget(), eventTarget);
                    if (connector == null) {
                        connector = Util.getConnectorForElement(a,
                                RootPanel.get(), eventTarget);
                    }
                    if (connector != null) {
                        printState(connector);
                        event.cancel();
                        event.consume();
                        event.getNativeEvent().stopPropagation();
                        return;
                    }
                }
                content.clear();
            }
            if (event.getTypeInt() == Event.ONCLICK) {
                Highlight.hideAll();
                event.cancel();
                event.consume();
                event.getNativeEvent().stopPropagation();
                stopFind();
                Element eventTarget = Util.getElementFromPoint(event
                        .getNativeEvent().getClientX(), event.getNativeEvent()
                        .getClientY());
                for (ApplicationConnection a : ApplicationConfiguration
                        .getRunningApplications()) {
                    ComponentConnector connector = Util.getConnectorForElement(
                            a, a.getUIConnector().getWidget(), eventTarget);
                    if (connector == null) {
                        connector = Util.getConnectorForElement(a,
                                RootPanel.get(), eventTarget);
                    }

                    if (connector != null) {
                        printState(connector);
                        return;
                    }
                }
            }
            event.cancel();
        }

    };

}