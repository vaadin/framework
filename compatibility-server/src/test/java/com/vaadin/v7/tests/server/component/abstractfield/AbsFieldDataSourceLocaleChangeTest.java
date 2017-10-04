package com.vaadin.v7.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;

import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.TextField;

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
        assertEquals("0000010000", tf.getValue());

        VerticalLayout vl = new VerticalLayout();
        ui.setContent(vl);
        ui.setLocale(new Locale("en", "US"));

        vl.addComponent(tf);
        assertEquals("10,000", tf.getValue());
    }
}
