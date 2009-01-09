package com.itmill.toolkit.tests.tickets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo;
import com.itmill.toolkit.ui.AbstractOrderedLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;

public class Ticket2279 extends Application {

    private Label label;

    private static Map<String, Integer> expected = new HashMap<String, Integer>();

    private static Set<String> longHorizontalAlignments = new HashSet<String>();
    private static Set<String> shortHorizontalAlignments = new HashSet<String>();
    private static Set<String> longVerticalAlignments = new HashSet<String>();
    private static Set<String> shortVerticalAlignments = new HashSet<String>();

    static {
        expected.put("r", AlignmentInfo.Bits.ALIGNMENT_RIGHT);
        expected.put("l", AlignmentInfo.Bits.ALIGNMENT_LEFT);
        expected.put("c", AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER);
        expected.put("t", AlignmentInfo.Bits.ALIGNMENT_TOP);
        expected.put("b", AlignmentInfo.Bits.ALIGNMENT_BOTTOM);
        expected.put("m", AlignmentInfo.Bits.ALIGNMENT_VERTICAL_CENTER);

        expected.put("right", AlignmentInfo.Bits.ALIGNMENT_RIGHT);
        expected.put("left", AlignmentInfo.Bits.ALIGNMENT_LEFT);
        expected.put("center", AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER);
        expected.put("top", AlignmentInfo.Bits.ALIGNMENT_TOP);
        expected.put("bottom", AlignmentInfo.Bits.ALIGNMENT_BOTTOM);
        expected.put("middle", AlignmentInfo.Bits.ALIGNMENT_VERTICAL_CENTER);

        shortHorizontalAlignments.add("r");
        shortHorizontalAlignments.add("l");
        shortHorizontalAlignments.add("c");
        shortVerticalAlignments.add("t");
        shortVerticalAlignments.add("b");
        shortVerticalAlignments.add("m");

        longHorizontalAlignments.add("right");
        longHorizontalAlignments.add("left");
        longHorizontalAlignments.add("center");
        longVerticalAlignments.add("top");
        longVerticalAlignments.add("bottom");
        longVerticalAlignments.add("middle");

    }

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        AbstractOrderedLayout layout = (AbstractOrderedLayout) w.getLayout();

        createUI(layout);
    }

    private void createUI(Layout layout) {
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("500px");
        vl.setHeight("500px");
        vl.setStyleName("borders");
        label = new Label("<b>Error messages follows:</b><br/>",
                Label.CONTENT_XHTML);
        vl.addComponent(label);
        layout.addComponent(vl);

        testAlignments(vl);

        GridLayout gl = new GridLayout(1, 1);
        gl.setWidth("500px");
        gl.setHeight("500px");
        gl.setStyleName("borders");
        label = new Label("<b>Error messages follows:</b><br/>",
                Label.CONTENT_XHTML);
        gl.addComponent(label);
        layout.addComponent(gl);

        testAlignments(gl);

    }

    private void testAlignments(AlignmentHandler layout) {
        HashSet<String> horizontals = new HashSet<String>();
        horizontals.addAll(shortHorizontalAlignments);
        horizontals.addAll(longHorizontalAlignments);

        for (String horiz : horizontals) {
            // Test "l","r","left","right" etc
            int expectedHoriz = expected.get(horiz);
            checkAlignment(layout, horiz, AlignmentHandler.ALIGNMENT_TOP
                    | expectedHoriz);

            for (String vert : shortVerticalAlignments) {
                int expectedVert = expected.get(vert);

                // Test "lt","rt" etc
                if (horiz.length() == 1) {
                    checkAlignment(layout, horiz + vert, expectedHoriz
                            | expectedVert);
                    checkAlignment(layout, vert + horiz, expectedHoriz
                            | expectedVert);
                } else {
                    boolean ok = false;
                    try {
                        checkAlignment(layout, horiz + vert, expectedHoriz
                                | expectedVert);
                    } catch (IllegalArgumentException e) {
                        // OK, "centert","rightb" etc are not valid
                        ok = true;
                    }
                    if (!ok) {
                        error("IllegalArgumentException was not thrown for "
                                + horiz + vert);
                    }
                    ok = false;
                    try {
                        checkAlignment(layout, vert + horiz, expectedHoriz
                                | expectedVert);
                    } catch (IllegalArgumentException e) {
                        // OK, "centert","rightb" etc are not valid
                        ok = true;
                    }
                    if (!ok) {
                        error("IllegalArgumentException was not thrown for "
                                + horiz + vert);
                    }

                }

                // Test "l t","r t" etc
                checkAlignment(layout, horiz + " " + vert, expectedHoriz
                        | expectedVert);
                checkAlignment(layout, vert + " " + horiz, expectedHoriz
                        | expectedVert);
            }

            for (String vert : longVerticalAlignments) {
                int expectedVert = expected.get(vert);

                // Test "right t","right b" etc
                checkAlignment(layout, horiz + " " + vert, expectedHoriz
                        | expectedVert);
                checkAlignment(layout, vert + " " + horiz, expectedHoriz
                        | expectedVert);

                // Three alignments should throw an exception
                boolean ok = false;
                try {
                    checkAlignment(layout, horiz + " " + vert + " " + horiz,
                            expectedHoriz | expectedVert);
                } catch (IllegalArgumentException e) {
                    // OK, "centert","rightb" etc are not valid
                    ok = true;
                }
                if (!ok) {
                    error("IllegalArgumentException was not thrown for "
                            + horiz + " " + vert + " " + horiz);
                }
            }
        }

        checkAlignment(layout, "left right", AlignmentHandler.ALIGNMENT_TOP
                | AlignmentHandler.ALIGNMENT_RIGHT);
    }

    private void checkAlignment(AlignmentHandler layout,
            String alignmentString, int expected) {
        layout.setComponentAlignment(label, AlignmentInfo.Bits.ALIGNMENT_TOP,
                AlignmentInfo.Bits.ALIGNMENT_LEFT);
        if (layout instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) layout).setComponentAlignment(label,
                    alignmentString);
        } else {
            ((GridLayout) layout).setComponentAlignment(label, alignmentString);
        }

        int actual = layout.getComponentAlignment(label).getBitMask();
        if (actual != expected) {
            String error = "Error " + alignmentString
                    + " did not produce expected results";
            error(error);
        } else {
            String str = layout.getClass().getSimpleName() + "/"
                    + alignmentString + ": OK";
            System.out.println(str);
        }

    }

    private void error(String error) {
        label.setValue(label.getValue() + error + "<br/>");
        System.out.println(error);
    }
}
