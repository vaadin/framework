package com.itmill.toolkit.terminal.gwt.server;

import java.util.Iterator;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class DebugUtilities {

    /**
     * Recursively checks given component and its subtree for invalid layout
     * setups. Prints errors to std err stream.
     * 
     * @param component
     *            component to check
     */
    static void validateComponentRelativeSizes(Component component) {

        String msg = null;

        Component parent = component.getParent();
        if (!(component instanceof Window)) {
            if (hasRelativeWidth(component) && hasUndefinedWidth(parent)) {
                if (parent instanceof OrderedLayout) {
                    OrderedLayout ol = (OrderedLayout) parent;
                    if (ol.getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL) {
                        msg = "Relative width for component inside non sized horizontal ordered layout.";
                    } else if (!hasNonRelativeWidthComponent(ol)) {
                        msg = "At least one of vertical orderedlayout's components must have non relative width if layout has no width defined";
                    } else {
                        // valid situation, other components defined width
                    }
                } else if (!(parent instanceof GridLayout)
                        && !(parent instanceof CustomLayout)) {
                    // TODO make grid layout check (each col should have at
                    // least one non relatively sized component)

                    // default error for non sized parent issue
                    msg = "Relative width component's parent should not have undefined width.";
                }
            }
            // if no error found yet, check for height
            if (msg == null) {
                if (hasRelativeHeight(component) && hasUndefinedHeight(parent)) {
                    if (parent instanceof OrderedLayout) {
                        OrderedLayout ol = (OrderedLayout) parent;
                        if (ol.getOrientation() == OrderedLayout.ORIENTATION_VERTICAL) {
                            msg = "Relative height for component inside non sized vertical ordered layout.";
                        } else if (!hasNonRelativeHeightComponent(ol)) {
                            msg = "At least one of horizontal orderedlayout's components must have non relative height if layout has no height defined";
                        } else {
                            // valid situation, other components defined height
                        }
                    } else if (!(parent instanceof GridLayout)) {
                        // TODO make grid layout check (each row should have at
                        // least one non relatively sized component)

                        // default error for non sized parent issue
                        msg = "Relative height component's parent should not have undefined height.";
                    }
                }
            }
        }
        if (msg != null) {
            StringBuffer err = new StringBuffer();
            err
                    .append("IT MILL Toolkit DEBUG: Invalid layout detected. Components may be invisible or not render as expected.\n");
            err.append("\t Component : ");
            err.append(component);
            err.append(", Caption: ");
            err.append(component.getCaption());
            err.append(" DebugId : ");
            err.append(component.getDebugId());
            err.append("\n\t Parent    : ");
            err.append(parent);
            err.append(", Caption: ");
            err.append(parent.getCaption());
            err.append(" DebugId : ");
            err.append(parent.getDebugId());
            err.append("\n\t Error     : ");
            err.append(msg);
            System.err.println(err);
            return;
        }

        if (component instanceof Panel) {
            Panel panel = (Panel) component;
            validateComponentRelativeSizes(panel.getLayout());
        } else if (component instanceof ComponentContainer) {
            ComponentContainer lo = (ComponentContainer) component;
            Iterator it = lo.getComponentIterator();
            while (it.hasNext()) {
                validateComponentRelativeSizes((Component) it.next());
            }
        }

    }

    private static boolean hasNonRelativeHeightComponent(OrderedLayout ol) {
        Iterator it = ol.getComponentIterator();
        while (it.hasNext()) {
            if (!hasRelativeHeight((Component) it.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasUndefinedHeight(Component parent) {
        if (parent instanceof Window) {
            Window w = (Window) parent;
            if (w.getParent() == null) {
                // main window is considered to have size
                return false;
            }
        }
        return parent.getHeight() < 0;
    }

    private static boolean hasRelativeHeight(Component component) {
        return (component.getHeightUnits() == Sizeable.UNITS_PERCENTAGE && component
                .getHeight() > 0);
    }

    private static boolean hasNonRelativeWidthComponent(OrderedLayout ol) {
        Iterator it = ol.getComponentIterator();
        while (it.hasNext()) {
            if (!hasRelativeWidth((Component) it.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRelativeWidth(Component paintable) {
        return paintable.getWidth() > 0
                && paintable.getWidthUnits() == Sizeable.UNITS_PERCENTAGE;
    }

    private static boolean hasUndefinedWidth(Component parent) {
        if (parent instanceof Window) {
            Window w = (Window) parent;
            if (w.getParent() == null) {
                // main window is considered to have size
                return false;
            }

        }
        return parent.getWidth() < 0;
    }

}
