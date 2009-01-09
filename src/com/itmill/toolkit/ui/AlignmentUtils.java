package com.itmill.toolkit.ui;

import java.util.HashMap;
import java.util.Map;

import com.itmill.toolkit.ui.Layout.AlignmentHandler;

/**
 * Helper class for setting alignments using a short notation.
 * 
 * Supported notation is:
 * 
 * t,top for top alignment
 * 
 * m,middle for vertical center alignment
 * 
 * b,bottom for bottom alignment
 * 
 * l,left for left alignment
 * 
 * c,center for horizontal center alignment
 * 
 * r,right for right alignment
 * 
 */
public class AlignmentUtils {

    private static int horizontalMask = AlignmentHandler.ALIGNMENT_LEFT
            | AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER
            | AlignmentHandler.ALIGNMENT_RIGHT;
    private static int verticalMask = AlignmentHandler.ALIGNMENT_TOP
            | AlignmentHandler.ALIGNMENT_VERTICAL_CENTER
            | AlignmentHandler.ALIGNMENT_BOTTOM;

    private static Map<String, Integer> alignmentStrings = new HashMap();

    private static void addMapping(int alignment, String... values) {
        for (String s : values) {
            alignmentStrings.put(s, alignment);
        }
    }

    static {
        addMapping(AlignmentHandler.ALIGNMENT_TOP, "t", "top");
        addMapping(AlignmentHandler.ALIGNMENT_BOTTOM, "b", "bottom");
        addMapping(AlignmentHandler.ALIGNMENT_VERTICAL_CENTER, "m", "middle");

        addMapping(AlignmentHandler.ALIGNMENT_LEFT, "l", "left");
        addMapping(AlignmentHandler.ALIGNMENT_RIGHT, "r", "right");
        addMapping(AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER, "c", "center");
    }

    /**
     * Set the alignment for the component using short notation
     * 
     * @param parent
     * @param component
     * @param alignment
     *            String containing one or two alignment strings. If short
     *            notation "r","t",etc is used valid strings include
     *            "r","rt","tr","t". If the longer notation is used the
     *            alignments should be separated by a space e.g.
     *            "right","right top","top right","top". It is valid to mix
     *            short and long notation but they must be separated by a space
     *            e.g. "r top".
     * @throws IllegalArgumentException
     */
    public static void setComponentAlignment(AlignmentHandler parent,
            Component component, String alignment)
            throws IllegalArgumentException {
        if (alignment == null || alignment.length() == 0) {
            throw new IllegalArgumentException(
                    "alignment for setComponentAlignment() cannot be null or empty");
        }

        Integer currentAlignment = parent.getComponentAlignment(component)
                .getBitMask();

        if (alignment.length() == 1) {
            // Use short form "t","l",...
            currentAlignment = parseAlignment(alignment.substring(0, 1),
                    currentAlignment);
        } else if (alignment.length() == 2) {
            // Use short form "tr","lb",...
            currentAlignment = parseAlignment(alignment.substring(0, 1),
                    currentAlignment);
            currentAlignment = parseAlignment(alignment.substring(1, 2),
                    currentAlignment);
        } else {
            // Alignments are separated by space
            String[] strings = alignment.split(" ");
            if (strings.length > 2) {
                throw new IllegalArgumentException(
                        "alignment for setComponentAlignment() should not contain more than 2 alignments");
            }
            for (String alignmentString : strings) {
                currentAlignment = parseAlignment(alignmentString,
                        currentAlignment);
            }
        }

        int horizontalAlignment = currentAlignment & horizontalMask;
        int verticalAlignment = currentAlignment & verticalMask;
        parent.setComponentAlignment(component, new Alignment(
                horizontalAlignment + verticalAlignment));
    }

    /**
     * Parse alignmentString which contains one alignment (horizontal or
     * vertical) and return and updated version of the passed alignment where
     * the alignment in one direction has been changed. If the passed
     * alignmentString is unknown an exception is thrown
     * 
     * @param alignmentString
     * @param alignment
     * @return
     * @throws IllegalArgumentException
     */
    private static int parseAlignment(String alignmentString, int alignment)
            throws IllegalArgumentException {
        Integer parsed = alignmentStrings.get(alignmentString.toLowerCase());

        if (parsed == null) {
            throw new IllegalArgumentException(
                    "Could not parse alignment string '" + alignmentString
                            + "'");
        }

        if ((parsed & horizontalMask) != 0) {
            // Get the vertical alignment from the current alignment
            int vertical = (alignment & verticalMask);
            // Add the parsed horizontal alignment
            alignment = (vertical | parsed);
        } else {
            // Get the horizontal alignment from the current alignment
            int horizontal = (alignment & horizontalMask);
            // Add the parsed vertical alignment
            alignment = (horizontal | parsed);
        }

        return alignment;
    }
}
