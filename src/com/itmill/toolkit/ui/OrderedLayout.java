package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Ordered layout.
 * 
 * <code>OrderedLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition in specified orientation.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class OrderedLayout extends AbstractOrderedLayout {
    /* Predefined orientations */

    /**
     * Components are to be laid out vertically.
     */
    public static final int ORIENTATION_VERTICAL = 0;

    /**
     * Components are to be laid out horizontally.
     */
    public static final int ORIENTATION_HORIZONTAL = 1;

    /**
     * Orientation of the layout.
     */
    private int orientation;

    /**
     * Creates a new ordered layout. The order of the layout is
     * <code>ORIENTATION_VERTICAL</code>.
     */
    public OrderedLayout() {
        this(ORIENTATION_VERTICAL);
    }

    /**
     * Create a new ordered layout. The orientation of the layout is given as
     * parameters.
     * 
     * @param orientation
     *            the Orientation of the layout.
     */
    public OrderedLayout(int orientation) {
        this.orientation = orientation;
        if (orientation == ORIENTATION_VERTICAL) {
            setWidth(100, UNITS_PERCENTAGE);
        }
    }

    /**
     * Gets the orientation of the container.
     * 
     * @return the Value of property orientation.
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of this OrderedLayout. This method should only be
     * used before initial paint.
     * 
     * @param orientation
     *            the New value of property orientation.
     * @deprecated Use VerticalLayout/HorizontalLayout or define orientation in
     *             constructor instead
     */
    @Deprecated
    public void setOrientation(int orientation) {
        setOrientation(orientation, true);
    }

    /**
     * Internal method to change orientation of layout. This method should only
     * be used before initial paint.
     * 
     * @param orientation
     */
    protected void setOrientation(int orientation, boolean needsRepaint) {
        // Checks the validity of the argument
        if (orientation < ORIENTATION_VERTICAL
                || orientation > ORIENTATION_HORIZONTAL) {
            throw new IllegalArgumentException();
        }

        this.orientation = orientation;
        if (needsRepaint) {
            requestRepaint();
        }
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Adds the orientation attributes (the default is vertical)
        if (orientation == ORIENTATION_HORIZONTAL) {
            target.addAttribute("orientation", "horizontal");
        }

    }

}
