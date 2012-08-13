/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal;

import java.io.Serializable;

import com.vaadin.terminal.PaintTarget.PaintStatus;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

public class LegacyPaint implements Serializable {
    /**
     * 
     * <p>
     * Paints the Paintable into a UIDL stream. This method creates the UIDL
     * sequence describing it and outputs it to the given UIDL stream.
     * </p>
     * 
     * <p>
     * It is called when the contents of the component should be painted in
     * response to the component first being shown or having been altered so
     * that its visual representation is changed.
     * </p>
     * 
     * <p>
     * <b>Do not override this to paint your component.</b> Override
     * {@link #paintContent(PaintTarget)} instead.
     * </p>
     * 
     * 
     * @param target
     *            the target UIDL stream where the component should paint itself
     *            to.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public static void paint(Component component, PaintTarget target)
            throws PaintException {
        // Only paint content of visible components.
        if (!isVisibleInContext(component)) {
            return;
        }

        final String tag = target.getTag(component);
        final PaintStatus status = target.startPaintable(component, tag);
        if (PaintStatus.CACHED == status) {
            // nothing to do but flag as cached and close the paintable tag
            target.addAttribute("cached", true);
        } else {
            // Paint the contents of the component
            if (component instanceof Vaadin6Component) {
                ((Vaadin6Component) component).paintContent(target);
            }

        }
        target.endPaintable(component);

    }

    /**
     * Checks if the component is visible and its parent is visible,
     * recursively.
     * <p>
     * This is only a helper until paint is moved away from this class.
     * 
     * @return
     */
    protected static boolean isVisibleInContext(Component c) {
        HasComponents p = c.getParent();
        while (p != null) {
            if (!p.isVisible()) {
                return false;
            }
            p = p.getParent();
        }
        if (c.getParent() != null && !c.getParent().isComponentVisible(c)) {
            return false;
        }

        // All parents visible, return this state
        return c.isVisible();
    }

}
