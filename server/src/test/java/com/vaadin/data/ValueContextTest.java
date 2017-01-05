package com.vaadin.data;

import java.util.Locale;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
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
        Assert.assertEquals("Unexpected locale from component",
                COMPONENT_LOCALE, locale);
    }

    @Test
    public void locale_from_ui() {
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        Assert.assertEquals("Unexpected locale from component", UI_LOCALE,
                locale);
    }

    @Test
    public void default_locale() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        Assert.assertEquals("Unexpected locale from component",
                Locale.getDefault(), locale);
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
