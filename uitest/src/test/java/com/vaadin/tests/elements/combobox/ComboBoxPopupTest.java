package com.vaadin.tests.elements.combobox;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxPopupTest extends MultiBrowserTest {

    private ComboBoxElement comboBoxElement;

    @Override
    protected Class<?> getUIClass() {
        return ComboBoxUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        comboBoxElement = $(ComboBoxElement.class).first();
    }

    @Test
    public void comboBoxPopup_popupOpen_popupFetchedSuccessfully() {
        comboBoxElement.openPopup();

        assertNotNull(comboBoxElement.getSuggestionPopup());
    }

    @Test
    public void comboBoxPopup_popupClosed_popupFetchedSuccessfully() {
        assertNotNull(comboBoxElement.getSuggestionPopup());
    }
}
