/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.shared.ui.colorpicker.AbstractColorPickerState;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.colorpicker.ColorPickerServerRpc;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * An abstract class that defines default implementation for a color picker
 * component.
 *
 * @since 7.0.0
 */
public abstract class AbstractColorPicker extends AbstractField<Color> {

    /**
     * Interface for converting 2d-coordinates to a Color.
     */
    public interface Coordinates2Color extends Serializable {

        /**
         * Calculates a color from coordinates.
         *
         * @param x
         *            the x-coordinate
         * @param y
         *            the y-coordinate
         *
         * @return the color
         */
        public Color calculate(int x, int y);

        /**
         * Calculates coordinates from a color.
         *
         * @param c
         *            the c
         *
         * @return the integer array with the coordinates
         */
        public int[] calculate(Color c);
    }

    /**
     * The style of the color picker popup.
     */
    public enum PopupStyle {
        /** A full popup with all tabs visible. */
        POPUP_NORMAL("normal"),

        /** A simple popup with only the swatches (palette) tab. */
        POPUP_SIMPLE("simple");

        private final String style;

        PopupStyle(String styleName) {
            style = styleName;
        }

        @Override
        public String toString() {
            return style;
        }
    }

    private ColorPickerServerRpc rpc = this::showPopup;

    protected static final String STYLENAME_DEFAULT = "v-colorpicker";
    protected static final String STYLENAME_BUTTON = "v-button";
    protected static final String STYLENAME_AREA = "v-colorpicker-area";

    protected PopupStyle popupStyle = PopupStyle.POPUP_NORMAL;

    private ColorPickerPopup window;

    /** The currently selected color. */
    protected Color color;

    private UI parent;

    private String popupCaption = null;
    private int positionX = 0;
    private int positionY = 0;

    protected boolean rgbVisible = true;
    protected boolean hsvVisible = true;
    protected boolean swatchesVisible = true;
    protected boolean historyVisible = true;
    protected boolean textfieldVisible = true;

    /**
     * Instantiates a new color picker.
     */
    public AbstractColorPicker() {
        this("Colors", Color.WHITE);
    }

    /**
     * Instantiates a new color picker.
     *
     * @param popupCaption
     *            the caption of the popup window
     */
    public AbstractColorPicker(String popupCaption) {
        this(popupCaption, Color.WHITE);
    }

    /**
     * Instantiates a new color picker.
     *
     * @param popupCaption
     *            the caption of the popup window
     * @param initialColor
     *            the initial color
     */
    public AbstractColorPicker(String popupCaption, Color initialColor) {
        super();
        registerRpc(rpc);
        setValue(initialColor);
        this.popupCaption = popupCaption;
        setDefaultStyles();
        setCaption("");
    }

    /**
     * Returns the current selected color of this color picker.
     *
     * @return the selected color, not null
     */
    @Override
    public Color getValue() {
        return color;
    }

    /**
     * Sets the selected color of this color picker. If the new color is not
     * equal to getValue(), fires a {@link ValueChangeEvent}.
     *
     * @param color
     *            the new selected color, not null
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     */
    @Override
    public void setValue(Color color) {
        Objects.requireNonNull(color, "color cannot be null");
        super.setValue(color);
    }

    /**
     * Set true if the component should show a default caption (css-code for the
     * currently selected color, e.g. #ffffff) when no other caption is
     * available.
     *
     * @param enabled
     *            {@code true} to enable the default caption, {@code false} to
     *            disable
     */
    public void setDefaultCaptionEnabled(boolean enabled) {
        getState().showDefaultCaption = enabled;
    }

    /**
     * Returns true if the component shows the default caption (css-code for the
     * currently selected color, e.g. #ffffff) if no other caption is available.
     *
     * @return {@code true} if the default caption is enabled, {@code false}
     *         otherwise
     */
    public boolean isDefaultCaptionEnabled() {
        return getState(false).showDefaultCaption;
    }

    /**
     * Sets the position of the popup window.
     *
     * @param x
     *            the x-coordinate
     * @param y
     *            the y-coordinate
     */
    public void setPosition(int x, int y) {
        positionX = x;
        positionY = y;

        if (window != null) {
            window.setPositionX(x);
            window.setPositionY(y);
        }
    }

    /**
     * Sets the style of the popup window.
     *
     * @param style
     *            the popup window style
     */
    public void setPopupStyle(PopupStyle style) {
        popupStyle = style;

        switch (style) {
        case POPUP_NORMAL:
            setRGBVisibility(true);
            setHSVVisibility(true);
            setSwatchesVisibility(true);
            setHistoryVisibility(true);
            setTextfieldVisibility(true);
            break;

        case POPUP_SIMPLE:
            setRGBVisibility(false);
            setHSVVisibility(false);
            setSwatchesVisibility(true);
            setHistoryVisibility(false);
            setTextfieldVisibility(false);
            break;

        default:
            assert false : "Unknown popup style " + style;
        }
    }

    /**
     * Gets the style for the popup window.
     *
     * @since 7.5.0
     * @return popup window style
     */
    public PopupStyle getPopupStyle() {
        return popupStyle;
    }

    /**
     * Sets the visibility of the RGB tab.
     *
     * @param visible
     *            {@code true} to display the RGB tab, {@code false} to hide it
     */
    public void setRGBVisibility(boolean visible) {

        if (!visible && !hsvVisible && !swatchesVisible) {
            throw new IllegalArgumentException("Cannot hide all tabs.");
        }

        rgbVisible = visible;
        if (window != null) {
            window.setRGBTabVisible(visible);
        }
    }

    /**
     * Gets the visibility of the RGB Tab.
     *
     * @since 7.5.0
     * @return visibility of the RGB tab
     */
    public boolean getRGBVisibility() {
        return rgbVisible;
    }

    /**
     * Sets the visibility of the HSV Tab.
     *
     * @param visible
     *            {@code true} to display the HSV tab, {@code false} to hide it
     */
    public void setHSVVisibility(boolean visible) {
        if (!visible && !rgbVisible && !swatchesVisible) {
            throw new IllegalArgumentException("Cannot hide all tabs.");
        }

        hsvVisible = visible;
        if (window != null) {
            window.setHSVTabVisible(visible);
        }
    }

    /**
     * Gets the visibility of the HSV tab.
     *
     * @since 7.5.0
     * @return {@code true} if the HSV tab is currently displayed, {@code false}
     *         otherwise
     */
    public boolean getHSVVisibility() {
        return hsvVisible;
    }

    /**
     * Sets the visibility of the Swatches (palette) tab.
     *
     * @param visible
     *            {@code true} to display the Swatches tab, {@code false} to
     *            hide it
     */
    public void setSwatchesVisibility(boolean visible) {
        if (!visible && !hsvVisible && !rgbVisible) {
            throw new IllegalArgumentException("Cannot hide all tabs.");
        }

        swatchesVisible = visible;
        if (window != null) {
            window.setSwatchesTabVisible(visible);
        }
    }

    /**
     * Gets the visibility of the Swatches (palette) tab.
     *
     * @since 7.5.0
     * @return {@code true} if the Swatches tab is currently displayed,
     *         {@code false} otherwise
     */
    public boolean getSwatchesVisibility() {
        return swatchesVisible;
    }

    /**
     * Sets the visibility of the color history, displaying recently picked
     * colors.
     *
     * @param visible
     *            {@code true} to display the history, {@code false} to hide it
     */
    public void setHistoryVisibility(boolean visible) {
        historyVisible = visible;
        if (window != null) {
            window.setHistoryVisible(visible);
        }
    }

    /**
     * Gets the visibility of the Color history.
     *
     * @since 7.5.0
     * @return {@code true} if the history is currently displayed, {@code false}
     *         otherwise
     */
    public boolean getHistoryVisibility() {
        return historyVisible;
    }

    /**
     * Sets the visibility of the CSS color code text field.
     *
     * @param visible
     *            {@code true} to display the CSS text field, {@code false} to
     *            hide it
     */
    public void setTextfieldVisibility(boolean visible) {
        textfieldVisible = visible;
        if (window != null) {
            window.setPreviewVisible(visible);
        }
    }

    /**
     * Gets the visibility of CSS color code text field.
     *
     * @since 7.5.0
     * @return {@code true} if the CSS text field is currently displayed,
     *         {@code false} otherwise
     */
    public boolean getTextfieldVisibility() {
        return textfieldVisible;
    }

    @Override
    protected AbstractColorPickerState getState() {
        return (AbstractColorPickerState) super.getState();
    }

    @Override
    protected AbstractColorPickerState getState(boolean markAsDirty) {
        return (AbstractColorPickerState) super.getState(markAsDirty);
    }

    /**
     * Sets the default styles of the component.
     */
    protected abstract void setDefaultStyles();

    /**
     * Shows a popup-window for color selection.
     */
    public void showPopup() {
        showPopup(true);
    }

    /**
     * Hides a popup-window for color selection.
     */
    public void hidePopup() {
        showPopup(false);
    }

    /**
     * Shows or hides the popup window depending on the given parameter. If
     * there is no such window yet, one is created.
     *
     * @param open
     *            {@code true} to display the popup, {@code false} to hide it
     */
    protected void showPopup(boolean open) {
        if (open && !isReadOnly()) {
            if (parent == null) {
                parent = getUI();
            }

            Color color = getValue();

            if (window == null) {
                window = new ColorPickerPopup(color);
                window.setCaption(popupCaption);

                window.setRGBTabVisible(rgbVisible);
                window.setHSVTabVisible(hsvVisible);
                window.setSwatchesTabVisible(swatchesVisible);
                window.setHistoryVisible(historyVisible);
                window.setPreviewVisible(textfieldVisible);

                window.addCloseListener(
                        event -> getState().popupVisible = false);
                window.addValueChangeListener(
                        event -> setValue(event.getValue()));

                window.getHistory().setValue(color);
                window.setPositionX(positionX);
                window.setPositionY(positionY);
                window.setVisible(true);

                parent.addWindow(window);
                window.focus();

            } else if (!parent.equals(window.getParent())) {

                window.setRGBTabVisible(rgbVisible);
                window.setHSVTabVisible(hsvVisible);
                window.setSwatchesTabVisible(swatchesVisible);
                window.setHistoryVisible(historyVisible);
                window.setPreviewVisible(textfieldVisible);

                window.setValue(color);
                window.getHistory().setValue(color);
                window.setVisible(true);

                parent.addWindow(window);
                window.focus();
            }

        } else if (window != null) {
            window.setVisible(false);
            parent.removeWindow(window);
        }
        getState().popupVisible = open;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);

        Attributes attributes = design.attributes();
        if (design.hasAttr("color")) {
            // Ignore the # character
            String hexColor = DesignAttributeHandler
                    .readAttribute("color", attributes, String.class)
                    .substring(1);
            doSetValue(new Color(Integer.parseInt(hexColor, 16)));
        }
        if (design.hasAttr("popup-style")) {
            setPopupStyle(PopupStyle.valueOf(
                    "POPUP_" + attributes.get("popup-style").toUpperCase()));
        }
        if (design.hasAttr("position")) {
            String[] position = attributes.get("position").split(",");
            setPosition(Integer.parseInt(position[0]),
                    Integer.parseInt(position[1]));
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);

        Attributes attribute = design.attributes();
        DesignAttributeHandler.writeAttribute("color", attribute,
                getValue().getCSS(), Color.WHITE.getCSS(), String.class,
                designContext);
        DesignAttributeHandler.writeAttribute("popup-style", attribute,
                popupStyle == PopupStyle.POPUP_NORMAL ? "normal" : "simple",
                "normal", String.class, designContext);
        DesignAttributeHandler.writeAttribute("position", attribute,
                positionX + "," + positionY, "0,0", String.class,
                designContext);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add("color");
        result.add("position");
        result.add("popup-style");
        return result;
    }

    @Override
    protected void doSetValue(Color color) {
        this.color = color;
        getState().color = color.getCSS();
    }

    @Override
    public Color getEmptyValue() {
        return Color.WHITE;
    }
}
