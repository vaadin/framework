/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VCssLayout extends SimplePanel {
    public static final String TAGNAME = "csslayout";
    public static final String CLASSNAME = "v-" + TAGNAME;

    FlowPane panel = new FlowPane();

    Element margin = DOM.createDiv();

    public VCssLayout() {
        super();
        getElement().appendChild(margin);
        setStyleName(CLASSNAME);
        margin.setClassName(CLASSNAME + "-margin");
        setWidget(panel);
    }

    @Override
    protected Element getContainerElement() {
        return margin;
    }

    public class FlowPane extends FlowPanel {

        private final HashMap<Widget, VCaption> widgetToCaption = new HashMap<Widget, VCaption>();
        private ApplicationConnection client;
        private int lastIndex;

        public FlowPane() {
            super();
            setStyleName(CLASSNAME + "-container");
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

            // for later requests
            this.client = client;

            final Collection<Widget> oldWidgets = new HashSet<Widget>();
            for (final Iterator<Widget> iterator = iterator(); iterator
                    .hasNext();) {
                oldWidgets.add(iterator.next());
            }

            ValueMap mapAttribute = null;
            if (uidl.hasAttribute("css")) {
                mapAttribute = uidl.getMapAttribute("css");
            }

            lastIndex = 0;
            for (final Iterator<Object> i = uidl.getChildIterator(); i
                    .hasNext();) {
                final UIDL r = (UIDL) i.next();
                final ComponentConnector child = client.getPaintable(r);
                final Widget widget = child.getWidget();
                if (widget.getParent() == this) {
                    oldWidgets.remove(widget);
                    VCaption vCaption = widgetToCaption.get(widget);
                    if (vCaption != null) {
                        addOrMove(vCaption, lastIndex++);
                        oldWidgets.remove(vCaption);
                    }
                }

                addOrMove(widget, lastIndex++);
                if (mapAttribute != null && mapAttribute.containsKey(r.getId())) {
                    String css = null;
                    try {
                        Style style = widget.getElement().getStyle();
                        css = mapAttribute.getString(r.getId());
                        String[] cssRules = css.split(";");
                        for (int j = 0; j < cssRules.length; j++) {
                            String[] rule = cssRules[j].split(":");
                            if (rule.length == 0) {
                                continue;
                            } else {
                                style.setProperty(
                                        makeCamelCase(rule[0].trim()),
                                        rule[1].trim());
                            }
                        }
                    } catch (Exception e) {
                        VConsole.log("CssLayout encounterd invalid css string: "
                                + css);
                    }
                }

            }

            // loop oldWidgetWrappers that where not re-attached and unregister
            // them
            for (Widget w : oldWidgets) {
                remove(w);
                ConnectorMap paintableMap = ConnectorMap.get(client);
                if (paintableMap.isConnector(w)) {
                    final ComponentConnector p = ConnectorMap.get(client)
                            .getConnector(w);
                    client.unregisterPaintable(p);
                }
                VCaption vCaption = widgetToCaption.remove(w);
                if (vCaption != null) {
                    remove(vCaption);
                }
            }
        }

        private void addOrMove(Widget child, int index) {
            if (child.getParent() == this) {
                int currentIndex = getWidgetIndex(child);
                if (index == currentIndex) {
                    return;
                }
            }
            insert(child, index);
        }

        public void updateCaption(ComponentConnector paintable) {
            Widget widget = paintable.getWidget();
            VCaption caption = widgetToCaption.get(widget);
            if (VCaption.isNeeded(paintable.getState())) {
                if (caption == null) {
                    caption = new VCaption(paintable, client);
                    widgetToCaption.put(widget, caption);
                    insert(caption, getWidgetIndex(widget));
                    lastIndex++;
                } else if (!caption.isAttached()) {
                    insert(caption, getWidgetIndex(widget));
                    lastIndex++;
                }
                caption.updateCaption();
            } else if (caption != null) {
                remove(caption);
                widgetToCaption.remove(widget);
            }
        }

        ComponentConnector getComponent(Element element) {
            return Util
                    .getConnectorForElement(client, VCssLayout.this, element);
        }

    }

    private static final String makeCamelCase(String cssProperty) {
        // TODO this might be cleaner to implement with regexp
        while (cssProperty.contains("-")) {
            int indexOf = cssProperty.indexOf("-");
            cssProperty = cssProperty.substring(0, indexOf)
                    + String.valueOf(cssProperty.charAt(indexOf + 1))
                            .toUpperCase() + cssProperty.substring(indexOf + 2);
        }
        if ("float".equals(cssProperty)) {
            if (BrowserInfo.get().isIE()) {
                return "styleFloat";
            } else {
                return "cssFloat";
            }
        }
        return cssProperty;
    }

    /**
     * Sets CSS classes for margin and spacing based on the given parameters.
     * 
     * @param margins
     *            A {@link VMarginInfo} object that provides info on
     *            top/left/bottom/right margins
     * @param spacing
     *            true to enable spacing, false otherwise
     */
    protected void setMarginAndSpacingStyles(VMarginInfo margins,
            boolean spacing) {
        setStyleName(margin, VCssLayout.CLASSNAME + "-"
                + StyleConstants.MARGIN_TOP, margins.hasTop());
        setStyleName(margin, VCssLayout.CLASSNAME + "-"
                + StyleConstants.MARGIN_RIGHT, margins.hasRight());
        setStyleName(margin, VCssLayout.CLASSNAME + "-"
                + StyleConstants.MARGIN_BOTTOM, margins.hasBottom());
        setStyleName(margin, VCssLayout.CLASSNAME + "-"
                + StyleConstants.MARGIN_LEFT, margins.hasLeft());

        setStyleName(margin, VCssLayout.CLASSNAME + "-" + "spacing", spacing);

    }
}
