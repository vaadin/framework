/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client;

import java.util.Iterator;
import java.util.List;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.terminal.gwt.client.ui.VWindow;

public class VUIDLBrowser extends SimpleTree {
    private static final String HELP = "Shift click handle to open recursively. Click components to hightlight them on client side. Shift click components to highlight them also on the server side.";
    private ApplicationConfiguration conf;
    private String highlightedPid;

    public VUIDLBrowser(final UIDL uidl, ApplicationConfiguration conf) {
        this.conf = conf;
        final UIDLItem root = new UIDLItem(uidl, conf);
        add(root);
    }

    public VUIDLBrowser(ValueMap u, ApplicationConfiguration conf) {
        this.conf = conf;
        ValueMap valueMap = u.getValueMap("meta");
        if (valueMap.containsKey("hl")) {
            highlightedPid = valueMap.getString("hl");
        }
        Set<String> keySet = u.getKeySet();
        for (String key : keySet) {
            if (key.equals("changes")) {
                JsArray<UIDL> jsValueMapArray = u.getJSValueMapArray("changes")
                        .cast();
                for (int i = 0; i < jsValueMapArray.length(); i++) {
                    UIDL uidl = jsValueMapArray.get(i);
                    UIDLItem change = new UIDLItem(uidl, conf);
                    change.setTitle("change " + i);
                    add(change);
                }
            } else if (key.equals("meta")) {

            } else {
                // TODO consider pretty printing other request data
                // addItem(key + " : " + u.getAsString(key));
            }
        }
        open(highlightedPid != null);
        setTitle(HELP);
    }

    class UIDLItem extends SimpleTree {

        private UIDL uidl;

        UIDLItem(UIDL uidl, ApplicationConfiguration conf) {
            setTitle(HELP);
            this.uidl = uidl;
            try {
                String name = uidl.getTag();
                try {
                    Integer.parseInt(name);
                    name = getNodeName(uidl, conf, name);
                } catch (Exception e) {
                    // NOP
                }
                setText(name);
                addItem("LOADING");
            } catch (Exception e) {
                setText(uidl.toString());
            }

            addDomHandler(new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    deHiglight();
                }
            }, MouseOutEvent.getType());

        }

        private String getNodeName(UIDL uidl, ApplicationConfiguration conf,
                String name) {
            Class<? extends Paintable> widgetClassByDecodedTag = conf
                    .getWidgetClassByEncodedTag(name);
            if (widgetClassByDecodedTag == VUnknownComponent.class) {
                return conf.getUnknownServerClassNameByEncodedTagName(name)
                        + "(NO CLIENT IMPLEMENTATION FOUND)";
            } else if (widgetClassByDecodedTag == VView.class
                    && uidl.hasAttribute("sub")) {
                return "com.vaadin.terminal.gwt.ui.VWindow";
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

        @Override
        protected void select(ClickEvent event) {
            List<ApplicationConnection> runningApplications = ApplicationConfiguration
                    .getRunningApplications();

            // TODO this does not work properly with multiple application on
            // same
            // host page
            for (ApplicationConnection applicationConnection : runningApplications) {
                Paintable paintable = applicationConnection.getPaintable(uidl
                        .getId());
                highlight(paintable);
                if (event != null && event.getNativeEvent().getShiftKey()) {
                    applicationConnection.highlightComponent(paintable);
                }
            }

            super.select(event);
        }

        public void dir() {
            remove(0);

            String nodeName = uidl.getTag();
            try {
                Integer.parseInt(nodeName);
                nodeName = getNodeName(uidl, conf, nodeName);
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
                    final UIDL c = (UIDL) child;
                    final UIDLItem childItem = new UIDLItem(c, conf);
                    add(childItem);

                } catch (final Exception e) {
                    addItem(child.toString());
                }
            }
            if (highlightedPid != null && highlightedPid.equals(uidl.getId())) {
                getElement().getStyle().setBackgroundColor("#fdd");
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
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

    static void highlight(Paintable paintable) {
        Widget w = (Widget) paintable;
        if (w != null) {
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