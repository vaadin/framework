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

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.VCaption;

public class VAbsoluteLayout extends ComplexPanel {

    /** Tag name for widget creation */
    public static final String TAGNAME = "absolutelayout";

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-absolutelayout";

    private DivElement marginElement;

    protected final Element canvas = DOM.createDiv();

    /**
     * Default constructor
     */
    public VAbsoluteLayout() {
        setElement(Document.get().createDivElement());
        marginElement = Document.get().createDivElement();
        canvas.getStyle().setProperty("position", "relative");
        canvas.getStyle().setProperty("overflow", "hidden");
        marginElement.appendChild(canvas);
        getElement().appendChild(marginElement);
        setStyleName(CLASSNAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Panel#add(com.google.gwt.user.client.ui
     * .Widget)
     */
    @Override
    public void add(Widget child) {
        AbsoluteWrapper wrapper = new AbsoluteWrapper(child);
        wrapper.updateStyleNames();
        super.add(wrapper, canvas);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.ComplexPanel#remove(com.google.gwt.user
     * .client.ui.Widget)
     */
    @Override
    public boolean remove(Widget w) {
        AbsoluteWrapper wrapper = getChildWrapper(w);
        if (wrapper != null) {
            wrapper.destroy();
            return super.remove(wrapper);
        }
        return super.remove(w);
    }

    /**
     * Does this layout contain a widget
     * 
     * @param widget
     *            The widget to check
     * @return Returns true if the widget is in this layout, false if not
     */
    public boolean contains(Widget widget) {
        return getChildWrapper(widget) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.ComplexPanel#getWidget(int)
     */
    @Override
    public Widget getWidget(int index) {
        for (int i = 0, j = 0; i < super.getWidgetCount(); i++) {
            Widget w = super.getWidget(i);
            if (w instanceof AbsoluteWrapper) {
                if (j == index) {
                    return w;
                } else {
                    j++;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.ComplexPanel#getWidgetCount()
     */
    @Override
    public int getWidgetCount() {
        int counter = 0;
        for (int i = 0; i < super.getWidgetCount(); i++) {
            if (super.getWidget(i) instanceof AbsoluteWrapper) {
                counter++;
            }
        }
        return counter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.ComplexPanel#getWidgetIndex(com.google.
     * gwt.user.client.ui.Widget)
     */
    @Override
    public int getWidgetIndex(Widget child) {
        for (int i = 0, j = 0; i < super.getWidgetCount(); i++) {
            Widget w = super.getWidget(i);
            if (w instanceof AbsoluteWrapper) {
                if (child == w) {
                    return j;
                } else {
                    j++;
                }
            }
        }
        return -1;
    }

    /**
     * Sets a caption for a contained widget
     * 
     * @param child
     *            The child widget to set the caption for
     * @param caption
     *            The caption of the widget
     */
    public void setWidgetCaption(Widget child, VCaption caption) {
        AbsoluteWrapper wrapper = getChildWrapper(child);
        if (wrapper != null) {
            if (caption != null) {
                if (!getChildren().contains(caption)) {
                    super.add(caption, canvas);
                }
                wrapper.setCaption(caption);
                caption.updateCaption();
                wrapper.updateCaptionPosition();
            } else if (wrapper.getCaption() != null) {
                wrapper.setCaption(null);
            }
        }
    }

    /**
     * Set the position of the widget in the layout. The position is a CSS
     * property string using properties such as top,left,right,top
     * 
     * @param child
     *            The child widget to set the position for
     * @param position
     *            The position string
     */
    public void setWidgetPosition(Widget child, String position) {
        AbsoluteWrapper wrapper = getChildWrapper(child);
        if (wrapper != null) {
            wrapper.setPosition(position);
        }
    }

    /**
     * Get the caption for a widget
     * 
     * @param child
     *            The child widget to get the caption of
     */
    public VCaption getWidgetCaption(Widget child) {
        AbsoluteWrapper wrapper = getChildWrapper(child);
        if (wrapper != null) {
            return wrapper.getCaption();
        }
        return null;
    }

    /**
     * Get the pixel width of an slot in the layout
     * 
     * @param child
     *            The widget in the layout.
     * @return Returns the size in pixels, or 0 if child is not in the layout
     */
    public int getWidgetSlotWidth(Widget child) {
        AbsoluteWrapper wrapper = getChildWrapper(child);
        if (wrapper != null) {
            return wrapper.getOffsetWidth();
        }
        return 0;
    }

    /**
     * Get the pixel height of an slot in the layout
     * 
     * @param child
     *            The widget in the layout
     * @return Returns the size in pixels, or 0 if the child is not in the
     *         layout
     */
    public int getWidgetSlotHeight(Widget child) {
        AbsoluteWrapper wrapper = getChildWrapper(child);
        if (wrapper != null) {
            return wrapper.getOffsetHeight();
        }
        return 0;
    }

    /**
     * Get the wrapper for a widget
     * 
     * @param child
     *            The child to get the wrapper for
     * @return
     */
    protected AbsoluteWrapper getChildWrapper(Widget child) {
        for (Widget w : getChildren()) {
            if (w instanceof AbsoluteWrapper) {
                AbsoluteWrapper wrapper = (AbsoluteWrapper) w;
                if (wrapper.getWidget() == child) {
                    return wrapper;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.UIObject#setStylePrimaryName(java.lang.
     * String)
     */
    @Override
    public void setStylePrimaryName(String style) {
        updateStylenames(style);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.UIObject#setStyleName(java.lang.String)
     */
    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStylenames(style);
        addStyleName(StyleConstants.UI_LAYOUT);
    }

    /**
     * Updates all style names contained in the layout
     * 
     * @param primaryStyleName
     *            The style name to use as primary
     */
    protected void updateStylenames(String primaryStyleName) {
        super.setStylePrimaryName(primaryStyleName);
        canvas.setClassName(getStylePrimaryName() + "-canvas");
        canvas.setClassName(getStylePrimaryName() + "-margin");
        for (Widget w : getChildren()) {
            if (w instanceof AbsoluteWrapper) {
                AbsoluteWrapper wrapper = (AbsoluteWrapper) w;
                wrapper.updateStyleNames();
            }
        }
    }

    /**
     * Performs a vertical layout of the layout. Should be called when a widget
     * is added or removed
     */
    public void layoutVertically() {
        layout();
    }

    /**
     * Performs an horizontal layout. Should be called when a widget is add or
     * removed
     */
    public void layoutHorizontally() {
        layout();
    }

    private void layout() {
        for (Widget widget : getChildren()) {
            if (widget instanceof AbsoluteWrapper) {
                AbsoluteWrapper wrapper = (AbsoluteWrapper) widget;
                wrapper.updateCaptionPosition();
            }
        }
    }

    /**
     * Cleanup old wrappers which have been left empty by other inner layouts
     * moving the widget from the wrapper into their own hierarchy. This usually
     * happens when a call to setWidget(widget) is done in an inner layout which
     * automatically detaches the widget from the parent, in this case the
     * wrapper, and re-attaches it somewhere else. This has to be done in the
     * layout phase since the order of the hierarchy events are not defined.
     */
    public void cleanupWrappers() {
        for (Widget widget : getChildren()) {
            if (widget instanceof AbsoluteWrapper) {
                AbsoluteWrapper wrapper = (AbsoluteWrapper) widget;
                if (wrapper.getWidget() == null) {
                    wrapper.destroy();
                    super.remove(wrapper);
                    continue;
                }
            }
        }
    }

    /**
     * Sets style names for the wrapper wrapping the widget in the layout. The
     * style names will be prefixed with v-absolutelayout-wrapper.
     * 
     * @param widget
     *            The widget which wrapper we want to add the stylenames to
     * @param stylenames
     *            The style names that should be added to the wrapper
     */
    public void setWidgetWrapperStyleNames(Widget widget, String... stylenames) {
        AbsoluteWrapper wrapper = getChildWrapper(widget);
        if (wrapper == null) {
            throw new IllegalArgumentException(
                    "No wrapper for widget found, has the widget been added to the layout?");
        }
        wrapper.setWrapperStyleNames(stylenames);
    }

    /**
     * Internal wrapper for wrapping widgets in the Absolute layout
     */
    protected class AbsoluteWrapper extends SimplePanel {
        private String css;
        private String left;
        private String top;
        private String right;
        private String bottom;
        private String zIndex;

        private VCaption caption;
        private String[] extraStyleNames;

        /**
         * Constructor
         * 
         * @param child
         *            The child to wrap
         */
        public AbsoluteWrapper(Widget child) {
            setWidget(child);
        }

        /**
         * Get the caption of the wrapper
         */
        public VCaption getCaption() {
            return caption;
        }

        /**
         * Set the caption for the wrapper
         * 
         * @param caption
         *            The caption for the wrapper
         */
        public void setCaption(VCaption caption) {
            if (caption != null) {
                this.caption = caption;
            } else if (this.caption != null) {
                this.caption.removeFromParent();
                this.caption = caption;
            }
        }

        /**
         * Removes the wrapper caption and itself from the layout
         */
        public void destroy() {
            if (caption != null) {
                caption.removeFromParent();
            }
            removeFromParent();
        }

        /**
         * Set the position for the wrapper in the layout
         * 
         * @param position
         *            The position string
         */
        public void setPosition(String position) {
            if (css == null || !css.equals(position)) {
                css = position;
                top = right = bottom = left = zIndex = null;
                if (!css.equals("")) {
                    String[] properties = css.split(";");
                    for (int i = 0; i < properties.length; i++) {
                        String[] keyValue = properties[i].split(":");
                        if (keyValue[0].equals("left")) {
                            left = keyValue[1];
                        } else if (keyValue[0].equals("top")) {
                            top = keyValue[1];
                        } else if (keyValue[0].equals("right")) {
                            right = keyValue[1];
                        } else if (keyValue[0].equals("bottom")) {
                            bottom = keyValue[1];
                        } else if (keyValue[0].equals("z-index")) {
                            zIndex = keyValue[1];
                        }
                    }
                }
                // ensure ne values
                Style style = getElement().getStyle();
                /*
                 * IE8 dies when nulling zIndex, even in IE7 mode. All other css
                 * properties (and even in older IE's) accept null values just
                 * fine. Assign empty string instead of null.
                 */
                if (zIndex != null) {
                    style.setProperty("zIndex", zIndex);
                } else {
                    style.setProperty("zIndex", "");
                }
                style.setProperty("top", top);
                style.setProperty("left", left);
                style.setProperty("right", right);
                style.setProperty("bottom", bottom);

            }
            updateCaptionPosition();
        }

        /**
         * Updates the caption position by using element offset left and top
         */
        private void updateCaptionPosition() {
            if (caption != null) {
                Style style = caption.getElement().getStyle();
                style.setProperty("position", "absolute");
                style.setPropertyPx("left", getElement().getOffsetLeft());
                style.setPropertyPx("top", getElement().getOffsetTop()
                        - caption.getHeight());
            }
        }

        /**
         * Sets the style names of the wrapper. Will be prefixed with the
         * v-absolutelayout-wrapper prefix
         * 
         * @param stylenames
         *            The wrapper style names
         */
        public void setWrapperStyleNames(String... stylenames) {
            extraStyleNames = stylenames;
            updateStyleNames();
        }

        /**
         * Updates the style names using the primary style name as prefix
         */
        protected void updateStyleNames() {
            setStyleName(VAbsoluteLayout.this.getStylePrimaryName()
                    + "-wrapper");
            if (extraStyleNames != null) {
                for (String stylename : extraStyleNames) {
                    addStyleDependentName(stylename);
                }
            }
        }
    }
}
