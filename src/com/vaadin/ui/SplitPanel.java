/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * SplitPanel.
 * 
 * <code>SplitPanel</code> is a component container, that can contain two
 * components (possibly containers) which are split by divider element.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 * @deprecated in 6.5. Use {@link HorizontalSplitPanel} or
 *             {@link VerticalSplitPanel} instead.
 */
@Deprecated
@ClientWidget(value = VSplitPanelHorizontal.class, loadStyle = LoadStyle.EAGER)
public class SplitPanel extends AbstractSplitPanel {

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
     * Creates a new split panel. The orientation of the panels is
     * <code>ORIENTATION_VERTICAL</code>.
     */
    public SplitPanel() {
        super();
        orientation = ORIENTATION_VERTICAL;
        setSizeFull();
    }

    /**
     * Create a new split panels. The orientation of the panel is given as
     * parameters.
     * 
     * @param orientation
     *            the Orientation of the layout.
     */
    public SplitPanel(int orientation) {
        this();
        setOrientation(orientation);
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (orientation == ORIENTATION_VERTICAL) {
            target.addAttribute("vertical", true);
        }

    }

    /**
     * Gets the orientation of the split panel.
     * 
     * @return the Value of property orientation.
     * 
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of the split panel.
     * 
     * @param orientation
     *            the New value of property orientation.
     */
    public void setOrientation(int orientation) {

        // Checks the validity of the argument
        if (orientation < ORIENTATION_VERTICAL
                || orientation > ORIENTATION_HORIZONTAL) {
            throw new IllegalArgumentException();
        }

        this.orientation = orientation;
        requestRepaint();
    }

}
