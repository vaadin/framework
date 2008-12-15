package com.itmill.toolkit.terminal.gwt.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.AbstractOrderedLayout;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
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

        boolean valid = checkWidths(component) && checkHeights(component);

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

    private static void showError(String msg, Stack<ComponentInfo> attributes,
            boolean widthError) {
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
                showComponent(ci.component, ci.info, err, indent, widthError);
            }
        }

        err.append("Invalid layout detected. ");
        err.append(msg);
        err.append("\n");
        err
                .append("Components may be invisible or not render as expected. Relative sizes were replaced by undefined sizes.\n");
        System.err.println(err);

    }

    public static boolean checkHeights(Component component) {
        String msg = null;
        try {
            if (component instanceof Window) {
                return true;
            }

            Component parent = component.getParent();
            Stack<ComponentInfo> attributes = null;

            if (hasRelativeHeight(component) && parent != null
                    && hasUndefinedHeight(parent)) {
                if (parent instanceof AbstractOrderedLayout) {
                    AbstractOrderedLayout ol = (AbstractOrderedLayout) parent;
                    boolean vertical = false;

                    if (ol instanceof OrderedLayout) {
                        if (((OrderedLayout) ol).getOrientation() == OrderedLayout.ORIENTATION_VERTICAL) {
                            vertical = true;
                        }
                    } else if (ol instanceof VerticalLayout) {
                        vertical = true;
                    }

                    if (vertical) {
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
                } else if (!(parent instanceof CustomLayout)) {
                    // default error for non sized parent issue
                    msg = "Relative height component's parent should not have undefined height.";
                    attributes = getHeightAttributes(component);
                }
            }

            if (msg != null) {
                showError(msg, attributes, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (msg == null);
    }

    public static boolean checkWidths(Component component) {
        String msg = null;
        try {
            if (component instanceof Window) {
                return true;
            }

            Component parent = component.getParent();
            Stack<ComponentInfo> attributes = null;

            if (hasRelativeWidth(component) && parent != null
                    && hasUndefinedWidth(parent)) {
                if (parent instanceof AbstractOrderedLayout) {
                    AbstractOrderedLayout ol = (AbstractOrderedLayout) parent;
                    boolean horizontal = true;

                    if (ol instanceof OrderedLayout) {
                        if (((OrderedLayout) ol).getOrientation() == OrderedLayout.ORIENTATION_VERTICAL) {
                            horizontal = false;
                        }
                    } else if (ol instanceof VerticalLayout) {
                        horizontal = false;
                    }

                    if (horizontal) {
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
                    // CustomLayout's and Panels are omitted. Width can be
                    // defined
                    // by layout or by caption in Window
                    if (!(parent instanceof Panel
                            && parent.getCaption() != null && !parent
                            .getCaption().equals(""))) {
                        // default error for non sized parent issue
                        msg = "Relative width component's parent should not have undefined width.";
                        attributes = getWidthAttributes(component);

                    }
                }
            }

            if (msg != null) {
                showError(msg, attributes, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            StringBuffer err, StringBuilder indent, boolean widthError) {

        FileLocation createLoc = creationLocations.get(component);

        FileLocation sizeLoc;
        if (widthError) {
            sizeLoc = widthLocations.get(component);
        } else {
            sizeLoc = heightLocations.get(component);
        }

        err.append(indent);
        indent.append("  ");
        err.append("- ");

        err.append(component.getClass().getSimpleName());
        err.append("/").append(Integer.toHexString(component.hashCode()));

        if (component.getCaption() != null) {
            err.append(" \"");
            err.append(component.getCaption());
            err.append("\"");
        }

        if (component.getDebugId() != null) {
            err.append(" debugId: ");
            err.append(component.getDebugId());
        }

        if (createLoc != null) {
            err.append(", created at (" + createLoc.file + ":"
                    + createLoc.lineNumber + ")");

        }

        if (attribute != null) {
            err.append(" (");
            err.append(attribute);
            if (sizeLoc != null) {
                err.append(", set at (" + sizeLoc.file + ":"
                        + sizeLoc.lineNumber + ")");
            }

            err.append(")");
        }
        err.append("\n");

    }

    private static String getRelativeHeight(Component component) {
        return component.getHeight() + " %";
    }

    private static boolean hasNonRelativeHeightComponent(
            AbstractOrderedLayout ol) {
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

    private static boolean hasNonRelativeWidthComponent(AbstractOrderedLayout ol) {
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
        if (component instanceof Panel) {
            if (component.getCaption() != null
                    && !component.getCaption().equals("")) {
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

    private static Map<Object, FileLocation> creationLocations = new HashMap<Object, FileLocation>();
    private static Map<Object, FileLocation> widthLocations = new HashMap<Object, FileLocation>();
    private static Map<Object, FileLocation> heightLocations = new HashMap<Object, FileLocation>();

    public static class FileLocation {
        public String method;
        public String file;
        public String className;
        public String classNameSimple;
        public int lineNumber;

        public FileLocation(StackTraceElement traceElement) {
            file = traceElement.getFileName();
            className = traceElement.getClassName();
            classNameSimple = className
                    .substring(className.lastIndexOf('.') + 1);
            lineNumber = traceElement.getLineNumber();
            method = traceElement.getMethodName();
        }
    }

    public static void setCreationLocation(Object object) {
        setLocation(creationLocations, object);
    }

    public static void setWidthLocation(Object object) {
        setLocation(widthLocations, object);
    }

    public static void setHeightLocation(Object object) {
        setLocation(heightLocations, object);
    }

    private static void setLocation(Map<Object, FileLocation> map, Object object) {
        StackTraceElement[] traceLines = Thread.currentThread().getStackTrace();
        for (StackTraceElement traceElement : traceLines) {
            Class cls;
            try {
                String className = traceElement.getClassName();
                if (className.startsWith("java.")
                        || className.startsWith("sun.")) {
                    continue;
                }

                cls = Class.forName(className);
                if (cls == DebugUtilities.class || cls == Thread.class) {
                    continue;
                }

                if (Component.class.isAssignableFrom(cls)
                        && !CustomComponent.class.isAssignableFrom(cls)) {
                    continue;
                }
                FileLocation cl = new FileLocation(traceElement);
                map.put(object, cl);
                return;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}
