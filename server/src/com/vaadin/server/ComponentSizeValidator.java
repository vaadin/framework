/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.server;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.Area;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings({ "serial", "deprecation" })
public class ComponentSizeValidator implements Serializable {

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

        if (component != null) {
            boolean invalidHeight = !checkHeights(component);
            boolean invalidWidth = !checkWidths(component);

            if (invalidHeight || invalidWidth) {
                InvalidLayout error = new InvalidLayout(component,
                        invalidHeight, invalidWidth);
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
        }

        if (component instanceof Panel) {
            Panel panel = (Panel) component;
            errors = validateComponentRelativeSizes(panel.getContent(), errors,
                    parent);
        } else if (component instanceof ComponentContainer) {
            ComponentContainer lo = (ComponentContainer) component;
            Iterator<Component> it = lo.getComponentIterator();
            while (it.hasNext()) {
                errors = validateComponentRelativeSizes(it.next(), errors,
                        parent);
            }
        } else if (component instanceof Form) {
            Form form = (Form) component;
            if (form.getLayout() != null) {
                errors = validateComponentRelativeSizes(form.getLayout(),
                        errors, parent);
            }
            if (form.getFooter() != null) {
                errors = validateComponentRelativeSizes(form.getFooter(),
                        errors, parent);
            }
        }

        return errors;
    }

    private static void printServerError(String msg,
            Stack<ComponentInfo> attributes, boolean widthError,
            PrintStream errorStream) {
        StringBuffer err = new StringBuffer();
        err.append("Vaadin DEBUG\n");

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

        err.append("Layout problem detected: ");
        err.append(msg);
        err.append("\n");
        err.append("Relative sizes were replaced by undefined sizes, components may not render as expected.\n");
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
            if (component.getParent() == null) {
                return true;
            }

            return parentCanDefineHeight(component);
        } catch (Exception e) {
            getLogger().log(Level.FINER,
                    "An exception occurred while validating sizes.", e);
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
            if (component.getParent() == null) {
                return true;
            }

            return parentCanDefineWidth(component);
        } catch (Exception e) {
            getLogger().log(Level.FINER,
                    "An exception occurred while validating sizes.", e);
            return true;
        }
    }

    public static class InvalidLayout implements Serializable {

        private final Component component;

        private final boolean invalidHeight;
        private final boolean invalidWidth;

        private final Vector<InvalidLayout> subErrors = new Vector<InvalidLayout>();

        public InvalidLayout(Component component, boolean height, boolean width) {
            this.component = component;
            invalidHeight = height;
            invalidWidth = width;
        }

        public void addError(InvalidLayout error) {
            subErrors.add(error);
        }

        public void reportErrors(StringBuilder clientJSON,
                PrintStream serverErrorStream) {
            clientJSON.append("{");

            Component parent = component.getParent();
            String paintableId = component.getConnectorId();

            clientJSON.append("\"id\":\"" + paintableId + "\"");

            if (invalidHeight) {
                Stack<ComponentInfo> attributes = null;
                String msg = "";
                // set proper error messages
                if (parent instanceof AbstractOrderedLayout) {
                    AbstractOrderedLayout ol = (AbstractOrderedLayout) parent;
                    boolean vertical = false;

                    if (ol instanceof VerticalLayout) {
                        vertical = true;
                    }

                    if (vertical) {
                        msg = "Component with relative height inside a VerticalLayout with no height defined.";
                        attributes = getHeightAttributes(component);
                    } else {
                        msg = "At least one of a HorizontalLayout's components must have non relative height if the height of the layout is not defined";
                        attributes = getHeightAttributes(component);
                    }
                } else if (parent instanceof GridLayout) {
                    msg = "At least one of the GridLayout's components in each row should have non relative height if the height of the layout is not defined.";
                    attributes = getHeightAttributes(component);
                } else {
                    // default error for non sized parent issue
                    msg = "A component with relative height needs a parent with defined height.";
                    attributes = getHeightAttributes(component);
                }
                printServerError(msg, attributes, false, serverErrorStream);
                clientJSON.append(",\"heightMsg\":\"" + msg + "\"");
            }
            if (invalidWidth) {
                Stack<ComponentInfo> attributes = null;
                String msg = "";
                if (parent instanceof AbstractOrderedLayout) {
                    AbstractOrderedLayout ol = (AbstractOrderedLayout) parent;
                    boolean horizontal = true;

                    if (ol instanceof VerticalLayout) {
                        horizontal = false;
                    }

                    if (horizontal) {
                        msg = "Component with relative width inside a HorizontalLayout with no width defined";
                        attributes = getWidthAttributes(component);
                    } else {
                        msg = "At least one of a VerticalLayout's components must have non relative width if the width of the layout is not defined";
                        attributes = getWidthAttributes(component);
                    }
                } else if (parent instanceof GridLayout) {
                    msg = "At least one of the GridLayout's components in each column should have non relative width if the width of the layout is not defined.";
                    attributes = getWidthAttributes(component);
                } else {
                    // default error for non sized parent issue
                    msg = "A component with relative width needs a parent with defined width.";
                    attributes = getWidthAttributes(component);
                }
                clientJSON.append(",\"widthMsg\":\"" + msg + "\"");
                printServerError(msg, attributes, true, serverErrorStream);
            }
            if (subErrors.size() > 0) {
                serverErrorStream.println("Sub errors >>");
                clientJSON.append(", \"subErrors\" : [");
                boolean first = true;
                for (InvalidLayout subError : subErrors) {
                    if (!first) {
                        clientJSON.append(",");
                    } else {
                        first = false;
                    }
                    subError.reportErrors(clientJSON, serverErrorStream);
                }
                clientJSON.append("]");
                serverErrorStream.println("<< Sub erros");
            }
            clientJSON.append("}");
        }
    }

    private static class ComponentInfo implements Serializable {
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
                    + component.getWidthUnits().getSymbol();
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
                    + component.getHeightUnits().getSymbol();
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

        if (component.getId() != null) {
            err.append(" id: ");
            err.append(component.getId());
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
        Iterator<Component> it = ol.getComponentIterator();
        while (it.hasNext()) {
            if (!hasRelativeHeight(it.next())) {
                return true;
            }
        }
        return false;
    }

    public static boolean parentCanDefineHeight(Component component) {
        Component parent = component.getParent();
        if (parent == null) {
            // main window, valid situation
            return true;
        }
        if (parent.getHeight() < 0) {
            // Undefined height
            if (parent instanceof Window) {
                // Sub window with undefined size has a min-height
                return true;
            }

            if (parent instanceof AbstractOrderedLayout) {
                boolean horizontal = true;
                if (parent instanceof VerticalLayout) {
                    horizontal = false;
                }
                if (horizontal
                        && hasNonRelativeHeightComponent((AbstractOrderedLayout) parent)) {
                    return true;
                } else {
                    return false;
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
                    return false;
                } else {
                    // Other components define row height
                    return true;
                }
            }

            if (parent instanceof Panel || parent instanceof AbstractSplitPanel
                    || parent instanceof TabSheet
                    || parent instanceof CustomComponent) {
                // height undefined, we know how how component works and no
                // exceptions
                // TODO horiz SplitPanel ??
                return false;
            } else {
                // We cannot generally know if undefined component can serve
                // space for children (like CustomLayout or component built by
                // third party) so we assume they can
                return true;
            }

        } else if (hasRelativeHeight(parent)) {
            // Relative height
            if (parent.getParent() != null) {
                return parentCanDefineHeight(parent);
            } else {
                return true;
            }
        } else {
            // Absolute height
            return true;
        }
    }

    private static boolean hasRelativeHeight(Component component) {
        return (component.getHeightUnits() == Unit.PERCENTAGE && component
                .getHeight() > 0);
    }

    private static boolean hasNonRelativeWidthComponent(AbstractOrderedLayout ol) {
        Iterator<Component> it = ol.getComponentIterator();
        while (it.hasNext()) {
            if (!hasRelativeWidth(it.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRelativeWidth(Component paintable) {
        return paintable.getWidth() > 0
                && paintable.getWidthUnits() == Unit.PERCENTAGE;
    }

    public static boolean parentCanDefineWidth(Component component) {
        Component parent = component.getParent();
        if (parent == null) {
            // main window, valid situation
            return true;
        }
        if (parent instanceof Window) {
            // Sub window with undefined size has a min-width
            return true;
        }

        if (parent.getWidth() < 0) {
            // Undefined width

            if (parent instanceof AbstractOrderedLayout) {
                AbstractOrderedLayout ol = (AbstractOrderedLayout) parent;
                boolean horizontal = true;
                if (ol instanceof VerticalLayout) {
                    horizontal = false;
                }

                if (!horizontal && hasNonRelativeWidthComponent(ol)) {
                    // valid situation, other components defined width
                    return true;
                } else {
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
                    return false;
                } else {
                    // Other components define column width
                    return true;
                }
            } else if (parent instanceof Form) {
                /*
                 * If some other part of the form is not relative it determines
                 * the component width
                 */
                return hasNonRelativeWidthComponent((Form) parent);
            } else if (parent instanceof AbstractSplitPanel
                    || parent instanceof TabSheet
                    || parent instanceof CustomComponent) {
                // FIXME Could we use com.vaadin package name here and
                // fail for all component containers?
                // FIXME Actually this should be moved to containers so it can
                // be implemented for custom containers
                // TODO vertical splitpanel with another non relative component?
                return false;
            } else if (parent instanceof Window) {
                // Sub window can define width based on caption
                if (parent.getCaption() != null
                        && !parent.getCaption().equals("")) {
                    return true;
                } else {
                    return false;
                }
            } else if (parent instanceof Panel) {
                // TODO Panel should be able to define width based on caption
                return false;
            } else {
                return true;
            }
        } else if (hasRelativeWidth(parent)) {
            // Relative width
            if (parent.getParent() == null) {
                return true;
            }

            return parentCanDefineWidth(parent);
        } else {
            return true;
        }

    }

    private static boolean hasNonRelativeWidthComponent(Form form) {
        Layout layout = form.getLayout();
        Layout footer = form.getFooter();

        if (layout != null && !hasRelativeWidth(layout)) {
            return true;
        }
        if (footer != null && !hasRelativeWidth(footer)) {
            return true;
        }

        return false;
    }

    private static Map<Object, FileLocation> creationLocations = new HashMap<Object, FileLocation>();
    private static Map<Object, FileLocation> widthLocations = new HashMap<Object, FileLocation>();
    private static Map<Object, FileLocation> heightLocations = new HashMap<Object, FileLocation>();

    public static class FileLocation implements Serializable {
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
            Class<?> cls;
            try {
                String className = traceElement.getClassName();
                if (className.startsWith("java.")
                        || className.startsWith("sun.")) {
                    continue;
                }

                cls = Class.forName(className);
                if (cls == ComponentSizeValidator.class || cls == Thread.class) {
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
                getLogger().log(Level.FINER,
                        "An exception occurred while validating sizes.", e);
            }

        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(ComponentSizeValidator.class.getName());
    }

    /**
     * Validates the layout and returns a collection of errors
     * 
     * @since 7.1
     * @param ui
     *            The UI to validate
     * @return A collection of errors. An empty collection if there are no
     *         errors.
     */
    public static List<InvalidLayout> validateLayouts(UI ui) {
        List<InvalidLayout> invalidRelativeSizes = ComponentSizeValidator
                .validateComponentRelativeSizes(ui.getContent(),
                        new ArrayList<ComponentSizeValidator.InvalidLayout>(),
                        null);

        // Also check any existing subwindows
        if (ui.getWindows() != null) {
            for (Window subWindow : ui.getWindows()) {
                invalidRelativeSizes = ComponentSizeValidator
                        .validateComponentRelativeSizes(subWindow.getContent(),
                                invalidRelativeSizes, null);
            }
        }
        return invalidRelativeSizes;

    }

}
