package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.InlineDateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Slider;
import com.itmill.toolkit.ui.TextField;

/**
 * Shows some basic fields for value input; TextField, DateField, Slider...
 * 
 * @author IT Mill Ltd.
 */
public class ValueInputExample extends CustomComponent {

    // used to show the last entered value in the textfields
    Label textfieldValue;

    public ValueInputExample() {
        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        // TextField
        OrderedLayout horiz = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(horiz);
        Panel left = new Panel("TextField");
        left.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(left);
        Panel right = new Panel("Last input");
        right.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(right);
        // "last entered" -label
        textfieldValue = new Label();
        textfieldValue.setContentMode(Label.CONTENT_PREFORMATTED);
        right.addComponent(textfieldValue);
        // listener that will update the last entered value for the TextFields
        Field.ValueChangeListener listener = new Field.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                textfieldValue.setValue(event.getProperty().getValue());
            }
        };
        // basic TextField
        TextField tf = new TextField("Basic");
        tf.setColumns(15);
        tf.setImmediate(true);
        tf.addListener(listener);
        left.addComponent(tf);
        // multiline TextField a.k.a TextArea
        tf = new TextField("Area");
        tf.setColumns(15);
        tf.setRows(5);
        tf.setImmediate(true);
        tf.addListener(listener);
        left.addComponent(tf);

        // DateFields
        horiz = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(horiz);
        left = new Panel("DateField");
        left.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(left);
        right = new Panel("Inline ");
        right.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(right);
        // default
        DateField df = new DateField("Default (day) resolution");
        left.addComponent(df);
        // minute
        df = new DateField("Minute resolution");
        df.setResolution(DateField.RESOLUTION_MIN);
        left.addComponent(df);
        // year
        df = new DateField("Year resolution");
        df.setResolution(DateField.RESOLUTION_YEAR);
        left.addComponent(df);
        // msec
        df = new DateField("Millisecond resolution");
        df.setResolution(DateField.RESOLUTION_MSEC);
        left.addComponent(df);
        // Inline
        df = new InlineDateField();
        right.addComponent(df);

        // Slider
        left = new Panel("Slider");
        left.setStyleName(Panel.STYLE_LIGHT);
        main.addComponent(left);
        // int slider
        Slider slider = new Slider(0, 100);
        slider.setSize(300);
        slider.setImmediate(true);
        slider.addListener(new Slider.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // update caption when value changes
                Slider s = (Slider) event.getProperty();
                s.setCaption("Value: " + s.getValue());
            }
        });
        try {
            slider.setValue(20);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        left.addComponent(slider);
        // double slider
        slider = new Slider(0.0, 1.0, 1);
        slider.setImmediate(true);
        slider.addListener(new Slider.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // update caption when value changes
                Slider s = (Slider) event.getProperty();
                s.setCaption("Value: " + s.getValue());
            }
        });
        try {
            slider.setValue(0.5);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        left.addComponent(slider);

    }

}
