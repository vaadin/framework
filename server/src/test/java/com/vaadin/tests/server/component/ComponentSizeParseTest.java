package com.vaadin.tests.server.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.LabelState;
import com.vaadin.ui.Label;

public class ComponentSizeParseTest {

    private final class LabelWithPublicState extends Label {
        @Override
        protected LabelState getState() {
            return super.getState();
        }
    }

    @Test
    public void testAllTheUnit() {
        testUnit("10.0px", 10, Unit.PIXELS);
        testUnit("10.0pt", 10, Unit.POINTS);
        testUnit("10.0pc", 10, Unit.PICAS);
        testUnit("10.0em", 10, Unit.EM);
        testUnit("10.0rem", 10, Unit.REM);
        testUnit("10.0mm", 10, Unit.MM);
        testUnit("10.0cm", 10, Unit.CM);
        testUnit("10.0in", 10, Unit.INCH);
        testUnit("10.0%", 10, Unit.PERCENTAGE);
    }

    private void testUnit(String string, int amout, Unit unit) {
        LabelWithPublicState label = new LabelWithPublicState();
        label.setHeight(string);

        assertEquals(amout, label.getHeight(), 0);
        assertEquals(unit, label.getHeightUnits());

        label = new LabelWithPublicState();

        label.setHeight(10, unit);
        label.beforeClientResponse(true);
        assertEquals(string, label.getState().height);
    }
}
