package com.itmill.toolkit.terminal.gwt.server;

import java.util.Iterator;
import java.util.Stack;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.GridLayout.Area;

public class DebugUtilities {

    private final static int LAYERS_SHOWN = 4;

    /**
     * Recursively checks given component and its subtree for invalid layout
     * setups. Prints errors to std err stream.
     * 
     * @param component
     *            component to check
     */
    public static boolean validateComponentRelativeSizes(Component component,
            boolean recursive) {

        boolean valid = true;

        if (!(component instanceof Window)) {
            valid = valid && checkWidths(component);
            valid = valid && checkHeights(component);
        }

        if (recursive) {
            if (component instanceof Panel) {
                Panel panel = (Panel) component;
                if (!validateComponentRelativeSizes(panel.getLayout(), false)) {
                    valid = false;
                }
            } else if (component instanceof ComponentContainer) {
                ComponentContainer lo = (ComponentContainer) component;
                Iterator it = lo.getComponentIterator();
                while (it.hasNext()) {
                    if (!validateComponentRelativeSizes((Component) it.next(),
                            false)) {
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    private static void showError(String msg, Stack<ComponentInfo> attributes) {
        StringBuffer err = new StringBuffer();
        err.append("IT MILL Toolkit DEBUG\n");

        StringBuilder indent = new StringBuilder("");
        ComponentInfo ci;
        if (attributes != null) {
            while (attributes.size() > LAYERS_SHOWN) {
                attributes.pop();
            }
            while (!attributes.empty()) {
                ci = attributes.pop();
                showComponent(ci.component, ci.info, err, indent);
            }
        }

        err.append("Invalid layout detected. ");
        err.append(msg);
        err.append("\n");
        err
                .append("Components may be invisible or not render as expected. Relative sizes were replaced by undefined sizes.\n");
        System.err.println(err);

    }

    private static boolean checkHeights(Component component) {
        Component parent = component.getParent();
        String msg = null;
        Stack<ComponentInfo> attributes = null;

        if (hasRelativeHeight(component) && hasUndefinedHeight(parent)) {
            if (parent instanceof OrderedLayout) {
                OrderedLayout ol = (OrderedLayout) parent;
                if (ol.getOrientation() == OrderedLayout.ORIENTATION_VERTICAL) {
                    msg = "Relative height for component inside non sized vertical ordered layout.";
                    attributes = getHeightAttributes(component);
                } else if (!hasNonRelativeHeightComponent(ol)) {
                    msg = "At least one of horizontal orderedlayout's components must have non relative height if layout has no height defined";
                    attributes = getHeightAttributes(component);
                } else {
                    // valid situation, other components defined height
                }
            } else if (parent instanceof GridLayout) {

                GridLayout gl = (GridLayout) parent;
                Area componentArea = gl.getComponentArea(component);
                boolean rowHasHeight = false;
                for (int row = componentArea.getRow1(); !rowHasHeight
                        && row <= componentArea.getRow2(); row++) {
                    for (int column = 0; !rowHasHeight
                            && column < gl.getColumns(); column++) {
                        Component c = gl.getComponent(column, row);
                        if (c != null) {
                            rowHasHeight = !hasRelativeHeight(c);
                        }
                    }
                }
                if (!rowHasHeight) {
                    msg = "At least one component in each row should have non relative height in GridLayout with undefined height.";
                    attributes = getHeightAttributes(component);
                }
            } else {
                // default error for non sized parent issue
                msg = "Relative height component's parent should not have undefined height.";
                attributes = getHeightAttributes(component);
            }
        }

        if (msg != null) {
            showError(msg, attributes);
        }
        return (msg == null);

    }

    private static boolean checkWidths(Component component) {
        Component parent = component.getParent();
        String msg = null;
        Stack<ComponentInfo> attributes = null;

        if (hasRelativeWidth(component) && hasUndefinedWidth(parent)) {
            if (parent instanceof OrderedLayout) {
                OrderedLayout ol = (OrderedLayout) parent;
                if (ol.getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL) {
                    msg = "Relative width for component inside non sized horizontal ordered layout.";
                    attributes = getWidthAttributes(component);
                } else if (!hasNonRelativeWidthComponent(ol)) {
                    msg = "At least one of vertical orderedlayout's components must have non relative width if layout has no width defined";
                    attributes = getWidthAttributes(component);
                } else {
                    // valid situation, other components defined width
                }
            } else if (parent instanceof GridLayout) {
                GridLayout gl = (GridLayout) parent;
                Area componentArea = gl.getComponentArea(component);
                boolean columnHasWidth = false;
                for (int col = componentArea.getColumn1(); !columnHasWidth
                        && col <= componentArea.getColumn2(); col++) {
                    for (int row = 0; !columnHasWidth && row < gl.getRows(); row++) {
                        Component c = gl.getComponent(col, row);
                        if (c != null) {
                            columnHasWidth = !hasRelativeWidth(c);
                        }
                    }
                }
                if (!columnHasWidth) {
                    msg = "At least one component in each column should have non relative width in GridLayout with undefined width.";
                    attributes = getWidthAttributes(component);
                }

            } else if (!(parent instanceof CustomLayout)) {

                // default error for non sized parent issue
                msg = "Relative width component's parent should not have undefined width.";
                attributes = getWidthAttributes(component);
            }
        }

        if (msg != null) {
            showError(msg, attributes);
        }

        return (msg == null);
    }

    private static class ComponentInfo {
        Component component;
        String info;

        public ComponentInfo(Component component, String info) {
            this.component = component;
            this.info = info;
        }

    }

    private static Stack<ComponentInfo> getHeightAttributes(Component component) {
        Stack<ComponentInfo> attributes = new Stack<ComponentInfo>();
        attributes
                .add(new ComponentInfo(component, getHeightString(component)));
        Component parent = component.getParent();
        attributes.add(new ComponentInfo(parent, getHeightString(parent)));

        while ((parent = parent.getParent()) != null) {
            attributes.add(new ComponentInfo(parent, getHeightString(parent)));
        }

        return attributes;
    }

    private static Stack<ComponentInfo> getWidthAttributes(Component component) {
        Stack<ComponentInfo> attributes = new Stack<ComponentInfo>();
        attributes.add(new ComponentInfo(component, getWidthString(component)));
        Component parent = component.getParent();
        attributes.add(new ComponentInfo(parent, getWidthString(parent)));

        while ((parent = parent.getParent()) != null) {
            attributes.add(new ComponentInfo(parent, getWidthString(parent)));
        }

        return attributes;
    }

    private static String getWidthString(Component component) {
        String width = "width: ";
        if (hasRelativeWidth(component)) {
            width += "RELATIVE, " + component.getWidth() + " %";
        } else if (hasUndefinedWidth(component)) {
            width += "UNDEFINED";
        } else if (component instanceof Window && component.getParent() == null) {
            width += "MAIN WINDOW";
        } else {
            width += "ABSOLUTE, " + component.getWidth() + " "
                    + Sizeable.UNIT_SYMBOLS[component.getWidthUnits()];
        }

        return width;
    }

    private static String getHeightString(Component component) {
        String height = "height: ";
        if (hasRelativeHeight(component)) {
            height += "RELATIVE, " + component.getHeight() + " %";
        } else if (hasUndefinedHeight(component)) {
            height += "UNDEFINED";
        } else if (component instanceof Window && component.getParent() == null) {
            height += "MAIN WINDOW";
        } else {
            height += "ABSOLUTE, " + component.getHeight() + " "
                    + Sizeable.UNIT_SYMBOLS[component.getHeightUnits()];
        }

        return height;
    }

    private static void showComponent(Component component, String attribute,
            StringBuffer err, StringBuilder indent) {
        err.append(indent);
        indent.append("  ");
        err.append("- ");

        err.append(component.getClass().getSimpleName());
        err.append("/").append(Integer.toHexString(component.hashCode()));
        err.append(" (");

        if (component.getCaption() != null) {
            err.append("\"");
            err.append(component.getCaption());
            err.append("\"");
        }

        if (component.getDebugId() != null) {
            err.append(" debugId: ");
            err.append(component.getDebugId());
        }

        err.append(")");
        if (attribute != null) {
            err.append(" (");
            err.append(attribute);
            err.append(")");
        }
        err.append("\n");

    }

    private static String getRelativeHeight(Component component) {
        return component.getHeight() + " %";
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
        if (parent.getHeight() < 0) {
            return true;
        } else {
            if (hasRelativeHeight(parent) && parent.getParent() != null) {
                return hasUndefinedHeight(parent.getParent());
            } else {
                return false;
            }
        }
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

    private static boolean hasUndefinedWidth(Component component) {
        if (component instanceof Window) {
            Window w = (Window) component;
            if (w.getParent() == null) {
                // main window is considered to have size
                return false;
            }

        }
        if (component.getWidth() < 0) {
            return true;
        } else {
            if (hasRelativeWidth(component) && component.getParent() != null) {
                return hasUndefinedWidth(component.getParent());
            } else {
                return false;
            }
        }

    }

}
