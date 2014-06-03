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
/**
 * 
 */
package com.vaadin.client;

import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.UnknownComponentConnector;
import com.vaadin.client.ui.VWindow;

/**
 * @author Vaadin Ltd
 * 
 * @deprecated as of 7.1. This class was mainly used by the old debug console
 *             but is retained for now for backwards compatibility.
 */
@Deprecated
public class VUIDLBrowser extends SimpleTree {
    private static final String HELP = "Alt-click handle to open recursively. ";
    private ApplicationConnection client;
    private String highlightedPid;

    public VUIDLBrowser(final UIDL uidl, ApplicationConnection client) {
        this.client = client;
        final UIDLItem root = new UIDLItem(uidl);
        add(root);
    }

    public VUIDLBrowser(ValueMap u, ApplicationConnection client) {
        this.client = client;
        ValueMap valueMap = u.getValueMap("meta");
        if (valueMap.containsKey("hl")) {
            highlightedPid = valueMap.getString("hl");
        }
        Set<String> keySet = u.getKeySet();
        for (String key : keySet) {
            if (key.equals("state")) {
                ValueMap stateJson = u.getValueMap(key);
                SimpleTree stateChanges = new SimpleTree("shared state");

                for (String connectorId : stateJson.getKeySet()) {
                    stateChanges.add(new SharedStateItem(connectorId, stateJson
                            .getValueMap(connectorId)));
                }
                add(stateChanges);

            } else if (key.equals("changes")) {
                JsArray<UIDL> jsValueMapArray = u.getJSValueMapArray(key)
                        .cast();
                for (int i = 0; i < jsValueMapArray.length(); i++) {
                    UIDL uidl = jsValueMapArray.get(i);
                    UIDLItem change = new UIDLItem(uidl);
                    change.setTitle("change " + i);
                    add(change);
                }
            } else if (key.equals("meta")) {

            } else {
                // TODO consider pretty printing other request data such as
                // hierarchy changes
                // addItem(key + " : " + u.getAsString(key));
            }
        }
        open(highlightedPid != null);
        setTitle(HELP);
    }

    /**
     * A debug view of a server-originated component state change.
     */
    abstract class StateChangeItem extends SimpleTree {

        protected StateChangeItem() {
            setTitle(HELP);
            addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    deHiglight();
                }
            }, MouseOutEvent.getType());
        }

        @Override
        protected void select(ClickEvent event) {
            ServerConnector connector = getConnector();

            if (connector != null && event != null) {
                connector.getConnection().highlightConnector(connector);
            }

            // For connectors that do not have a widget, highlight the widget of
            // their ancestor component connector if any
            while (connector != null
                    && !(connector instanceof ComponentConnector)) {
                connector = connector.getParent();
            }
            if (connector != null) {
                ComponentConnector cc = (ComponentConnector) connector;
                highlight(cc);
            }
            super.select(event);
        }

        /**
         * Returns the Connector associated with this state change.
         */
        protected ServerConnector getConnector() {
            return client.getConnectorMap().getConnector(getConnectorId());
        }

        protected abstract String getConnectorId();
    }

    /**
     * A debug view of a Vaadin 7 style shared state change.
     */
    class SharedStateItem extends StateChangeItem {

        private String connectorId;

        SharedStateItem(String connectorId, ValueMap stateChanges) {
            this.connectorId = connectorId;
            ServerConnector connector = getConnector();
            if (connector != null) {
                setText(Util.getConnectorString(connector));
            } else {
                setText("Unknown connector (" + connectorId + ")");
            }
            dir(new JSONObject(stateChanges), this);
        }

        @Override
        protected String getConnectorId() {
            return connectorId;
        }

        private void dir(String key, JSONValue value, SimpleTree tree) {
            if (value.isObject() != null) {
                SimpleTree subtree = new SimpleTree(key + "=object");
                tree.add(subtree);
                dir(value.isObject(), subtree);
            } else if (value.isArray() != null) {
                SimpleTree subtree = new SimpleTree(key + "=array");
                dir(value.isArray(), subtree);
                tree.add(subtree);
            } else {
                tree.addItem(key + "=" + value);
            }
        }

        private void dir(JSONObject state, SimpleTree tree) {
            for (String key : state.keySet()) {
                dir(key, state.get(key), tree);
            }
        }

        private void dir(JSONArray array, SimpleTree tree) {
            for (int i = 0; i < array.size(); ++i) {
                dir("" + i, array.get(i), tree);
            }
        }
    }

    /**
     * A debug view of a Vaadin 6 style hierarchical component state change.
     */
    class UIDLItem extends StateChangeItem {

        private UIDL uidl;

        UIDLItem(UIDL uidl) {
            this.uidl = uidl;
            try {
                String name = uidl.getTag();
                try {
                    name = getNodeName(uidl, client.getConfiguration(),
                            Integer.parseInt(name));
                } catch (Exception e) {
                    // NOP
                }
                setText(name);
                addItem("LOADING");
            } catch (Exception e) {
                setText(uidl.toString());
            }
        }

        @Override
        protected String getConnectorId() {
            return uidl.getId();
        }

        private String getNodeName(UIDL uidl, ApplicationConfiguration conf,
                int tag) {
            Class<? extends ServerConnector> widgetClassByDecodedTag = conf
                    .getConnectorClassByEncodedTag(tag);
            if (widgetClassByDecodedTag == UnknownComponentConnector.class) {
                return conf.getUnknownServerClassNameByTag(tag)
                        + "(NO CLIENT IMPLEMENTATION FOUND)";
            } else {
                return widgetClassByDecodedTag.getName();
            }
        }

        @Override
        public void open(boolean recursive) {
            if (getWidgetCount() == 1
                    && getWidget(0).getElement().getInnerText()
                            .equals("LOADING")) {
                dir();
            }
            super.open(recursive);
        }

        public void dir() {
            remove(0);

            String nodeName = uidl.getTag();
            try {
                nodeName = getNodeName(uidl, client.getConfiguration(),
                        Integer.parseInt(nodeName));
            } catch (Exception e) {
                // NOP
            }

            Set<String> attributeNames = uidl.getAttributeNames();
            for (String name : attributeNames) {
                if (uidl.isMapAttribute(name)) {
                    try {
                        ValueMap map = uidl.getMapAttribute(name);
                        JsArrayString keyArray = map.getKeyArray();
                        nodeName += " " + name + "=" + "{";
                        for (int i = 0; i < keyArray.length(); i++) {
                            nodeName += keyArray.get(i) + ":"
                                    + map.getAsString(keyArray.get(i)) + ",";
                        }
                        nodeName += "}";
                    } catch (Exception e) {

                    }
                } else {
                    final String value = uidl.getAttribute(name);
                    nodeName += " " + name + "=" + value;
                }
            }
            setText(nodeName);

            try {
                SimpleTree tmp = null;
                Set<String> variableNames = uidl.getVariableNames();
                for (String name : variableNames) {
                    String value = "";
                    try {
                        value = uidl.getVariable(name);
                    } catch (final Exception e) {
                        try {
                            String[] stringArrayAttribute = uidl
                                    .getStringArrayAttribute(name);
                            value = stringArrayAttribute.toString();
                        } catch (final Exception e2) {
                            try {
                                final int intVal = uidl.getIntVariable(name);
                                value = String.valueOf(intVal);
                            } catch (final Exception e3) {
                                value = "unknown";
                            }
                        }
                    }
                    if (tmp == null) {
                        tmp = new SimpleTree("variables");
                    }
                    tmp.addItem(name + "=" + value);
                }
                if (tmp != null) {
                    add(tmp);
                }
            } catch (final Exception e) {
                // Ignored, no variables
            }

            final Iterator<Object> i = uidl.getChildIterator();
            while (i.hasNext()) {
                final Object child = i.next();
                try {
                    add(new UIDLItem((UIDL) child));
                } catch (final Exception e) {
                    addItem(child.toString());
                }
            }
            if (highlightedPid != null && highlightedPid.equals(uidl.getId())) {
                getElement().getStyle().setBackgroundColor("#fdd");
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        getElement().scrollIntoView();
                    }
                });
            }
        }
    }

    static Element highlight = Document.get().createDivElement();

    static {
        Style style = highlight.getStyle();
        style.setPosition(Position.ABSOLUTE);
        style.setZIndex(VWindow.Z_INDEX + 1000);
        style.setBackgroundColor("red");
        style.setOpacity(0.2);
        if (BrowserInfo.get().isIE()) {
            style.setProperty("filter", "alpha(opacity=20)");
        }
    }

    static void highlight(ComponentConnector paintable) {
        if (paintable != null) {
            Widget w = paintable.getWidget();
            Style style = highlight.getStyle();
            style.setTop(w.getAbsoluteTop(), Unit.PX);
            style.setLeft(w.getAbsoluteLeft(), Unit.PX);
            style.setWidth(w.getOffsetWidth(), Unit.PX);
            style.setHeight(w.getOffsetHeight(), Unit.PX);
            RootPanel.getBodyElement().appendChild(highlight);
        }
    }

    static void deHiglight() {
        if (highlight.getParentElement() != null) {
            highlight.getParentElement().removeChild(highlight);
        }
    }

}
