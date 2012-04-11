/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.formlayout;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.terminal.gwt.client.ui.VMarginInfo;

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
     * @param enabled
     * @return An array of stylenames
     */
    private String[] getStylesFromState(ComponentState state, boolean enabled) {
        List<String> styles = new ArrayList<String>();
        if (state.hasStyles()) {
            for (String name : state.getStyles()) {
                styles.add(name);
            }
        }

        if (!enabled) {
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

        public void setMargins(VMarginInfo margins) {
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

        }

        public void setSpacing(boolean spacing) {
            setStyleName(getElement(), CLASSNAME + "-" + "spacing", spacing);

        }

        public void setRowCount(int rowNr) {
            for (int i = 0; i < rowNr; i++) {
                prepareCell(i, COLUMN_CAPTION);
                getCellFormatter().setStyleName(i, COLUMN_CAPTION,
                        CLASSNAME + "-captioncell");

                prepareCell(i, 1);
                getCellFormatter().setStyleName(i, COLUMN_ERRORFLAG,
                        CLASSNAME + "-errorcell");

                prepareCell(i, 2);
                getCellFormatter().setStyleName(i, COLUMN_WIDGET,
                        CLASSNAME + "-contentcell");

                String rowstyles = CLASSNAME + "-row";
                if (i == 0) {
                    rowstyles += " " + CLASSNAME + "-firstrow";
                }
                if (i == rowNr - 1) {
                    rowstyles += " " + CLASSNAME + "-lastrow";
                }

                getRowFormatter().setStyleName(i, rowstyles);

            }
            while (getRowCount() != rowNr) {
                removeRow(rowNr);
            }
        }

        public void setChild(int rowNr, Widget childWidget, Caption caption,
                ErrorFlag error) {
            setWidget(rowNr, COLUMN_WIDGET, childWidget);
            setWidget(rowNr, COLUMN_CAPTION, caption);
            setWidget(rowNr, COLUMN_ERRORFLAG, error);

            widgetToCaption.put(childWidget, caption);
            widgetToError.put(childWidget, error);

        }

        public Caption getCaption(Widget childWidget) {
            return widgetToCaption.get(childWidget);
        }

        public ErrorFlag getError(Widget childWidget) {
            return widgetToError.get(childWidget);
        }

        public void cleanReferences(Widget oldChildWidget) {
            widgetToError.remove(oldChildWidget);
            widgetToCaption.remove(oldChildWidget);

        }

        public void updateCaption(Widget widget, ComponentState state,
                boolean enabled) {
            final Caption c = widgetToCaption.get(widget);
            if (c != null) {
                c.updateCaption(state, enabled);
            }
        }

        public void updateError(Widget widget, String errorMessage,
                boolean hideErrors) {
            final ErrorFlag e = widgetToError.get(widget);
            if (e != null) {
                e.updateError(errorMessage, hideErrors);
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

        /**
         * 
         * @param component
         *            optional owner of caption. If not set, getOwner will
         *            return null
         */
        public Caption(ComponentConnector component) {
            super();
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

        public void updateCaption(ComponentState state, boolean enabled) {
            // Update styles as they might have changed when the caption changed
            setStyles(getStylesFromState(state, enabled));

            boolean isEmpty = true;

            if (state.getIcon() != null) {
                if (icon == null) {
                    icon = new Icon(owner.getConnection());

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

            boolean required = owner instanceof AbstractFieldConnector
                    && ((AbstractFieldConnector) owner).isRequired();
            if (required) {
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
            owner.getConnection().handleTooltipEvent(event, owner);
        }
    }

    class ErrorFlag extends HTML {
        private static final String CLASSNAME = VFormLayout.CLASSNAME
                + "-error-indicator";
        Element errorIndicatorElement;

        private ComponentConnector owner;

        public ErrorFlag(ComponentConnector owner) {
            setStyleName(CLASSNAME);
            sinkEvents(VTooltip.TOOLTIP_EVENTS);
            this.owner = owner;
        }

        public void updateError(String errorMessage, boolean hideErrors) {
            boolean showError = null != errorMessage;
            if (hideErrors) {
                showError = false;
            }

            if (showError) {
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
