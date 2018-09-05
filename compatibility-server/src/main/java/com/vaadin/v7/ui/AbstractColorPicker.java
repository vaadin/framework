/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.shared.ui.colorpicker.Color;
import com.vaadin.v7.shared.ui.colorpicker.ColorPickerServerRpc;
import com.vaadin.v7.shared.ui.colorpicker.ColorPickerState;
import com.vaadin.v7.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.v7.ui.components.colorpicker.ColorChangeListener;
import com.vaadin.v7.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.v7.ui.components.colorpicker.ColorSelector;

/**
 * An abstract class that defines default implementation for a color picker
 * component.
 *
 * @since 7.0.0
 */
@Deprecated
public abstract class AbstractColorPicker extends AbstractLegacyComponent
        implements CloseListener, ColorSelector {
    private static final Method COLOR_CHANGE_METHOD;
    static {
        try {
            COLOR_CHANGE_METHOD = ColorChangeListener.class.getDeclaredMethod(
                    "colorChanged", new Class[] { ColorChangeEvent.class });
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(
                    "Internal error finding methods in ColorPicker");
        }
    }

    /**
     * Interface for converting 2d-coordinates to a Color.
     */
    @Deprecated
    public interface Coordinates2Color extends Serializable {

        /**
         * Calculate color from coordinates.
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
         * Calculate coordinates from color.
         *
         * @param c
         *            the c
         *
         * @return the integer array with the coordinates
         */
        public int[] calculate(Color c);
    }

    @Deprecated
    public enum PopupStyle {
        POPUP_NORMAL("normal"), POPUP_SIMPLE("simple");

        private String style;

        PopupStyle(String styleName) {
            style = styleName;
        }

        @Override
        public String toString() {
            return style;
        }
    }

    private ColorPickerServerRpc rpc = new ColorPickerServerRpc() {

        @Override
        public void openPopup(boolean open) {
            showPopup(open);
        }
    };

    protected static final String STYLENAME_DEFAULT = "v-colorpicker";
    protected static final String STYLENAME_BUTTON = "v-button";
    protected static final String STYLENAME_AREA = "v-colorpicker-area";

    protected PopupStyle popupStyle = PopupStyle.POPUP_NORMAL;

    /** The popup window. */
    private ColorPickerPopup window;

    /** The color. */
    protected Color color;

    /** The UI. */
    private UI parent;

    protected String popupCaption = null;
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
        setColor(initialColor);
        this.popupCaption = popupCaption;
        setDefaultStyles();
        setCaption("");
    }

    @Override
    public void setColor(Color color) {
        this.color = color;

        if (window != null) {
            window.setColor(color);
        }
        getState().color = color.getCSS();
    }

    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Set true if the component should show a default caption (css-code for the
     * currently selected color, e.g. #ffffff) when no other caption is
     * available.
     *
     * @param enabled
     */
    public void setDefaultCaptionEnabled(boolean enabled) {
        getState().showDefaultCaption = enabled;
    }

    /**
     * Returns true if the component shows the default caption (css-code for the
     * currently selected color, e.g. #ffffff) if no other caption is available.
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

    @Override
    public void addColorChangeListener(ColorChangeListener listener) {
        addListener(ColorChangeEvent.class, listener, COLOR_CHANGE_METHOD);
    }

    @Override
    public void removeColorChangeListener(ColorChangeListener listener) {
        removeListener(ColorChangeEvent.class, listener);
    }

    @Override
    public void windowClose(CloseEvent e) {
        if (e.getWindow() == window) {
            getState().popupVisible = false;
        }
    }

    /**
     * Fired when a color change event occurs.
     *
     * @param event
     *            The color change event
     */
    protected void colorChanged(ColorChangeEvent event) {
        setColor(event.getColor());
        fireColorChanged();
    }

    /**
     * Notifies the listeners that the selected color has changed.
     */
    public void fireColorChanged() {
        fireEvent(new ColorChangeEvent(this, color));
    }

    /**
     * The style for the popup window.
     *
     * @param style
     *            The style
     */
    public void setPopupStyle(PopupStyle style) {
        popupStyle = style;

        switch (style) {
        case POPUP_NORMAL: {
            setRGBVisibility(true);
            setHSVVisibility(true);
            setSwatchesVisibility(true);
            setHistoryVisibility(true);
            setTextfieldVisibility(true);
            break;
        }

        case POPUP_SIMPLE: {
            setRGBVisibility(false);
            setHSVVisibility(false);
            setSwatchesVisibility(true);
            setHistoryVisibility(false);
            setTextfieldVisibility(false);
            break;
        }
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
     * Set the visibility of the RGB Tab.
     *
     * @param visible
     *            The visibility
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
     * Set the visibility of the HSV Tab.
     *
     * @param visible
     *            The visibility
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
     * Gets the visibility of the HSV Tab.
     *
     * @since 7.5.0
     * @return visibility of the HSV tab
     */
    public boolean getHSVVisibility() {
        return hsvVisible;
    }

    /**
     * Set the visibility of the Swatches Tab.
     *
     * @param visible
     *            The visibility
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
     * Gets the visibility of the Swatches Tab.
     *
     * @since 7.5.0
     * @return visibility of the swatches tab
     */
    public boolean getSwatchesVisibility() {
        return swatchesVisible;
    }

    /**
     * Sets the visibility of the Color History.
     *
     * @param visible
     *            The visibility
     */
    public void setHistoryVisibility(boolean visible) {
        historyVisible = visible;
        if (window != null) {
            window.setHistoryVisible(visible);
        }
    }

    /**
     * Gets the visibility of the Color History.
     *
     * @since 7.5.0
     * @return visibility of color history
     */
    public boolean getHistoryVisibility() {
        return historyVisible;
    }

    /**
     * Sets the visibility of the CSS color code text field.
     *
     * @param visible
     *            The visibility
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
     * @return visibility of css color code text field
     */
    public boolean getTextfieldVisibility() {
        return textfieldVisible;
    }

    @Override
    protected ColorPickerState getState() {
        return (ColorPickerState) super.getState();
    }

    @Override
    protected ColorPickerState getState(boolean markAsDirty) {
        return (ColorPickerState) super.getState(markAsDirty);
    }

    /**
     * Sets the default styles of the component.
     *
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
     * Shows or hides popup-window depending on the given parameter. If there is
     * no such window yet, one is created.
     *
     * @param open
     */
    protected void showPopup(boolean open) {
        if (open && !isReadOnly()) {
            if (parent == null) {
                parent = getUI();
            }

            if (window == null) {

                // Create the popup
                window = new ColorPickerPopup(color);
                window.setCaption(popupCaption);

                window.setRGBTabVisible(rgbVisible);
                window.setHSVTabVisible(hsvVisible);
                window.setSwatchesTabVisible(swatchesVisible);
                window.setHistoryVisible(historyVisible);
                window.setPreviewVisible(textfieldVisible);

                window.addCloseListener(this);
                window.addColorChangeListener(new ColorChangeListener() {
                    @Override
                    public void colorChanged(ColorChangeEvent event) {
                        AbstractColorPicker.this.colorChanged(event);
                    }
                });

                window.getHistory().setColor(color);
                parent.addWindow(window);
                window.setVisible(true);
                window.setPositionX(positionX);
                window.setPositionY(positionY);

            } else if (!parent.equals(window.getParent())) {

                window.setRGBTabVisible(rgbVisible);
                window.setHSVTabVisible(hsvVisible);
                window.setSwatchesTabVisible(swatchesVisible);
                window.setHistoryVisible(historyVisible);
                window.setPreviewVisible(textfieldVisible);

                window.setColor(color);
                window.getHistory().setColor(color);
                window.setVisible(true);
                parent.addWindow(window);
            }

        } else if (window != null) {
            window.setVisible(false);
            parent.removeWindow(window);
        }
        getState().popupVisible = open;
    }

    /**
     * Set whether the caption text is rendered as HTML or not. You might need
     * to re-theme component to allow higher content than the original text
     * style.
     *
     * If set to true, the captions are passed to the browser as html and the
     * developer is responsible for ensuring no harmful html is used. If set to
     * false, the content is passed to the browser as plain text.
     *
     * @param htmlContentAllowed
     *            <code>true</code> if caption is rendered as HTML,
     *            <code>false</code> otherwise
     * @deprecated as of , use {@link #setCaptionAsHtml(boolean)} instead
     */
    @Deprecated
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        setCaptionAsHtml(htmlContentAllowed);
    }

    /**
     * Return HTML rendering setting.
     *
     * @return <code>true</code> if the caption text is to be rendered as HTML,
     *         <code>false</code> otherwise
     * @deprecated as of , use {@link #isCaptionAsHtml()} instead
     */
    @Deprecated
    public boolean isHtmlContentAllowed() {
        return isCaptionAsHtml();
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
            setColor(new Color(Integer.parseInt(hexColor, 16)));
        }
        if (design.hasAttr("popup-style")) {
            setPopupStyle(PopupStyle.valueOf("POPUP_"
                    + attributes.get("popup-style").toUpperCase(Locale.ROOT)));
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
                color.getCSS(), Color.WHITE.getCSS(), String.class,
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
}
