/**
 * 
 */
package com.vaadin.terminal.gwt.client;

import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;
import com.vaadin.terminal.gwt.client.ui.VView;

public class VUIDLBrowser extends Tree {
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

}