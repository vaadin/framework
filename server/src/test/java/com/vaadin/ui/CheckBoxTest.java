package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.tests.util.MockUI;

public class CheckBoxTest {
    @Test
    public void initiallyFalse() {
        CheckBox cb = new CheckBox();
        assertFalse(cb.getValue());
    }

    @Test
    public void testSetValue() {
        CheckBox cb = new CheckBox();
        cb.setValue(true);
        assertTrue(cb.getValue());
        cb.setValue(false);
        assertFalse(cb.getValue());
    }

    @Test
    public void setValueChangeFromClientIsUserOriginated() {
        UI ui = new MockUI();
        CheckBox cb = new CheckBox();
        ui.setContent(cb);
        AtomicBoolean userOriginated = new AtomicBoolean(false);
        cb.addValueChangeListener(
                event -> userOriginated.set(event.isUserOriginated()));
        ComponentTest.syncToClient(cb);
        ServerRpcManager.getRpcProxy(cb, CheckBoxServerRpc.class)
                .setChecked(true, new MouseEventDetails());
        assertTrue(userOriginated.get());
        userOriginated.set(false);
        ComponentTest.syncToClient(cb);
        ServerRpcManager.getRpcProxy(cb, CheckBoxServerRpc.class)
                .setChecked(false, new MouseEventDetails());
        assertTrue(userOriginated.get());
    }

    @Test
    public void setValueChangeFromServerIsNotUserOriginated() {
        UI ui = new MockUI();
        CheckBox cb = new CheckBox();
        ui.setContent(cb);
        AtomicBoolean userOriginated = new AtomicBoolean(true);
        cb.addValueChangeListener(
                event -> userOriginated.set(event.isUserOriginated()));
        cb.setValue(true);
        assertFalse(userOriginated.get());
        userOriginated.set(true);
        cb.setValue(false);
        assertFalse(userOriginated.get());
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        CheckBox cb = new CheckBox();
        cb.setValue(null);
    }

    @Test
    public void getComboBoxInput() {
        CheckBox cb = new CheckBox();
        assertNotNull("getInputElement should always return a element", cb.getInputElement());
        assertHasStyleNames(cb.getInputElement());
    }

    @Test
    public void getCheckBoxLabel() {
        CheckBox cb = new CheckBox();
        assertNotNull("getLabelElement should always return a element", cb.getLabelElement());
        assertHasStyleNames(cb.getLabelElement());
    }

    @Test
    @Ignore("Component#setStyleName(null, false) should not throw a NPE")
    public void setStyleName_null_false_throws_NPE() {
        // FIXME? - Currently it throws a NPE like the implementation in Component.java
        // waiting for other ticket that fixes the behaviour in Component.java before
        CheckBox cb = new CheckBox();
        cb.getLabelElement().addStyleName("first");
        cb.getLabelElement().setStyleName(null, false);
        assertEquals("Removing a null style should be ignored",
                "first", cb.getLabelElement().getStyleName());
    }

    private void assertHasStyleNames(HasStyleNames hasStyleNames) {
        assertEquals("Given element should not have a default style name",
                "", hasStyleNames.getStyleName());

        hasStyleNames.addStyleName("first");
        assertEquals("first", hasStyleNames.getStyleName());

        hasStyleNames.addStyleName("first");
        assertEquals("Adding two times the same style should be ignored",
                "first", hasStyleNames.getStyleName());

        hasStyleNames.addStyleName(null);
        assertEquals("Adding null as style should be ignored",
                "first", hasStyleNames.getStyleName());

        hasStyleNames.addStyleName("");
        assertEquals("Adding an empty string as style should be ignored",
                "first", hasStyleNames.getStyleName());

        hasStyleNames.addStyleName("second");
        assertEquals("first second", hasStyleNames.getStyleName());

        hasStyleNames.removeStyleName("second");
        assertEquals("first", hasStyleNames.getStyleName());

        hasStyleNames.addStyleName("second third fourth");
        assertEquals("first second third fourth", hasStyleNames.getStyleName());

        hasStyleNames.removeStyleName("third fourth");
        assertEquals("first second", hasStyleNames.getStyleName());

        hasStyleNames.addStyleNames("third", "fourth");
        assertEquals("first second third fourth", hasStyleNames.getStyleName());

        hasStyleNames.removeStyleNames("second",  "fourth");
        assertEquals("first third", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName(null);
        assertEquals("Setting null as style should reset them",
                "", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("set-style");
        assertEquals("set-style", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("");
        assertEquals("Setting an empty string as style should reset them",
                "", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("set-style multiple values");
        assertEquals("set-style multiple values", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("set-style", false);
        assertEquals("multiple values", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("", false);
        assertEquals("Removing an empty style should be ignored",
                "multiple values", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("", true);
        assertEquals("Adding an empty style should be ignored",
                "multiple values", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName(null, true);
        assertEquals("Adding a null style should be ignored",
                "multiple values", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("multiple values", false);
        assertEquals("Removing all set style names should result in an empty style name",
                "", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("set-style", true);
        assertEquals("set-style", hasStyleNames.getStyleName());

        hasStyleNames.setStyleName("multiple values", true);
        assertEquals("set-style multiple values", hasStyleNames.getStyleName());

    }

}
