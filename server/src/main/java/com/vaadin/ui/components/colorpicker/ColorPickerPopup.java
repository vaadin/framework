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
package com.vaadin.ui.components.colorpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker.Coordinates2Color;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * A component that represents color selection popup within a color picker.
 *
 * @since 7.0.0
 */
public class ColorPickerPopup extends Window implements HasValue<Color> {

    private static final String STYLENAME = "v-colorpicker-popup";

    /** The tabs. */
    private final TabSheet tabs = new TabSheet();

    private Component rgbTab;

    private Component hsvTab;

    private Component swatchesTab;

    /** The layout. */
    private final VerticalLayout layout;

    /** The ok button. */
    private final Button ok = new Button("OK");

    /** The cancel button. */
    private final Button cancel = new Button("Cancel");

    /** The resize button. */
    private final Button resize = new Button("show/hide history");

    /** The previously selected color. */
    private Color previouslySelectedColor = Color.WHITE;

    /** The selected color. */
    private Color selectedColor = Color.WHITE;

    /** The history. */
    private ColorPickerHistory history;

    /** The history container. */
    private Layout historyContainer;

    /** The rgb gradient. */
    private ColorPickerGradient rgbGradient;

    /** The hsv gradient. */
    private ColorPickerGradient hsvGradient;

    /** The red slider. */
    private Slider redSlider;

    /** The green slider. */
    private Slider greenSlider;

    /** The blue slider. */
    private Slider blueSlider;

    /** The hue slider. */
    private Slider hueSlider;

    /** The saturation slider. */
    private Slider saturationSlider;

    /** The value slider. */
    private Slider valueSlider;

    /** The preview on the rgb tab. */
    private ColorPickerPreview rgbPreview;

    /** The preview on the hsv tab. */
    private ColorPickerPreview hsvPreview;

    /** The preview on the swatches tab. */
    private ColorPickerPreview selPreview;

    /** The color select. */
    private ColorPickerSelect colorSelect;

    /** The selectors. */
    private final Set<HasValue<Color>> selectors = new HashSet<>();

    private boolean readOnly;

    private boolean required;

    /**
     * Set true while the slider values are updated after colorChange. When
     * true, valueChange reactions from the sliders are disabled, because
     * otherwise the set color may become corrupted as it is repeatedly re-set
     * in valueChangeListeners using values from sliders that may not have been
     * updated yet.
     */
    private boolean updatingColors = false;

    private ColorPickerPopup() {
        // Set the layout
        layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setWidth("100%");
        layout.setHeight(null);

        setContent(layout);
        setStyleName(STYLENAME);
        setResizable(false);
        // Create the history
        history = new ColorPickerHistory();
        history.addValueChangeListener(this::colorChanged);
    }

    /**
     * Instantiates a new color picker popup.
     *
     * @param initialColor
     *            the initially selected color
     */
    public ColorPickerPopup(Color initialColor) {
        this();
        selectedColor = initialColor;
        initContents();
    }

    private void initContents() {
        // Create the preview on the rgb tab
        rgbPreview = new ColorPickerPreview(selectedColor);
        rgbPreview.setWidth("240px");
        rgbPreview.setHeight("20px");
        rgbPreview.addValueChangeListener(this::colorChanged);
        selectors.add(rgbPreview);

        // Create the preview on the hsv tab
        hsvPreview = new ColorPickerPreview(selectedColor);
        hsvPreview.setWidth("240px");
        hsvPreview.setHeight("20px");
        hsvPreview.addValueChangeListener(this::colorChanged);
        selectors.add(hsvPreview);

        // Create the preview on the swatches tab
        selPreview = new ColorPickerPreview(selectedColor);
        selPreview.setWidth("100%");
        selPreview.setHeight("20px");
        selPreview.setRequiredIndicatorVisible(required);
        selPreview.addValueChangeListener(this::colorChanged);
        selectors.add(selPreview);

        // Create the tabs
        rgbTab = createRGBTab(selectedColor);
        tabs.addTab(rgbTab, "RGB", null);

        hsvTab = createHSVTab(selectedColor);
        tabs.addTab(hsvTab, "HSV", null);

        swatchesTab = createSelectTab();
        tabs.addTab(swatchesTab, "Swatches", null);

        // Add the tabs
        tabs.setWidth("100%");

        layout.addComponent(tabs);

        // Add the history
        history.setWidth("97%");
        history.setHeight("22px");

        // Create the default colors
        List<Color> defaultColors = new ArrayList<>();
        defaultColors.add(Color.BLACK);
        defaultColors.add(Color.WHITE);

        // Create the history
        VerticalLayout innerContainer = new VerticalLayout();
        innerContainer.setWidth("100%");
        innerContainer.setHeight(null);
        innerContainer.addComponent(history);

        VerticalLayout outerContainer = new VerticalLayout();
        outerContainer.setWidth("99%");
        outerContainer.setHeight("27px");
        outerContainer.addComponent(innerContainer);
        historyContainer = outerContainer;

        layout.addComponent(historyContainer);

        // Add the resize button for the history
        resize.addClickListener(this::resizeButtonClick);
        resize.setData(false);
        resize.setWidth("100%");
        resize.setHeight("10px");
        resize.setPrimaryStyleName("resize-button");
        layout.addComponent(resize);

        // Add the buttons
        ok.setWidth("70px");
        ok.addClickListener(this::okButtonClick);

        cancel.setWidth("70px");
        cancel.addClickListener(this::cancelButtonClick);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(ok);
        buttons.addComponent(cancel);
        buttons.setWidth("100%");
        buttons.setHeight("30px");
        buttons.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
        buttons.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
        layout.addComponent(buttons);
    }

    /**
     * Creates the RGB tab.
     *
     * @return the component
     */
    private Component createRGBTab(Color color) {
        VerticalLayout rgbLayout = new VerticalLayout();
        rgbLayout.setMargin(new MarginInfo(false, false, true, false));
        rgbLayout.addComponent(rgbPreview);
        rgbLayout.setStyleName("rgbtab");

        // Add the RGB color gradient
        rgbGradient = new ColorPickerGradient("rgb-gradient", rgbConverter);
        rgbGradient.setValue(color);
        rgbGradient.addValueChangeListener(this::colorChanged);
        rgbLayout.addComponent(rgbGradient);
        selectors.add(rgbGradient);

        // Add the RGB sliders
        VerticalLayout sliders = new VerticalLayout();
        sliders.setStyleName("rgb-sliders");

        redSlider = createRGBSlider("Red", "red");
        greenSlider = createRGBSlider("Green", "green");
        blueSlider = createRGBSlider("Blue", "blue");
        setRgbSliderValues(color);

        redSlider.addValueChangeListener(e -> {
            double red = e.getValue();
            if (!updatingColors) {
                Color newColor = new Color((int) red, selectedColor.getGreen(),
                        selectedColor.getBlue());
                setValue(newColor);
            }
        });

        sliders.addComponent(redSlider);

        greenSlider.addValueChangeListener(e -> {
            double green = e.getValue();
            if (!updatingColors) {
                Color newColor = new Color(selectedColor.getRed(), (int) green,
                        selectedColor.getBlue());
                setValue(newColor);
            }
        });
        sliders.addComponent(greenSlider);

        blueSlider.addValueChangeListener(e -> {
            double blue = e.getValue();
            if (!updatingColors) {
                Color newColor = new Color(selectedColor.getRed(),
                        selectedColor.getGreen(), (int) blue);
                setValue(newColor);
            }
        });
        sliders.addComponent(blueSlider);

        rgbLayout.addComponent(sliders);

        return rgbLayout;
    }

    private Slider createRGBSlider(String caption, String styleName) {
        Slider redSlider = new Slider(caption, 0, 255);
        redSlider.setStyleName("rgb-slider");
        redSlider.setWidth("220px");
        redSlider.addStyleName(styleName);
        return redSlider;
    }

    /**
     * Creates the hsv tab.
     *
     * @return the component
     */
    private Component createHSVTab(Color color) {
        VerticalLayout hsvLayout = new VerticalLayout();
        hsvLayout.setMargin(new MarginInfo(false, false, true, false));
        hsvLayout.addComponent(hsvPreview);
        hsvLayout.setStyleName("hsvtab");

        // Add the hsv gradient
        hsvGradient = new ColorPickerGradient("hsv-gradient", hsvConverter);
        hsvGradient.setValue(color);
        hsvGradient.addValueChangeListener(this::colorChanged);
        hsvLayout.addComponent(hsvGradient);
        selectors.add(hsvGradient);

        VerticalLayout sliders = new VerticalLayout();
        sliders.setStyleName("hsv-sliders");

        hueSlider = new Slider("Hue", 0, 360);
        saturationSlider = new Slider("Saturation", 0, 100);
        valueSlider = new Slider("Value", 0, 100);

        float[] hsv = color.getHSV();
        setHsvSliderValues(hsv);

        hueSlider.setStyleName("hsv-slider");
        hueSlider.addStyleName("hue-slider");
        hueSlider.setWidth("220px");
        hueSlider.addValueChangeListener(event -> {
            if (!updatingColors) {
                float hue = Float.parseFloat(event.getValue().toString())
                        / 360f;
                float saturation = Float.parseFloat(
                        saturationSlider.getValue().toString()) / 100f;
                float value = Float
                        .parseFloat(valueSlider.getValue().toString()) / 100f;

                // Set the color
                Color newColor = new Color(
                        Color.HSVtoRGB(hue, saturation, value));
                setValue(newColor);

                /*
                 * Set the background color of the hue gradient. This has to be
                 * done here since in the conversion the base color information
                 * is lost when color is black/white
                 */
                Color bgColor = new Color(Color.HSVtoRGB(hue, 1f, 1f));
                hsvGradient.setBackgroundColor(bgColor);
            }
        });
        sliders.addComponent(hueSlider);

        saturationSlider.setStyleName("hsv-slider");
        saturationSlider.setWidth("220px");
        saturationSlider.addValueChangeListener(event -> {
            if (!updatingColors) {
                float hue = Float.parseFloat(hueSlider.getValue().toString())
                        / 360f;
                float saturation = Float.parseFloat(event.getValue().toString())
                        / 100f;
                float value = Float
                        .parseFloat(valueSlider.getValue().toString()) / 100f;
                Color newColor = new Color(
                        Color.HSVtoRGB(hue, saturation, value));
                setValue(newColor);
            }
        });
        sliders.addComponent(saturationSlider);

        valueSlider.setStyleName("hsv-slider");
        valueSlider.setWidth("220px");
        valueSlider.addValueChangeListener(event -> {
            if (!updatingColors) {
                float hue = Float.parseFloat(hueSlider.getValue().toString())
                        / 360f;
                float saturation = Float.parseFloat(
                        saturationSlider.getValue().toString()) / 100f;
                float value = Float.parseFloat(event.getValue().toString())
                        / 100f;

                Color newColor = new Color(
                        Color.HSVtoRGB(hue, saturation, value));
                setValue(newColor);
            }
        });

        sliders.addComponent(valueSlider);
        hsvLayout.addComponent(sliders);

        return hsvLayout;
    }

    /**
     * Creates the select tab.
     *
     * @return the component
     */
    private Component createSelectTab() {
        VerticalLayout selLayout = new VerticalLayout();
        selLayout.setMargin(new MarginInfo(false, false, true, false));
        selLayout.addComponent(selPreview);
        selLayout.addStyleName("seltab");

        colorSelect = new ColorPickerSelect();
        colorSelect.addValueChangeListener(this::colorChanged);
        selLayout.addComponent(colorSelect);

        return selLayout;
    }

    private void resizeButtonClick(ClickEvent event) {

        boolean minimize = (Boolean) resize.getData();
        if (minimize) {
            historyContainer.setHeight("27px");
            history.setHeight("22px");
        } else {
            historyContainer.setHeight("90px");
            history.setHeight("85px");
        }

        resize.setData(!minimize);
    }

    private void okButtonClick(ClickEvent event) {
        fireEvent(new ValueChangeEvent<>(this, previouslySelectedColor, true));
        close();
    }

    private void cancelButtonClick(ClickEvent event) {
        close();
    }

    /**
     * Gets the history.
     *
     * @return the history
     */
    public ColorPickerHistory getHistory() {
        return history;
    }

    /**
     * Sets the value of this object. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent}. Throws
     * {@code NullPointerException} if the value is null.
     *
     * @param color
     *            the new value, not {@code null}
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     */
    @Override
    public void setValue(Color color) {
        Objects.requireNonNull(color, "color cannot be null");

        previouslySelectedColor = selectedColor;
        selectedColor = color;

        hsvGradient.setValue(selectedColor);
        hsvPreview.setValue(selectedColor);

        rgbGradient.setValue(selectedColor);
        rgbPreview.setValue(selectedColor);

        selPreview.setValue(selectedColor);
    }

    @Override
    public Color getValue() {
        return selectedColor;
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<Color> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        return addListener(ValueChangeEvent.class, listener,
                ValueChangeListener.VALUE_CHANGE_METHOD);
    }

    /**
     * Gets the color history.
     *
     * @return the color history
     */
    public List<Color> getColorHistory() {
        return Collections.unmodifiableList(history.getHistory());
    }

    private void colorChanged(ValueChangeEvent<Color> event) {
        setValue(event.getValue());

        updatingColors = true;

        setRgbSliderValues(selectedColor);
        float[] hsv = selectedColor.getHSV();
        setHsvSliderValues(hsv);

        updatingColors = false;

        for (HasValue<Color> s : selectors) {
            if (event.getSource() != s && s != this
                    && s.getValue() != selectedColor) {
                s.setValue(selectedColor);
            }
        }
    }

    private void setRgbSliderValues(Color color) {
        try {
            redSlider.setValue(((Integer) color.getRed()).doubleValue());
            blueSlider.setValue(((Integer) color.getBlue()).doubleValue());
            greenSlider.setValue(((Integer) color.getGreen()).doubleValue());
        } catch (ValueOutOfBoundsException e) {
            getLogger().log(Level.WARNING,
                    "Unable to set RGB color value to " + color.getRed() + ","
                            + color.getGreen() + "," + color.getBlue(),
                    e);
        }
    }

    private void setHsvSliderValues(float[] hsv) {
        try {
            hueSlider.setValue(((Float) (hsv[0] * 360f)).doubleValue());
            saturationSlider.setValue(((Float) (hsv[1] * 100f)).doubleValue());
            valueSlider.setValue(((Float) (hsv[2] * 100f)).doubleValue());
        } catch (ValueOutOfBoundsException e) {
            getLogger().log(Level.WARNING, "Unable to set HSV color value to "
                    + hsv[0] + "," + hsv[1] + "," + hsv[2], e);
        }
    }

    /**
     * Checks the visibility of the given tab
     *
     * @param tab
     *            The tab to check
     * @return true if tab is visible, false otherwise
     */
    private boolean isTabVisible(Component tab) {
        for (Component child : tabs) {
            if (child == tab) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if tabs are needed and hides them if not
     */
    private void checkIfTabsNeeded() {
        tabs.setTabsVisible(tabs.getComponentCount() > 1);
    }

    /**
     * Sets the RGB tab visibility.
     *
     * @param visible
     *            The visibility of the RGB tab
     */
    public void setRGBTabVisible(boolean visible) {
        if (visible && !isTabVisible(rgbTab)) {
            tabs.addTab(rgbTab, "RGB", null);
            checkIfTabsNeeded();
        } else if (!visible && isTabVisible(rgbTab)) {
            tabs.removeComponent(rgbTab);
            checkIfTabsNeeded();
        }
    }

    /**
     * Sets the HSV tab visibility.
     *
     * @param visible
     *            The visibility of the HSV tab
     */
    public void setHSVTabVisible(boolean visible) {
        if (visible && !isTabVisible(hsvTab)) {
            tabs.addTab(hsvTab, "HSV", null);
            checkIfTabsNeeded();
        } else if (!visible && isTabVisible(hsvTab)) {
            tabs.removeComponent(hsvTab);
            checkIfTabsNeeded();
        }
    }

    /**
     * Sets the visibility of the Swatches tab.
     *
     * @param visible
     *            The visibility of the Swatches tab
     */
    public void setSwatchesTabVisible(boolean visible) {
        if (visible && !isTabVisible(swatchesTab)) {
            tabs.addTab(swatchesTab, "Swatches", null);
            checkIfTabsNeeded();
        } else if (!visible && isTabVisible(swatchesTab)) {
            tabs.removeComponent(swatchesTab);
            checkIfTabsNeeded();
        }
    }

    /**
     * Sets the visibility of the History.
     *
     * @param visible
     *            {@code true} to show the history, {@code false} to hide it
     */
    public void setHistoryVisible(boolean visible) {
        historyContainer.setVisible(visible);
        resize.setVisible(visible);
    }

    /**
     * Sets the preview visibility.
     *
     * @param visible
     *            {@code true} to show the preview, {@code false} to hide it
     */
    public void setPreviewVisible(boolean visible) {
        hsvPreview.setVisible(visible);
        rgbPreview.setVisible(visible);
        selPreview.setVisible(visible);
    }

    /** An RGB color converter. */
    private Coordinates2Color rgbConverter = new Coordinates2Color() {

        @Override
        public Color calculate(int x, int y) {
            float h = x / 220f;
            float s = 1f;
            float v = 1f;

            if (y < 110) {
                s = y / 110f;
            } else if (y > 110) {
                v = 1f - (y - 110f) / 110f;
            }

            return new Color(Color.HSVtoRGB(h, s, v));
        }

        @Override
        public int[] calculate(Color color) {

            float[] hsv = color.getHSV();

            int x = Math.round(hsv[0] * 220f);
            int y = 0;

            // lower half
            if (hsv[1] == 1f) {
                y = Math.round(110f - (hsv[1] + hsv[2]) * 110f);
            } else {
                y = Math.round(hsv[1] * 110f);
            }

            return new int[] { x, y };
        }
    };

    /** An HSV color converter. */
    private Coordinates2Color hsvConverter = new Coordinates2Color() {
        @Override
        public int[] calculate(Color color) {

            float[] hsv = color.getHSV();

            // Calculate coordinates
            int x = Math.round(hsv[2] * 220.0f);
            int y = Math.round(220 - hsv[1] * 220.0f);

            // Create background color of clean color
            Color bgColor = new Color(Color.HSVtoRGB(hsv[0], 1f, 1f));
            hsvGradient.setBackgroundColor(bgColor);

            return new int[] { x, y };
        }

        @Override
        public Color calculate(int x, int y) {
            float saturation = 1f - y / 220.0f;
            float value = x / 220.0f;
            float hue = Float.parseFloat(hueSlider.getValue().toString())
                    / 360f;

            Color color = new Color(Color.HSVtoRGB(hue, saturation, value));
            return color;
        }
    };

    @Override
    public void setRequiredIndicatorVisible(boolean visible) {
        required = visible;
        if (selPreview != null) {
            selPreview.setRequiredIndicatorVisible(required);
        }
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return required;
    }

    private static Logger getLogger() {
        return Logger.getLogger(ColorPickerPopup.class.getName());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        updateColorComponents();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    private void updateColorComponents() {
        if (getContent() != null) {
            updateColorComponents(getContent());
        }
    }

    private void updateColorComponents(Component component) {
        if (component instanceof HasValue<?>) {
            ((HasValue<?>) component).setReadOnly(isReadOnly());
            ((HasValue<?>) component)
                    .setRequiredIndicatorVisible(isRequiredIndicatorVisible());
        }
        if (component instanceof HasComponents) {
            Iterator<Component> iterator = ((HasComponents) component)
                    .iterator();
            while (iterator.hasNext()) {
                updateColorComponents(iterator.next());
            }
        }
    }
}
