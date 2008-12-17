package com.itmill.toolkit.terminal.gwt.server;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.AbstractOrderedLayout;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
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
     * @return set of first level errors found
     */
    public static List<InvalidLayout> validateComponentRelativeSizes(
            Component component, List<InvalidLayout> errors,
            InvalidLayout parent) {

        boolean invalidHeight = !checkHeights(component);
        boolean invalidWidth = !checkWidths(component);

        if (invalidHeight || invalidWidth) {
            InvalidLayout error = new InvalidLayout(component, invalidHeight,
                    invalidWidth);
            if (parent != null) {
                parent.addError(error);
            } else {
                if (errors == null) {
                    errors = new LinkedList<InvalidLayout>();
                }
                errors.add(error);
            }
            parent = error;
        }

        if (component instanceof Panel) {
            Panel panel = (Panel) component;
            errors = validateComponentRelativeSizes(panel.getLayout(), errors,
                    parent);
        } else if (component instanceof ComponentContainer) {
            ComponentContainer lo = (ComponentContainer) component;
            Iterator it = lo.getComponentIterator();
            while (it.hasNext()) {
                errors = validateComponentRelativeSizes((Component) it.next(),
                        errors, parent);
            }
        }

        return errors;
    }

    private static void printServerError(String msg,
            Stack<ComponentInfo> attributes, boolean widthError,
            PrintStream errorStream) {
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
        errorStream.println(err);

    }

    public static boolean checkHeights(Component component) {
        try {
            if (!hasRelativeHeight(component)) {
                return true;
            }
            if (component instanceof Window) {
                return true;
            }
            return !(component.getParent() != null && parentCannotDefineHeight(component));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean checkWidths(Component component) {
        try {
            if (!hasRelativeWidth(component)) {
                return true;
            }
            if (component instanceof Window) {
                return true;
            }
            return !(component.getParent() != null && parentCannotDefineWidth(component));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static class InvalidLayout {

        private Component component;

        private boolean invalidHeight;
        private boolean invalidWidth;

        private Vector<InvalidLayout> subErrors = new Vector<InvalidLayout>();

        public InvalidLayout(Component component, boolean height, boolean width) {
            this.component = component;
            invalidHeight = height;
            invalidWidth = width;
        }

        public void addError(InvalidLayout error) {
            subErrors.add(error);
        }

        public void reportErrors(PrintWriter clientJSON,
                CommunicationManager communicationManager,
                PrintStream serverErrorStream) {
            clientJSON.write("{");

            Component parent = component.getParent();
            String paintableId = communicationManager.getPaintableId(component);

            clientJSON.print("id:\"" + paintableId + "\"");

            if (invalidHeight) {
                Stack<ComponentInfo> attributes = null;
                String msg = "";
                // set proper error messages
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
                    } else {
                        msg = "At least one of horizontal orderedlayout's components must have non relative height if layout has no height defined";
                        attributes = getHeightAttributes(component);
                    }
                } else if (parent instanceof GridLayout) {
                    msg = "At least one component in each row should have non relative height in GridLayout with undefined height.";
                    attributes = getHeightAttributes(component);
                } else {
                    // default error for non sized parent issue
                    msg = "Relative height component's parent should not have undefined height.";
                    attributes = getHeightAttributes(component);
                }
                printServerError(msg, attributes, false, serverErrorStream);
                clientJSON.print(",\"heightMsg\":\"" + msg + "\"");
            }
            if (invalidWidth) {
                Stack<ComponentInfo> attributes = null;
                String msg = "";
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
                    } else {
                        msg = "At least one of vertical orderedlayout's components must have non relative width if layout has no width defined";
                        attributes = getWidthAttributes(component);
                    }
                } else if (parent instanceof GridLayout) {
                    msg = "At least one component in each column should have non relative width in GridLayout with undefined width.";
                    attributes = getWidthAttributes(component);
                } else {
                    // default error for non sized parent issue
                    msg = "Relative width component's parent should not have undefined width.";
                    attributes = getWidthAttributes(component);
                }
                clientJSON.print(",\"widthMsg\":\"" + msg + "\"");
                printServerError(msg, attributes, true, serverErrorStream);
            }
            if (subErrors.size() > 0) {
                serverErrorStream.println("Sub erros >>");
                clientJSON.write(", \"subErrors\" : [");
                boolean first = true;
                for (InvalidLayout subError : subErrors) {
                    if (!first) {
                        clientJSON.print(",");
                    } else {
                        first = false;
                    }
                    subError.reportErrors(clientJSON, communicationManager,
                            serverErrorStream);
                }
                clientJSON.write("]");
                serverErrorStream.println("<< Sub erros");
            }
            clientJSON.write("}");
        }
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
        } else if (component instanceof Window && component.getParent() == null) {
            width += "MAIN WINDOW";
        } else if (component.getWidth() >= 0) {
            width += "ABSOLUTE, " + component.getWidth() + " "
                    + Sizeable.UNIT_SYMBOLS[component.getWidthUnits()];
        } else {
            width += "UNDEFINED";
        }

        return width;
    }

    private static String getHeightString(Component component) {
        String height = "height: ";
        if (hasRelativeHeight(component)) {
            height += "RELATIVE, " + component.getHeight() + " %";
        } else if (component instanceof Window && component.getParent() == null) {
            height += "MAIN WINDOW";
        } else if (component.getHeight() > 0) {
            height += "ABSOLUTE, " + component.getHeight() + " "
                    + Sizeable.UNIT_SYMBOLS[component.getHeightUnits()];
        } else {
            height += "UNDEFINED";
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

    public static boolean parentCannotDefineHeight(Component component) {
        Component parent = component.getParent();
        if (parent == null) {
            // main window, valid situation
            return false;
        }
        if (parent.getHeight() < 0) {
            if (parent instanceof Window) {
                Window w = (Window) parent;
                if (w.getParent() == null) {
                    // main window is considered to have size
                    return false;
                }
            }
            if (parent instanceof VerticalLayout) {
                return true;
            } else if (parent instanceof AbstractOrderedLayout) {
                boolean horizontal = true;
                if (parent instanceof OrderedLayout) {
                    horizontal = ((OrderedLayout) parent).getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL;
                }
                if (horizontal
                        && hasNonRelativeHeightComponent((AbstractOrderedLayout) parent)) {
                    return false;
                } else {
                    return true;
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
                    return true;
                }
            }

            if (parent instanceof Panel || parent instanceof SplitPanel
                    || parent instanceof TabSheet
                    || parent instanceof CustomComponent) {
                // height undefined, we know how how component works and no
                // exceptions
                // TODO horiz SplitPanel ??
                return true;
            } else {
                // we cannot generally know if undefined component can serve
                // space for children (like CustomLayout or component built by
                // third party)
                return false;
            }

        } else {
            if (hasRelativeHeight(parent) && parent.getParent() != null) {
                return parentCannotDefineHeight(parent);
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

    public static boolean parentCannotDefineWidth(Component component) {
        Component parent = component.getParent();
        if (parent == null) {
            // main window, valid situation
            return false;
        }
        if (parent instanceof Window) {
            Window w = (Window) parent;
            if (w.getParent() == null) {
                // main window is considered to have size
                return false;
            }

        }

        if (parent.getWidth() < 0) {
            if (parent instanceof Panel) {
                if (parent.getCaption() != null
                        && !parent.getCaption().equals("")) {
                    return false;
                } else {
                    return true;
                }
            } else if (parent instanceof AbstractOrderedLayout) {
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
                    return true;
                } else if (!hasNonRelativeWidthComponent(ol)) {
                    return true;
                } else {
                    // valid situation, other components defined width
                    return false;
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
                    return true;
                } else {
                    // valid situation
                    return false;
                }
            } else if (parent instanceof SplitPanel
                    || parent instanceof TabSheet
                    || parent instanceof CustomComponent) {
                // TODO vertical splitpanel with another non relative component?
                return true;
            } else {
                return false;
            }
        } else {
            if (hasRelativeWidth(parent) && parent.getParent() != null) {
                return parentCannotDefineWidth(parent);
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
