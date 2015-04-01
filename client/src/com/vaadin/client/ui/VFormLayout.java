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

package com.vaadin.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Focusable;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.VTooltip;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.MarginInfo;

/**
 * Two col Layout that places caption on left col and field on right col
 */
public class VFormLayout extends SimplePanel {

    private final static String CLASSNAME = "v-formlayout";

    /** For internal use only. May be removed or replaced in the future. */
    public VFormLayoutTable table;

    public VFormLayout() {
        super();
        setStyleName(CLASSNAME);
        addStyleName(StyleConstants.UI_LAYOUT);
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
    private String[] getStylesFromState(AbstractComponentState state,
            boolean enabled) {
        List<String> styles = new ArrayList<String>();
        if (ComponentStateUtil.hasStyles(state)) {
            for (String name : state.styles) {
                styles.add(name);
            }
        }

        if (!enabled) {
            styles.add(StyleConstants.DISABLED);
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

            Roles.getPresentationRole().set(getElement());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
         * .event.dom.client.ClickEvent)
         */
        @Override
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

        public void setMargins(MarginInfo margins) {
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

        public void updateCaption(Widget widget, AbstractComponentState state,
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
        }

        private void setStyles(String[] styles) {
            String styleName = CLASSNAME;

            if (styles != null) {
                for (String style : styles) {
                    if (StyleConstants.DISABLED.equals(style)) {
                        // Add v-disabled also without classname prefix so
                        // generic v-disabled CSS rules work
                        styleName += " " + style;
                    }

                    styleName += " " + CLASSNAME + "-" + style;
                }
            }

            setStyleName(styleName);
        }

        public void updateCaption(AbstractComponentState state, boolean enabled) {
            // Update styles as they might have changed when the caption changed
            setStyles(getStylesFromState(state, enabled));

            boolean isEmpty = true;

            if (icon != null) {
                getElement().removeChild(icon.getElement());
                icon = null;
            }
            if (state.resources.containsKey(ComponentConstants.ICON_RESOURCE)) {
                icon = owner.getConnection().getIcon(
                        state.resources.get(ComponentConstants.ICON_RESOURCE)
                                .getURL());
                DOM.insertChild(getElement(), icon.getElement(), 0);

                isEmpty = false;
            }

            if (state.caption != null) {
                if (captionText == null) {
                    captionText = DOM.createSpan();

                    AriaHelper.bindCaption(owner.getWidget(), captionText);

                    DOM.insertChild(getElement(), captionText, icon == null ? 0
                            : 1);
                }
                String c = state.caption;
                if (c == null) {
                    c = "";
                } else {
                    isEmpty = false;
                }
                DOM.setInnerText(captionText, c);
            } else {
                // TODO should span also be removed
            }

            if (state.description != null && captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }

            boolean required = owner instanceof AbstractFieldConnector
                    && ((AbstractFieldConnector) owner).isRequired();

            AriaHelper.handleInputRequired(owner.getWidget(), required);

            if (required) {
                if (requiredFieldIndicator == null) {
                    requiredFieldIndicator = DOM.createSpan();
                    DOM.setInnerText(requiredFieldIndicator, "*");
                    DOM.setElementProperty(requiredFieldIndicator, "className",
                            "v-required-field-indicator");
                    DOM.appendChild(getElement(), requiredFieldIndicator);

                    // Hide the required indicator from screen reader, as this
                    // information is set directly at the input field
                    Roles.getTextboxRole().setAriaHiddenState(
                            requiredFieldIndicator, true);
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
                    getElement().getStyle().setOverflow(Overflow.HIDDEN);
                } else {
                    setHeight("");
                    getElement().getStyle().clearOverflow();
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
    }

    /** For internal use only. May be removed or replaced in the future. */
    public class ErrorFlag extends HTML {
        private static final String CLASSNAME = VFormLayout.CLASSNAME
                + "-error-indicator";
        Element errorIndicatorElement;

        private ComponentConnector owner;

        public ErrorFlag(ComponentConnector owner) {
            setStyleName(CLASSNAME);

            if(!BrowserInfo.get().isTouchDevice()) {
                sinkEvents(VTooltip.TOOLTIP_EVENTS);
            }

            this.owner = owner;
        }

        public ComponentConnector getOwner() {
            return owner;
        }

        public void updateError(String errorMessage, boolean hideErrors) {
            boolean showError = null != errorMessage;
            if (hideErrors) {
                showError = false;
            }

            AriaHelper.handleInputInvalid(owner.getWidget(), showError);

            if (showError) {
                if (errorIndicatorElement == null) {
                    errorIndicatorElement = DOM.createDiv();
                    DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                    DOM.setElementProperty(errorIndicatorElement, "className",
                            "v-errorindicator");
                    DOM.appendChild(getElement(), errorIndicatorElement);

                    // Hide the error indicator from screen reader, as this
                    // information is set directly at the input field
                    Roles.getFormRole().setAriaHiddenState(
                            errorIndicatorElement, true);
                }

            } else if (errorIndicatorElement != null) {
                DOM.removeChild(getElement(), errorIndicatorElement);
                errorIndicatorElement = null;
            }
        }

    }
}
