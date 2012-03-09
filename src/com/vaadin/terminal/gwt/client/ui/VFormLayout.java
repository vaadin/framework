/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VTooltip;

/**
 * Two col Layout that places caption on left col and field on right col
 */
public class VFormLayout extends SimplePanel {

    private final static String CLASSNAME = "v-formlayout";

    ApplicationConnection client;
    VFormLayoutTable table;

    public VFormLayout() {
        super();
        setStyleName(CLASSNAME);
        table = new VFormLayoutTable();
        setWidget(table);
    }

    /**
     * Parses the stylenames from shared state
     * 
     * @param state
     *            shared state of the component
     * @return An array of stylenames
     */
    private String[] getStylesFromState(ComponentState state) {
        List<String> styles = new ArrayList<String>();
        if (state.hasStyles()) {
            String[] stylesnames = state.getStyle().split(" ");
            for (String name : stylesnames) {
                styles.add(name);
            }
        }

        if (!state.isEnabled()) {
            styles.add(ApplicationConnection.DISABLED_CLASSNAME);
        }

        return styles.toArray(new String[styles.size()]);
    }

    public class VFormLayoutTable extends FlexTable implements ClickHandler {

        private static final int COLUMN_CAPTION = 0;
        private static final int COLUMN_ERRORFLAG = 1;
        private static final int COLUMN_WIDGET = 2;

        private HashMap<Widget, Caption> widgetToCaption = new HashMap<Widget, Caption>();
        private HashMap<Widget, ErrorFlag> widgetToError = new HashMap<Widget, ErrorFlag>();

        public VFormLayoutTable() {
            DOM.setElementProperty(getElement(), "cellPadding", "0");
            DOM.setElementProperty(getElement(), "cellSpacing", "0");
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
            final VMarginInfo margins = new VMarginInfo(
                    uidl.getIntAttribute("margins"));

            Element margin = getElement();
            setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_TOP,
                    margins.hasTop());
            setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                    margins.hasRight());
            setStyleName(margin,
                    CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM,
                    margins.hasBottom());
            setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                    margins.hasLeft());

            setStyleName(margin, CLASSNAME + "-" + "spacing",
                    uidl.hasAttribute("spacing"));

            int i = 0;
            for (final Iterator<?> it = uidl.getChildIterator(); it.hasNext(); i++) {
                prepareCell(i, 1);
                final UIDL childUidl = (UIDL) it.next();
                final ComponentConnector childPaintable = client
                        .getPaintable(childUidl);
                Widget childWidget = childPaintable.getWidget();
                Caption caption = widgetToCaption.get(childWidget);
                if (caption == null) {
                    caption = new Caption(childPaintable, client);
                    caption.addClickHandler(this);
                    widgetToCaption.put(childWidget, caption);
                }
                ErrorFlag error = widgetToError.get(childWidget);
                if (error == null) {
                    error = new ErrorFlag();
                    widgetToError.put(childWidget, error);
                }
                prepareCell(i, COLUMN_WIDGET);

                Widget oldWidget = getWidget(i, COLUMN_WIDGET);
                if (oldWidget == null) {
                    setWidget(i, COLUMN_WIDGET, childWidget);
                } else if (oldWidget != childWidget) {
                    final ComponentConnector oldPaintable = ConnectorMap.get(
                            client).getConnector(oldWidget);
                    client.unregisterPaintable(oldPaintable);
                    setWidget(i, COLUMN_WIDGET, childWidget);
                }
                getCellFormatter().setStyleName(i, COLUMN_WIDGET,
                        CLASSNAME + "-contentcell");
                getCellFormatter().setStyleName(i, COLUMN_CAPTION,
                        CLASSNAME + "-captioncell");
                setWidget(i, COLUMN_CAPTION, caption);

                getCellFormatter().setStyleName(i, COLUMN_ERRORFLAG,
                        CLASSNAME + "-errorcell");
                setWidget(i, COLUMN_ERRORFLAG, error);

                childPaintable.updateFromUIDL(childUidl, client);

                // Update cell width when isRelativeWidth has been udpated
                if (childPaintable.isRelativeWidth()) {
                    getCellFormatter().setWidth(i, COLUMN_WIDGET, "100%");
                } else {
                    getCellFormatter().setWidth(i, COLUMN_WIDGET, null);
                }

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
                Widget w = getWidget(i, COLUMN_WIDGET);
                final ComponentConnector p = ConnectorMap.get(client)
                        .getConnector(w);
                client.unregisterPaintable(p);
                widgetToCaption.remove(w);
                removeRow(i);
            }

            /*
             * Must update relative sized fields last when it is clear how much
             * space they are allowed to use
             */
            for (Widget p : widgetToCaption.keySet()) {
                client.handleComponentRelativeSize(p);
            }
        }

        public void updateCaption(ComponentConnector paintable, UIDL uidl) {
            final Caption c = widgetToCaption.get(paintable.getWidget());
            if (c != null) {
                c.updateCaption(uidl, paintable.getState());
            }
            final ErrorFlag e = widgetToError.get(paintable.getWidget());
            if (e != null) {
                e.updateFromUIDL(uidl, paintable);
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
         * .event.dom.client.ClickEvent)
         */
        public void onClick(ClickEvent event) {
            Caption caption = (Caption) event.getSource();
            if (caption.getOwner() != null) {
                if (caption.getOwner() instanceof Focusable) {
                    ((Focusable) caption.getOwner()).focus();
                } else if (caption.getOwner() instanceof com.google.gwt.user.client.ui.Focusable) {
                    ((com.google.gwt.user.client.ui.Focusable) caption
                            .getOwner()).setFocus(true);
                }
            }
        }
    }

    // TODO why duplicated here?
    public class Caption extends HTML {

        public static final String CLASSNAME = "v-caption";

        private final ComponentConnector owner;

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
        public Caption(ComponentConnector component,
                ApplicationConnection client) {
            super();
            this.client = client;
            owner = component;

            sinkEvents(VTooltip.TOOLTIP_EVENTS);
        }

        private void setStyles(String[] styles) {
            String styleName = CLASSNAME;

            if (styles != null) {
                for (String style : styles) {
                    if (ApplicationConnection.DISABLED_CLASSNAME.equals(style)) {
                        // Add v-disabled also without classname prefix so
                        // generic v-disabled CSS rules work
                        styleName += " " + style;
                    }

                    styleName += " " + CLASSNAME + "-" + style;
                }
            }

            setStyleName(styleName);
        }

        public void updateCaption(UIDL uidl, ComponentState state) {
            setVisible(!uidl.getBooleanAttribute("invisible"));

            // Update styles as they might have changed when the caption changed
            setStyles(getStylesFromState(state));

            boolean isEmpty = true;

            if (state.getIcon() != null) {
                if (icon == null) {
                    icon = new Icon(client);

                    DOM.insertChild(getElement(), icon.getElement(), 0);
                }
                icon.setUri(state.getIcon().getURL());
                isEmpty = false;
            } else {
                if (icon != null) {
                    DOM.removeChild(getElement(), icon.getElement());
                    icon = null;
                }

            }

            if (state.getCaption() != null) {
                if (captionText == null) {
                    captionText = DOM.createSpan();
                    DOM.insertChild(getElement(), captionText, icon == null ? 0
                            : 1);
                }
                String c = state.getCaption();
                if (c == null) {
                    c = "";
                } else {
                    isEmpty = false;
                }
                DOM.setInnerText(captionText, c);
            } else {
                // TODO should span also be removed
            }

            if (state.hasDescription() && captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }

            if (uidl.getBooleanAttribute(AbstractComponentConnector.ATTRIBUTE_REQUIRED)) {
                if (requiredFieldIndicator == null) {
                    requiredFieldIndicator = DOM.createSpan();
                    DOM.setInnerText(requiredFieldIndicator, "*");
                    DOM.setElementProperty(requiredFieldIndicator, "className",
                            "v-required-field-indicator");
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
            // FIXME: This could be unnecessary for IE8+
            if (BrowserInfo.get().isIE()) {
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
        public ComponentConnector getOwner() {
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
        private static final String CLASSNAME = VFormLayout.CLASSNAME
                + "-error-indicator";
        Element errorIndicatorElement;
        private ComponentConnector owner;

        public ErrorFlag() {
            setStyleName(CLASSNAME);
            sinkEvents(VTooltip.TOOLTIP_EVENTS);
        }

        public void updateFromUIDL(UIDL uidl, ComponentConnector component) {
            owner = component;
            if (uidl.hasAttribute("error")
                    && !uidl.getBooleanAttribute(AbstractComponentConnector.ATTRIBUTE_HIDEERRORS)) {
                if (errorIndicatorElement == null) {
                    errorIndicatorElement = DOM.createDiv();
                    DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                    DOM.setElementProperty(errorIndicatorElement, "className",
                            "v-errorindicator");
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
}
