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

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;
import com.vaadin.terminal.gwt.client.ui.VView;
import com.vaadin.terminal.gwt.client.ui.VWindow;

public class VUIDLBrowser extends Tree implements MouseOutHandler {
    /**
     * 
     */
    private final UIDL uidl;
    private ApplicationConfiguration conf;

    public VUIDLBrowser(final UIDL uidl, ApplicationConfiguration conf) {
        this.conf = conf;
        this.uidl = uidl;
        DOM.setStyleAttribute(getElement(), "position", "");

        final UIDLItem root = new UIDLItem(this.uidl, conf);
        addItem(root);
        addOpenHandler(new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> event) {
                TreeItem item = event.getTarget();
                if (item.getChildCount() == 1
                        && item.getChild(0).getText().equals("LOADING")) {
                    ((UIDLItem) item).dir();
                }
            }
        });

        addSelectionHandler(new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> event) {
                UIDLItem selectedItem = (UIDLItem) event.getSelectedItem();
                List<ApplicationConnection> runningApplications = ApplicationConfiguration
                        .getRunningApplications();

                // TODO this does not work properly with multiple application on
                // same
                // host page
                for (ApplicationConnection applicationConnection : runningApplications) {
                    Paintable paintable = applicationConnection
                            .getPaintable(selectedItem.uidl.getId());
                    highlight(paintable);
                }

            }
        });

        addMouseOutHandler(VUIDLBrowser.this);

    }

    @Override
    protected boolean isKeyboardNavigationEnabled(TreeItem currentItem) {
        return false;
    }

    class UIDLItem extends TreeItem {

        private UIDL uidl;

        UIDLItem(UIDL uidl, ApplicationConfiguration conf) {
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

        public void dir() {
            TreeItem temp = getChild(0);
            removeItem(temp);

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
                TreeItem tmp = null;
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
                        tmp = new TreeItem("variables");
                    }
                    tmp.addItem(name + "=" + value);
                }
                if (tmp != null) {
                    addItem(tmp);
                }
            } catch (final Exception e) {
                // Ignored, no variables
            }

            final Iterator<Object> i = uidl.getChildIterator();
            while (i.hasNext()) {
                final Object child = i.next();
                try {
                    final UIDL c = (UIDL) child;
                    final TreeItem childItem = new UIDLItem(c, conf);
                    addItem(childItem);

                } catch (final Exception e) {
                    addItem(child.toString());
                }
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

    private static void highlight(Paintable paintable) {
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

    private static void deHiglight() {
        if (highlight.getParentElement() != null) {
            highlight.getParentElement().removeChild(highlight);
        }
    }

    public void onMouseOut(MouseOutEvent event) {
        deHiglight();
    }

}