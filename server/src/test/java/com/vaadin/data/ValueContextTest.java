package com.vaadin.data;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ValueContextTest extends UI {

    private static final Locale UI_LOCALE = Locale.GERMAN;
    private static final Locale COMPONENT_LOCALE = Locale.FRENCH;
    private TextField textField;

    @Test
    public void locale_from_component() {
        textField.setLocale(COMPONENT_LOCALE);
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        assertEquals("Unexpected locale from component", COMPONENT_LOCALE,
                locale);
    }

    @Test
    public void locale_from_ui() {
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        assertEquals("Unexpected locale from component", UI_LOCALE, locale);
    }

    @Test
    public void default_locale() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        assertEquals("Unexpected locale from component", Locale.getDefault(),
                locale);
    }

    @Test
    public void testHasValue1() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(textField);
        assertEquals(textField, fromComponent.getHasValue().get());
    }

    @Test
    public void testHasValue2() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(new CheckBox(),
                textField);
        assertEquals(textField, fromComponent.getHasValue().get());
    }

    @Test
    public void testHasValue3() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(new CheckBox(), textField,
                Locale.CANADA);
        assertEquals(textField, fromComponent.getHasValue().get());
        assertEquals(Locale.CANADA, fromComponent.getLocale().get());
    }

    @Before
    public void setUp() {
        setLocale(UI_LOCALE);
        textField = new TextField();
        setContent(textField);
    }

    @Override
    public void init(VaadinRequest request) {
    }
}
