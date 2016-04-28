package com.vaadin.ui;

import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;

public class AbsFieldDataSourceLocaleChangeTest {

    private VaadinSession vaadinSession;
    private UI ui;

    @Before
    public void setup() {
        vaadinSession = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(vaadinSession);
        ui = new UI() {

            @Override
            protected void init(VaadinRequest request) {

            }
        };
        ui.setSession(vaadinSession);
        UI.setCurrent(ui);
    }

    @Test
    public void localeChangesOnAttach() {
        TextField tf = new TextField();

        tf.setConverter(new StringToIntegerConverter() {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                if (locale == null) {
                    NumberFormat format = super.getFormat(locale);
                    format.setGroupingUsed(false);
                    format.setMinimumIntegerDigits(10);
                    return format;
                }
                return super.getFormat(locale);
            }
        });
        tf.setImmediate(true);
        tf.setConvertedValue(10000);
        Assert.assertEquals("0000010000", tf.getValue());

        VerticalLayout vl = new VerticalLayout();
        ui.setContent(vl);
        ui.setLocale(new Locale("en", "US"));

        vl.addComponent(tf);
        Assert.assertEquals("10,000", tf.getValue());
    }
}
