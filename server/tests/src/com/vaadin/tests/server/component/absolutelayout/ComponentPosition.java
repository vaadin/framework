package com.vaadin.tests.server.component.absolutelayout;

import junit.framework.TestCase;

import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;

public class ComponentPosition extends TestCase {

    private static final String CSS = "top:7.0px;right:7.0%;bottom:7.0pc;left:7.0em;z-index:7;";
    private static final String PARTIAL_CSS = "top:7.0px;left:7.0em;";
    private static final Float CSS_VALUE = Float.valueOf(7);

    private static final Unit UNIT_UNSET = Sizeable.Unit.PIXELS;

    /**
     * Add component w/o giving positions, assert that everything is unset
     */
    public void testNoPosition() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b);

        assertNull(layout.getPosition(b).getTopValue());
        assertNull(layout.getPosition(b).getBottomValue());
        assertNull(layout.getPosition(b).getLeftValue());
        assertNull(layout.getPosition(b).getRightValue());

        assertEquals(UNIT_UNSET, layout.getPosition(b).getTopUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getBottomUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getLeftUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getRightUnits());

        assertEquals(-1, layout.getPosition(b).getZIndex());

        assertEquals("", layout.getPosition(b).getCSSString());

    }

    /**
     * Add component, setting all attributes using CSS, assert getter agree
     */
    public void testFullCss() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, CSS);

        assertEquals(CSS_VALUE, layout.getPosition(b).getTopValue());
        assertEquals(CSS_VALUE, layout.getPosition(b).getBottomValue());
        assertEquals(CSS_VALUE, layout.getPosition(b).getLeftValue());
        assertEquals(CSS_VALUE, layout.getPosition(b).getRightValue());

        assertEquals(Sizeable.Unit.PIXELS, layout.getPosition(b).getTopUnits());
        assertEquals(Sizeable.Unit.PICAS, layout.getPosition(b)
                .getBottomUnits());
        assertEquals(Sizeable.Unit.EM, layout.getPosition(b).getLeftUnits());
        assertEquals(Sizeable.Unit.PERCENTAGE, layout.getPosition(b)
                .getRightUnits());

        assertEquals(7, layout.getPosition(b).getZIndex());

        assertEquals(CSS, layout.getPosition(b).getCSSString());

    }

    /**
     * Add component, setting some attributes using CSS, assert getters agree
     */
    public void testPartialCss() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, PARTIAL_CSS);

        assertEquals(CSS_VALUE, layout.getPosition(b).getTopValue());
        assertNull(layout.getPosition(b).getBottomValue());
        assertEquals(CSS_VALUE, layout.getPosition(b).getLeftValue());
        assertNull(layout.getPosition(b).getRightValue());

        assertEquals(Sizeable.Unit.PIXELS, layout.getPosition(b).getTopUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getBottomUnits());
        assertEquals(Sizeable.Unit.EM, layout.getPosition(b).getLeftUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getRightUnits());

        assertEquals(-1, layout.getPosition(b).getZIndex());

        assertEquals(PARTIAL_CSS, layout.getPosition(b).getCSSString());

    }

    /**
     * Add component setting all attributes using CSS, then reset using partial
     * CSS; assert getters agree and the appropriate attributes are unset.
     */
    public void testPartialCssReset() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, CSS);

        layout.getPosition(b).setCSSString(PARTIAL_CSS);

        assertEquals(CSS_VALUE, layout.getPosition(b).getTopValue());
        assertNull(layout.getPosition(b).getBottomValue());
        assertEquals(CSS_VALUE, layout.getPosition(b).getLeftValue());
        assertNull(layout.getPosition(b).getRightValue());

        assertEquals(Sizeable.Unit.PIXELS, layout.getPosition(b).getTopUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getBottomUnits());
        assertEquals(Sizeable.Unit.EM, layout.getPosition(b).getLeftUnits());
        assertEquals(UNIT_UNSET, layout.getPosition(b).getRightUnits());

        assertEquals(-1, layout.getPosition(b).getZIndex());

        assertEquals(PARTIAL_CSS, layout.getPosition(b).getCSSString());

    }

    /**
     * Add component, then set all position attributes with individual setters
     * for value and units; assert getters agree.
     */
    public void testSetPosition() {
        final Float SIZE = Float.valueOf(12);

        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b);

        layout.getPosition(b).setTopValue(SIZE);
        layout.getPosition(b).setRightValue(SIZE);
        layout.getPosition(b).setBottomValue(SIZE);
        layout.getPosition(b).setLeftValue(SIZE);

        layout.getPosition(b).setTopUnits(Sizeable.Unit.CM);
        layout.getPosition(b).setRightUnits(Sizeable.Unit.EX);
        layout.getPosition(b).setBottomUnits(Sizeable.Unit.INCH);
        layout.getPosition(b).setLeftUnits(Sizeable.Unit.MM);

        assertEquals(SIZE, layout.getPosition(b).getTopValue());
        assertEquals(SIZE, layout.getPosition(b).getRightValue());
        assertEquals(SIZE, layout.getPosition(b).getBottomValue());
        assertEquals(SIZE, layout.getPosition(b).getLeftValue());

        assertEquals(Sizeable.Unit.CM, layout.getPosition(b).getTopUnits());
        assertEquals(Sizeable.Unit.EX, layout.getPosition(b).getRightUnits());
        assertEquals(Sizeable.Unit.INCH, layout.getPosition(b).getBottomUnits());
        assertEquals(Sizeable.Unit.MM, layout.getPosition(b).getLeftUnits());

    }

    /**
     * Add component, then set all position attributes with combined setters for
     * value and units; assert getters agree.
     */
    public void testSetPosition2() {
        final Float SIZE = Float.valueOf(12);
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b);

        layout.getPosition(b).setTop(SIZE, Sizeable.Unit.CM);
        layout.getPosition(b).setRight(SIZE, Sizeable.Unit.EX);
        layout.getPosition(b).setBottom(SIZE, Sizeable.Unit.INCH);
        layout.getPosition(b).setLeft(SIZE, Sizeable.Unit.MM);

        assertEquals(SIZE, layout.getPosition(b).getTopValue());
        assertEquals(SIZE, layout.getPosition(b).getRightValue());
        assertEquals(SIZE, layout.getPosition(b).getBottomValue());
        assertEquals(SIZE, layout.getPosition(b).getLeftValue());

        assertEquals(Sizeable.Unit.CM, layout.getPosition(b).getTopUnits());
        assertEquals(Sizeable.Unit.EX, layout.getPosition(b).getRightUnits());
        assertEquals(Sizeable.Unit.INCH, layout.getPosition(b).getBottomUnits());
        assertEquals(Sizeable.Unit.MM, layout.getPosition(b).getLeftUnits());

    }

    /**
     * Add component, set all attributes using CSS, unset some using method
     * calls, assert getters agree.
     */
    public void testUnsetPosition() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, CSS);

        layout.getPosition(b).setTopValue(null);
        layout.getPosition(b).setRightValue(null);
        layout.getPosition(b).setBottomValue(null);
        layout.getPosition(b).setLeftValue(null);

        layout.getPosition(b).setZIndex(-1);

        assertNull(layout.getPosition(b).getTopValue());
        assertNull(layout.getPosition(b).getBottomValue());
        assertNull(layout.getPosition(b).getLeftValue());
        assertNull(layout.getPosition(b).getRightValue());

        assertEquals("", layout.getPosition(b).getCSSString());

    }

}
