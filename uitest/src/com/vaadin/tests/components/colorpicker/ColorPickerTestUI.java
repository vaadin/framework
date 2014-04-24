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
package com.vaadin.tests.components.colorpicker;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ColorPickerArea;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;

public class ColorPickerTestUI extends AbstractTestUI implements
        ColorChangeListener {

    @Override
    public String getTestDescription() {
        return "Vaadin 7 compatible ColorPicker";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9201;
    }

    /** The foreground color. */
    private Color foregroundColor = Color.BLACK; // The currently selected

    /** The background color. */
    private Color backgroundColor = Color.WHITE; // The currently selected

    // The display box where the image is rendered
    /** The display. */
    private Embedded display;

    private AbstractColorPicker colorpicker1;
    private AbstractColorPicker colorpicker2;
    private AbstractColorPicker colorpicker3;
    private AbstractColorPicker colorpicker4;
    private AbstractColorPicker colorpicker5;
    private AbstractColorPicker colorpicker6;

    private boolean rgbVisible = true;
    private boolean hsvVisible = true;
    private boolean swaVisible = true;
    private boolean historyVisible = true;
    private boolean txtfieldVisible = true;

    private final CheckBox rgbBox = new CheckBox("RGB tab visible");
    private final CheckBox hsvBox = new CheckBox("HSV tab visible");
    private final CheckBox swaBox = new CheckBox("Swatches tab visible");
    private final CheckBox hisBox = new CheckBox("History visible");
    private final CheckBox txtBox = new CheckBox("CSS field visible");

    /**
     * This class is used to represent the preview of the color selection.
     */
    public class MyImageSource implements StreamResource.StreamSource {

        /** The imagebuffer. */
        private java.io.ByteArrayOutputStream imagebuffer = null;

        /** The bg color. */
        private final java.awt.Color bgColor;

        /** The fg color. */
        private final java.awt.Color fgColor;

        /**
         * Instantiates a new my image source.
         * 
         * @param fg
         *            the foreground
         * @param bg
         *            the background
         */
        public MyImageSource(java.awt.Color fg, java.awt.Color bg) {
            fgColor = fg;
            bgColor = bg;
        }

        /* Must implement this method that returns the resource as a stream. */
        @Override
        public InputStream getStream() {

            /* Create an image and draw something on it. */
            BufferedImage image = new BufferedImage(270, 270,
                    BufferedImage.TYPE_INT_RGB);
            Graphics drawable = image.getGraphics();
            drawable.setColor(bgColor);
            drawable.fillRect(0, 0, 270, 270);
            drawable.setColor(fgColor);
            drawable.fillOval(25, 25, 220, 220);
            drawable.setColor(java.awt.Color.blue);
            drawable.drawRect(0, 0, 269, 269);
            drawable.setColor(java.awt.Color.black);
            drawable.drawString(
                    "r=" + String.valueOf(fgColor.getRed()) + ",g="
                            + String.valueOf(fgColor.getGreen()) + ",b="
                            + String.valueOf(fgColor.getBlue()), 50, 100);
            drawable.drawString(
                    "r=" + String.valueOf(bgColor.getRed()) + ",g="
                            + String.valueOf(bgColor.getGreen()) + ",b="
                            + String.valueOf(bgColor.getBlue()), 5, 15);

            try {
                /* Write the image to a buffer. */
                imagebuffer = new ByteArrayOutputStream();
                ImageIO.write(image, "png", imagebuffer);

                /* Return a stream from the buffer. */
                return new ByteArrayInputStream(imagebuffer.toByteArray());
            } catch (IOException e) {
                return null;
            }
        }
    }

    private void setPopupVisibilities() {

        rgbBox.setEnabled(!(rgbVisible && !hsvVisible && !swaVisible));
        hsvBox.setEnabled(!(!rgbVisible && hsvVisible && !swaVisible));
        swaBox.setEnabled(!(!rgbVisible && !hsvVisible && swaVisible));

        colorpicker1.setRGBVisibility(rgbVisible);
        colorpicker2.setRGBVisibility(rgbVisible);
        colorpicker3.setRGBVisibility(rgbVisible);
        colorpicker4.setRGBVisibility(rgbVisible);
        colorpicker5.setRGBVisibility(rgbVisible);
        colorpicker6.setRGBVisibility(rgbVisible);

        colorpicker1.setHSVVisibility(hsvVisible);
        colorpicker2.setHSVVisibility(hsvVisible);
        colorpicker3.setHSVVisibility(hsvVisible);
        colorpicker4.setHSVVisibility(hsvVisible);
        colorpicker5.setHSVVisibility(hsvVisible);
        colorpicker6.setHSVVisibility(hsvVisible);

        colorpicker1.setSwatchesVisibility(swaVisible);
        colorpicker2.setSwatchesVisibility(swaVisible);
        colorpicker3.setSwatchesVisibility(swaVisible);
        colorpicker4.setSwatchesVisibility(swaVisible);
        colorpicker5.setSwatchesVisibility(swaVisible);
        colorpicker6.setSwatchesVisibility(swaVisible);

        colorpicker1.setHistoryVisibility(historyVisible);
        colorpicker2.setHistoryVisibility(historyVisible);
        colorpicker3.setHistoryVisibility(historyVisible);
        colorpicker4.setHistoryVisibility(historyVisible);
        colorpicker5.setHistoryVisibility(historyVisible);
        colorpicker6.setHistoryVisibility(historyVisible);

        colorpicker1.setTextfieldVisibility(txtfieldVisible);
        colorpicker2.setTextfieldVisibility(txtfieldVisible);
        colorpicker3.setTextfieldVisibility(txtfieldVisible);
        colorpicker4.setTextfieldVisibility(txtfieldVisible);
        colorpicker5.setTextfieldVisibility(txtfieldVisible);
        colorpicker6.setTextfieldVisibility(txtfieldVisible);
    }

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setWidth("1000px");
        getLayout().setHeight(null);
        getLayout().addStyleName("colorpicker-mainwindow-content");

        // Create an instance of the preview and add it to the window
        display = new Embedded("Color preview");
        display.setWidth("270px");
        display.setHeight("270px");

        // Add the foreground and background colorpickers to a layout
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addStyleName("colorpicker-mainlayout");
        mainLayout.setWidth("100%");
        mainLayout.setHeight(null);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        getLayout().addComponent(mainLayout);

        VerticalLayout layoutLeft = new VerticalLayout();
        layoutLeft.setWidth("450px");
        layoutLeft.setHeight(null);
        layoutLeft.setSpacing(true);

        GridLayout optLayout = new GridLayout(3, 2);
        optLayout.setWidth("100%");
        optLayout.setHeight(null);
        optLayout.setMargin(true);
        optLayout.setSpacing(true);

        rgbBox.setValue(rgbVisible);
        rgbBox.addValueChangeListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                rgbVisible = (Boolean) event.getProperty().getValue();
                setPopupVisibilities();
            }
        });
        rgbBox.setImmediate(true);
        rgbBox.setId("rgbBox");
        optLayout.addComponent(rgbBox);

        hsvBox.setValue(hsvVisible);
        hsvBox.addValueChangeListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                hsvVisible = (Boolean) event.getProperty().getValue();
                setPopupVisibilities();
            }
        });
        hsvBox.setImmediate(true);
        hsvBox.setId("hsvBox");
        optLayout.addComponent(hsvBox);

        swaBox.setValue(swaVisible);
        swaBox.addValueChangeListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                swaVisible = (Boolean) event.getProperty().getValue();
                setPopupVisibilities();
            }
        });
        swaBox.setImmediate(true);
        swaBox.setId("swaBox");
        optLayout.addComponent(swaBox);

        hisBox.setValue(historyVisible);
        hisBox.addValueChangeListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                historyVisible = (Boolean) event.getProperty().getValue();
                setPopupVisibilities();
            }
        });
        hisBox.setImmediate(true);
        hisBox.setId("hisBox");
        optLayout.addComponent(hisBox);

        txtBox.setValue(txtfieldVisible);
        txtBox.addValueChangeListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                txtfieldVisible = (Boolean) event.getProperty().getValue();
                setPopupVisibilities();
            }
        });
        txtBox.setImmediate(true);
        txtBox.setId("txtBox");
        optLayout.addComponent(txtBox);

        Panel optPanel = new Panel("Customize the color picker popup window",
                optLayout);
        layoutLeft.addComponent(optPanel);

        HorizontalLayout layout1 = createHorizontalLayout();

        colorpicker1 = new ColorPicker("Foreground", foregroundColor);
        colorpicker1.setHtmlContentAllowed(true);
        colorpicker1.addColorChangeListener(this);
        colorpicker1.setId("colorpicker1");
        layout1.addComponent(colorpicker1);
        layout1.setComponentAlignment(colorpicker1, Alignment.MIDDLE_CENTER);

        colorpicker2 = new ColorPicker("Background", backgroundColor);
        colorpicker2.addColorChangeListener(this);
        colorpicker2.setId("colorpicker2");
        layout1.addComponent(colorpicker2);
        layout1.setComponentAlignment(colorpicker2, Alignment.MIDDLE_CENTER);

        Panel panel1 = new Panel(
                "Button-like colorpicker with current color and CSS code",
                layout1);
        layoutLeft.addComponent(panel1);

        HorizontalLayout layout2 = createHorizontalLayout();

        colorpicker3 = new ColorPicker("Foreground", foregroundColor);
        colorpicker3.addColorChangeListener(this);
        colorpicker3.setWidth("120px");
        colorpicker3.setCaption("Foreground");
        colorpicker3.setId("colorpicker3");
        layout2.addComponent(colorpicker3);
        layout2.setComponentAlignment(colorpicker3, Alignment.MIDDLE_CENTER);

        colorpicker4 = new ColorPicker("Background", backgroundColor);
        colorpicker4.addColorChangeListener(this);
        colorpicker4.setWidth("120px");
        colorpicker4.setCaption("Background");
        colorpicker4.setId("colorpicker4");
        layout2.addComponent(colorpicker4);
        layout2.setComponentAlignment(colorpicker4, Alignment.MIDDLE_CENTER);

        Panel panel2 = new Panel(
                "Button-like colorpicker with current color and custom caption",
                layout2);
        layoutLeft.addComponent(panel2);

        HorizontalLayout layout3 = createHorizontalLayout();

        colorpicker5 = new ColorPickerArea("Foreground", foregroundColor);
        colorpicker5.setCaption("Foreground");
        colorpicker5.addColorChangeListener(this);
        colorpicker5.setId("colorpicker5");
        layout3.addComponent(colorpicker5);
        layout3.setComponentAlignment(colorpicker5, Alignment.MIDDLE_CENTER);

        colorpicker6 = new ColorPickerArea("Background", backgroundColor);
        colorpicker6.setCaption("Background");
        colorpicker6.setDefaultCaptionEnabled(false);
        colorpicker6.addColorChangeListener(this);
        colorpicker6.setId("colorpicker6");
        layout3.addComponent(colorpicker6);
        layout3.setComponentAlignment(colorpicker6, Alignment.MIDDLE_CENTER);

        Panel panel3 = new Panel("Color area colorpicker with caption", layout3);
        panel3.setWidth("100%");
        panel3.setHeight(null);
        layoutLeft.addComponent(panel3);

        Label divider1 = new Label("<hr>", ContentMode.HTML);
        layoutLeft.addComponent(divider1);

        Label divider2 = new Label("<hr>", ContentMode.HTML);
        layoutLeft.addComponent(divider2);

        HorizontalLayout layout4 = createHorizontalLayout();

        addShadeButton(new Color(Integer.parseInt("000000", 16)), layout4);
        addShadeButton(new Color(Integer.parseInt("333333", 16)), layout4);
        addShadeButton(new Color(Integer.parseInt("666666", 16)), layout4);
        addShadeButton(new Color(Integer.parseInt("999999", 16)), layout4);
        addShadeButton(new Color(Integer.parseInt("cccccc", 16)), layout4);
        addShadeButton(new Color(Integer.parseInt("ffffff", 16)), layout4);

        Panel panel4 = new Panel(
                "Button-like colorpickers with disabled caption (no effect on fg/bg colors)",
                layout4);
        layoutLeft.addComponent(panel4);

        HorizontalLayout layout5 = createHorizontalLayout();

        addShadeArea(new Color(Integer.parseInt("000000", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("111111", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("222222", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("333333", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("444444", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("555555", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("666666", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("777777", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("888888", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("999999", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("aaaaaa", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("bbbbbb", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("cccccc", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("dddddd", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("eeeeee", 16)), layout5);
        addShadeArea(new Color(Integer.parseInt("ffffff", 16)), layout5);

        Panel panel5 = new Panel(
                "Area colorpickers with no given caption (no effect on fg/bg colors)",
                layout5);
        layoutLeft.addComponent(panel5);

        mainLayout.addComponent(layoutLeft);

        mainLayout.addComponent(display);

        updateDisplay(foregroundColor, backgroundColor);
    }

    private HorizontalLayout createHorizontalLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setHeight(null);
        layout.setMargin(true);
        return layout;
    }

    private int shadeButtonCounter = 1;

    private void addShadeButton(Color color, HorizontalLayout layout) {
        AbstractColorPicker colorPicker = new ColorPicker(color.toString(),
                color);
        colorPicker.setDefaultCaptionEnabled(false);
        colorPicker.setWidth("41px");
        colorPicker.setId("shadebutton_" + shadeButtonCounter);
        layout.addComponent(colorPicker);
        layout.setComponentAlignment(colorPicker, Alignment.MIDDLE_CENTER);

        ++shadeButtonCounter;
    }

    private int shadeAreaCounter = 1;

    private void addShadeArea(Color color, HorizontalLayout layout) {
        AbstractColorPicker colorPicker = new ColorPickerArea(color.toString(),
                color);
        colorPicker.setWidth("20px");
        colorPicker.setHeight("20px");
        colorPicker.setId("shadearea_" + shadeAreaCounter);
        layout.addComponent(colorPicker);
        layout.setComponentAlignment(colorPicker, Alignment.MIDDLE_CENTER);

        ++shadeAreaCounter;
    }

    // This is called whenever a colorpicker popup is closed
    /**
     * Update display.
     * 
     * @param fg
     *            the fg
     * @param bg
     *            the bg
     */
    public void updateDisplay(Color fg, Color bg) {
        java.awt.Color awtFg = new java.awt.Color(fg.getRed(), fg.getGreen(),
                fg.getBlue());
        java.awt.Color awtBg = new java.awt.Color(bg.getRed(), bg.getGreen(),
                bg.getBlue());
        StreamResource.StreamSource imagesource = new MyImageSource(awtFg,
                awtBg);

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hhmmss");

        StreamResource imageresource = new StreamResource(imagesource,
                "myimage" + format.format(now) + ".png");
        imageresource.setCacheTime(0);

        display.setSource(imageresource);
    }

    @Override
    public void colorChanged(ColorChangeEvent event) {
        if (event.getSource() == colorpicker1
                || event.getSource() == colorpicker3
                || event.getSource() == colorpicker5) {
            foregroundColor = event.getColor();

            if (event.getSource() != colorpicker1) {
                colorpicker1.setColor(event.getColor());
            }
            if (event.getSource() != colorpicker3) {
                colorpicker3.setColor(event.getColor());
            }
            if (event.getSource() != colorpicker5) {
                colorpicker5.setColor(event.getColor());
            }

        } else if (event.getSource() == colorpicker2
                || event.getSource() == colorpicker4
                || event.getSource() == colorpicker6) {
            backgroundColor = event.getColor();

            if (event.getSource() != colorpicker2) {
                colorpicker2.setColor(event.getColor());
            }
            if (event.getSource() != colorpicker4) {
                colorpicker4.setColor(event.getColor());
            }
            if (event.getSource() != colorpicker6) {
                colorpicker6.setColor(event.getColor());
            }

        } else {
            return;
        }

        updateDisplay(foregroundColor, backgroundColor);
    }
}
