package com.vaadin.tests.components.grid;

import com.vaadin.ui.Grid;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GridAssistiveCaptionTest {

    private Grid<SimplePair> grid;

    @Before
    public void setup() {
        grid = new Grid<>(SimplePair.class);
        grid.getColumn("b").setAssistiveCaption("Press Enter to sort.");
    }

    @Test
    public void testGridAssistiveCaption() {
        assertEquals(null,
                grid.getColumn("a").getAssistiveCaption());
        assertEquals("Press Enter to sort.",
                grid.getColumn("b").getAssistiveCaption());
    }

    private class SimplePair {

        private String a;
        private String b;

        public SimplePair(){

        }

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public void setA(String a) {
            this.a = a;
        }

        public void setB(String b) {
            this.b = b;
        }
    }
}
