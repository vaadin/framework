/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.ITooltip;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

/**
 * Two col Layout that places caption on left col and field on right col
 */
public class IFormLayout extends SimplePanel implements Container {

    private final static String CLASSNAME = "i-formlayout";

    private ApplicationConnection client;
    private IFormLayoutTable table;

    private String width = "";
    private String height = "";

    private boolean rendering = false;

    public IFormLayout() {
        super();
        setStylePrimaryName(CLASSNAME);
        table = new IFormLayoutTable();
        setWidget(table);
    }

    public class IFormLayoutTable extends FlexTable {

        private static final int COLUMN_CAPTION = 0;
        private static final int COLUMN_ERRORFLAG = 1;
        private static final int COLUMN_WIDGET = 2;

        private HashMap<Paintable, Caption> componentToCaption = new HashMap<Paintable, Caption>();
        private HashMap<Paintable, ErrorFlag> componentToError = new HashMap<Paintable, ErrorFlag>();

        public IFormLayoutTable() {
            DOM.setElementProperty(getElement(), "cellPadding", "0");
            DOM.setElementProperty(getElement(), "cellSpacing", "0");
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
            final IMarginInfo margins = new IMarginInfo(uidl
                    .getIntAttribute("margins"));

            Element margin = getElement();
            setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_TOP,
                    margins.hasTop());
            setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                    margins.hasRight());
            setStyleName(margin,
                    CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM, margins
                            .hasBottom());
            setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                    margins.hasLeft());

            setStyleName(margin, CLASSNAME + "-" + "spacing", uidl
                    .hasAttribute("spacing"));

            int i = 0;
            for (final Iterator it = uidl.getChildIterator(); it.hasNext(); i++) {
                prepareCell(i, 1);
                final UIDL childUidl = (UIDL) it.next();
                final Paintable p = client.getPaintable(childUidl);
                Caption caption = componentToCaption.get(p);
                if (caption == null) {
                    caption = new Caption(p, client);
                    componentToCaption.put(p, caption);
                }
                ErrorFlag error = componentToError.get(p);
                if (error == null) {
                    error = new ErrorFlag();
                    componentToError.put(p, error);
                }
                prepareCell(i, COLUMN_WIDGET);
                final Paintable oldComponent = (Paintable) getWidget(i,
                        COLUMN_WIDGET);
                if (oldComponent == null) {
                    setWidget(i, COLUMN_WIDGET, (Widget) p);
                } else if (oldComponent != p) {
                    client.unregisterPaintable(oldComponent);
                    setWidget(i, COLUMN_WIDGET, (Widget) p);
                }
                getCellFormatter().setStyleName(i, COLUMN_WIDGET,
                        CLASSNAME + "-contentcell");
                getCellFormatter().setStyleName(i, COLUMN_CAPTION,
                        CLASSNAME + "-captioncell");
                setWidget(i, COLUMN_CAPTION, caption);

                setContentWidth(i);

                getCellFormatter().setStyleName(i, COLUMN_ERRORFLAG,
                        CLASSNAME + "-errorcell");
                setWidget(i, COLUMN_ERRORFLAG, error);

                p.updateFromUIDL(childUidl, client);

                String rowstyles = CLASSNAME + "-row";
                if (i == 0) {
                    rowstyles += " " + CLASSNAME + "-firstrow";
                }
                if (!it.hasNext()) {
                    rowstyles += " " + CLASSNAME + "-lastrow";
                }

                getRowFormatter().setStyleName(i, rowstyles);

            }

            while (getRowCount() > i) {
                final Paintable p = (Paintable) getWidget(i, COLUMN_WIDGET);
                client.unregisterPaintable(p);
                componentToCaption.remove(p);
                removeRow(i);
            }

            /*
             * Must update relative sized fields last when it is clear how much
             * space they are allowed to use
             */
            for (Paintable p : componentToCaption.keySet()) {
                client.handleComponentRelativeSize((Widget) p);
            }
        }

        public void setContentWidths() {
            for (int row = 0; row < getRowCount(); row++) {
                setContentWidth(row);
            }
        }

        private void setContentWidth(int row) {
            String width = "";
            if (!isDynamicWidth()) {
                width = "100%";
            }
            getCellFormatter().setWidth(row, COLUMN_WIDGET, width);
        }

        public void replaceChildComponent(Widget oldComponent,
                Widget newComponent) {
            int i;
            for (i = 0; i < getRowCount(); i++) {
                Widget candidate = getWidget(i, COLUMN_WIDGET);
                if (oldComponent == candidate) {
                    final Caption newCap = new Caption(
                            (Paintable) newComponent, client);
                    componentToCaption.put((Paintable) newComponent, newCap);
                    ErrorFlag error = componentToError.get(newComponent);
                    if (error == null) {
                        error = new ErrorFlag();
                        componentToError.put((Paintable) newComponent, error);
                    }

                    setWidget(i, COLUMN_CAPTION, newCap);
                    setWidget(i, COLUMN_ERRORFLAG, error);
                    setWidget(i, COLUMN_WIDGET, newComponent);
                    break;
                }
            }

        }

        public boolean hasChildComponent(Widget component) {
            return componentToCaption.containsKey(component);
        }

        public void updateCaption(Paintable component, UIDL uidl) {
            final Caption c = componentToCaption.get(component);
            if (c != null) {
                c.updateCaption(uidl);
            }
            final ErrorFlag e = componentToError.get(component);
            if (e != null) {
                e.updateFromUIDL(uidl, component);
            }

        }

        public int getAllocatedWidth(Widget child, int availableWidth) {
            Caption caption = componentToCaption.get(child);
            ErrorFlag error = componentToError.get(child);
            int width = availableWidth;

            if (caption != null) {
                width -= DOM.getParent(caption.getElement()).getOffsetWidth();
            }
            if (error != null) {
                width -= DOM.getParent(error.getElement()).getOffsetWidth();
            }

            return width;
        }

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;

        this.client = client;

        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }

        table.updateFromUIDL(uidl, client);

        rendering = false;
    }

    public boolean isDynamicWidth() {
        return width.equals("");
    }

    public boolean hasChildComponent(Widget component) {
        return table.hasChildComponent(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        table.replaceChildComponent(oldComponent, newComponent);
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        table.updateCaption(component, uidl);
    }

    public class Caption extends HTML {

        public static final String CLASSNAME = "i-caption";

        private final Paintable owner;

        private Element requiredFieldIndicator;

        private Icon icon;

        private Element captionText;

        private final ApplicationConnection client;

        /**
         * 
         * @param component
         *            optional owner of caption. If not set, getOwner will
         *            return null
         * @param client
         */
        public Caption(Paintable component, ApplicationConnection client) {
            super();
            this.client = client;
            owner = component;
            setStyleName(CLASSNAME);
            sinkEvents(ITooltip.TOOLTIP_EVENTS);
        }

        public void updateCaption(UIDL uidl) {
            setVisible(!uidl.getBooleanAttribute("invisible"));

            setStyleName(getElement(), "i-disabled", uidl
                    .hasAttribute("disabled"));

            boolean isEmpty = true;

            if (uidl.hasAttribute("icon")) {
                if (icon == null) {
                    icon = new Icon(client);

                    DOM.insertChild(getElement(), icon.getElement(), 0);
                }
                icon.setUri(uidl.getStringAttribute("icon"));
                isEmpty = false;
            } else {
                if (icon != null) {
                    DOM.removeChild(getElement(), icon.getElement());
                    icon = null;
                }

            }

            if (uidl.hasAttribute("caption")) {
                if (captionText == null) {
                    captionText = DOM.createSpan();
                    DOM.insertChild(getElement(), captionText, icon == null ? 0
                            : 1);
                }
                String c = uidl.getStringAttribute("caption");
                if (c == null) {
                    c = "";
                } else {
                    isEmpty = false;
                }
                DOM.setInnerText(captionText, c);
            } else {
                // TODO should span also be removed
            }

            if (uidl.hasAttribute("description")) {
                if (captionText != null) {
                    addStyleDependentName("hasdescription");
                } else {
                    removeStyleDependentName("hasdescription");
                }
            }

            if (uidl.getBooleanAttribute("required")) {
                if (requiredFieldIndicator == null) {
                    requiredFieldIndicator = DOM.createSpan();
                    DOM.setInnerText(requiredFieldIndicator, "*");
                    DOM.setElementProperty(requiredFieldIndicator, "className",
                            "i-required-field-indicator");
                    DOM.appendChild(getElement(), requiredFieldIndicator);
                }
            } else {
                if (requiredFieldIndicator != null) {
                    DOM.removeChild(getElement(), requiredFieldIndicator);
                    requiredFieldIndicator = null;
                }
            }

            // Workaround for IE weirdness, sometimes returns bad height in some
            // circumstances when Caption is empty. See #1444
            // IE7 bugs more often. I wonder what happens when IE8 arrives...
            if (Util.isIE()) {
                if (isEmpty) {
                    setHeight("0px");
                    DOM.setStyleAttribute(getElement(), "overflow", "hidden");
                } else {
                    setHeight("");
                    DOM.setStyleAttribute(getElement(), "overflow", "");
                }

            }

        }

        /**
         * Returns Paintable for which this Caption belongs to.
         * 
         * @return owner Widget
         */
        public Paintable getOwner() {
            return owner;
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (client != null) {
                client.handleTooltipEvent(event, owner);
            }
        }
    }

    private class ErrorFlag extends HTML {
        private static final String CLASSNAME = IFormLayout.CLASSNAME
                + "-error-indicator";
        Element errorIndicatorElement;
        private Paintable owner;

        public ErrorFlag() {
            setStyleName(CLASSNAME);
            sinkEvents(ITooltip.TOOLTIP_EVENTS);
        }

        public void updateFromUIDL(UIDL uidl, Paintable component) {
            owner = component;
            if (uidl.hasAttribute("error")
                    && !uidl.getBooleanAttribute("hideErrors")) {
                if (errorIndicatorElement == null) {
                    errorIndicatorElement = DOM.createDiv();
                    DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                    DOM.setElementProperty(errorIndicatorElement, "className",
                            "i-errorindicator");
                    DOM.appendChild(getElement(), errorIndicatorElement);
                }

            } else if (errorIndicatorElement != null) {
                DOM.removeChild(getElement(), errorIndicatorElement);
                errorIndicatorElement = null;
            }
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (owner != null) {
                client.handleTooltipEvent(event, owner);
            }
        }

    }

    public boolean requestLayout(Set<Paintable> child) {
        if (height.equals("") || width.equals("")) {
            // A dynamic size might change due to children changes
            return false;
        }

        return true;
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        int width = 0;
        int height = 0;

        if (!this.width.equals("")) {
            int availableWidth = getOffsetWidth();
            width = table.getAllocatedWidth(child, availableWidth);
        }

        return new RenderSpace(width, height, false);
    }

    @Override
    public void setHeight(String height) {
        if (this.height.equals(height)) {
            return;
        }

        this.height = height;
        super.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width)) {
            return;
        }

        this.width = width;
        super.setWidth(width);

        if (!rendering) {
            table.setContentWidths();
            if (height.equals("")) {
                // Width might affect height
                Util.updateRelativeChildrenAndSendSizeUpdateEvent(client, this);
            }
        }
    }

}
