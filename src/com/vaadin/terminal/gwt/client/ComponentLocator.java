package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.IView;
import com.vaadin.terminal.gwt.client.ui.IWindow;
import com.vaadin.terminal.gwt.client.ui.SubPartAware;

/**
 * ComponentLocator provides methods for uniquely identifying DOM elements using
 * string expressions. This class is EXPERIMENTAL and subject to change.
 */
public class ComponentLocator {

    /**
     * Separator used in the string expression between a parent and a child
     * widget.
     */
    private static final String PARENTCHILD_SEPARATOR = "/";

    /**
     * Separator used in the string expression between a widget and the widget's
     * sub part. NOT CURRENTLY IN USE.
     */
    private static final String SUBPART_SEPARATOR = "#";

    private ApplicationConnection client;

    public ComponentLocator(ApplicationConnection client) {
        this.client = client;
    }

    /**
     * EXPERIMENTAL.
     * 
     * Generates a string expression (path) which uniquely identifies the target
     * element . The getElementByPath method can be used for the inverse
     * operation, i.e. locating an element based on the string expression.
     * 
     * @since 5.4
     * @param targetElement
     *            The element to generate a path for.
     * @return A string expression uniquely identifying the target element or
     *         null if a string expression could not be created.
     */
    public String getPathForElement(Element targetElement) {
        String pid = null;

        Element e = targetElement;

        while (true) {
            pid = client.getPid(e);
            if (pid != null) {
                break;
            }

            e = DOM.getParent(e);
            if (e == null) {
                break;
            }
        }

        if (e == null || pid == null) {

            // Still test for context menu option
            String subPartName = client.getContextMenu().getSubPartName(
                    targetElement);
            if (subPartName != null) {
                // IContextMenu, singleton attached directly to rootpanel
                return "/IContextMenu[0]" + SUBPART_SEPARATOR + subPartName;

            }
            return null;
        }

        Widget w = (Widget) client.getPaintable(pid);
        if (w == null) {
            return null;
        }
        // ApplicationConnection.getConsole().log(
        // "First parent widget: " + Util.getSimpleName(w));

        String path = getPathForWidget(w);
        // ApplicationConnection.getConsole().log(
        // "getPathFromWidget returned " + path);
        if (w.getElement() == targetElement) {
            // ApplicationConnection.getConsole().log(
            // "Path for " + Util.getSimpleName(w) + ": " + path);

            return path;
        } else if (w instanceof SubPartAware) {
            return path + SUBPART_SEPARATOR
                    + ((SubPartAware) w).getSubPartName(targetElement);
        } else {
            path = path + getDOMPathForElement(targetElement, w.getElement());
            // ApplicationConnection.getConsole().log(
            // "Path with dom addition for " + Util.getSimpleName(w)
            // + ": " + path);

            return path;
        }
    }

    private Element getElementByDOMPath(Element baseElement, String path) {
        String parts[] = path.split(PARENTCHILD_SEPARATOR);
        Element element = baseElement;

        for (String part : parts) {
            if (part.startsWith("domChild[")) {
                String childIndexString = part.substring("domChild[".length(),
                        part.length() - 1);
                try {
                    int childIndex = Integer.parseInt(childIndexString);
                    element = DOM.getChild(element, childIndex);
                } catch (Exception e) {
                    // ApplicationConnection.getConsole().error(
                    // "Failed to parse integer in " + childIndexString);
                    return null;
                }
            }
        }

        return element;
    }

    private String getDOMPathForElement(Element element, Element baseElement) {
        Element e = element;
        String path = "";
        while (true) {
            Element parent = DOM.getParent(e);
            if (parent == null) {
                return "ERROR, baseElement is not a parent to element";
            }

            int childIndex = -1;

            int childCount = DOM.getChildCount(parent);
            for (int i = 0; i < childCount; i++) {
                if (e == DOM.getChild(parent, i)) {
                    childIndex = i;
                    break;
                }
            }
            if (childIndex == -1) {
                return "ERROR, baseElement is not a parent to element.";
            }

            path = PARENTCHILD_SEPARATOR + "domChild[" + childIndex + "]"
                    + path;

            if (parent == baseElement) {
                break;
            }

            e = parent;
        }

        return path;
    }

    /**
     * EXPERIMENTAL.
     * 
     * Locates an element by using a string expression (path) which uniquely
     * identifies the element. The getPathForElement method can be used for the
     * inverse operation, i.e. generating a string expression for a target
     * element.
     * 
     * @since 5.4
     * @param path
     *            The string expression which uniquely identifies the target
     *            element.
     * @return The DOM element identified by the path or null if the element
     *         could not be located.
     */
    public Element getElementByPath(String path) {
        // ApplicationConnection.getConsole()
        // .log("getElementByPath(" + path + ")");

        // Path is of type "PID/componentPart"
        String parts[] = path.split(SUBPART_SEPARATOR, 2);
        String widgetPath = parts[0];
        Widget w = getWidgetFromPath(widgetPath);
        if (w == null) {
            return null;
        }

        if (parts.length == 1) {
            int pos = widgetPath.indexOf("domChild");
            if (pos == -1) {
                return w.getElement();
            }

            // Contains dom reference to a sub element of the widget
            String subPath = widgetPath.substring(pos);
            return getElementByDOMPath(w.getElement(), subPath);
        } else if (parts.length == 2) {
            if (w instanceof SubPartAware) {
                // ApplicationConnection.getConsole().log(
                // "subPartAware: " + parts[1]);
                return ((SubPartAware) w).getSubPartElement(parts[1]);
            } else {
                // ApplicationConnection.getConsole().error(
                // "getElementByPath failed because "
                // + Util.getSimpleName(w)
                // + " is not SubPartAware");
                return null;
            }
        }

        return null;
    }

    private String getPathForWidget(Widget w) {
        if (w == null) {
            return "";
        }

        String pid = client.getPid(w.getElement());
        if (isStaticPid(pid)) {
            return pid;
        }

        if (w instanceof IView) {
            return "";
        } else if (w instanceof IWindow) {
            IWindow win = (IWindow) w;
            ArrayList<IWindow> subWindowList = client.getView()
                    .getSubWindowList();
            int indexOfSubWindow = subWindowList.indexOf(win);
            return PARENTCHILD_SEPARATOR + "IWindow[" + indexOfSubWindow + "]";
        }

        Widget parent = w.getParent();

        String basePath = getPathForWidget(parent);

        String simpleName = Util.getSimpleName(w);

        Iterator<Widget> i = ((HasWidgets) parent).iterator();
        int pos = 0;
        while (i.hasNext()) {
            Object child = i.next();
            if (child == w) {
                return basePath + PARENTCHILD_SEPARATOR + simpleName + "["
                        + pos + "]";
            }
            String simpleName2 = Util.getSimpleName(child);
            if (simpleName.equals(simpleName2)) {
                pos++;
            }
        }

        return "NOTFOUND";
    }

    private Widget getWidgetFromPath(String path) {
        Widget w = null;
        String parts[] = path.split(PARENTCHILD_SEPARATOR);

        // ApplicationConnection.getConsole().log(
        // "getWidgetFromPath(" + path + ")");

        for (String part : parts) {
            // ApplicationConnection.getConsole().log("Part: " + part);
            // ApplicationConnection.getConsole().log(
            // "Widget: " + Util.getSimpleName(w));
            if (part.equals("")) {
                w = client.getView();
            } else if (w == null) {
                w = (Widget) client.getPaintable(part);
            } else if (part.startsWith("domChild[")) {
                break;
            } else if (w instanceof HasWidgets) {
                HasWidgets parent = (HasWidgets) w;

                String[] split = part.split("\\[");

                Iterator<? extends Widget> i;
                String widgetClassName = split[0];
                if (widgetClassName.equals("IWindow")) {
                    i = client.getView().getSubWindowList().iterator();
                } else if (widgetClassName.equals("IContextMenu")) {
                    return client.getContextMenu();
                } else {
                    i = parent.iterator();
                }

                boolean ok = false;
                int pos = Integer.parseInt(split[1].substring(0, split[1]
                        .length() - 1));
                // ApplicationConnection.getConsole().log(
                // "Looking for child " + pos);
                while (i.hasNext()) {
                    // ApplicationConnection.getConsole().log("- child found");

                    Widget child = i.next();
                    String simpleName2 = Util.getSimpleName(child);

                    if (widgetClassName.equals(simpleName2)) {
                        if (pos == 0) {
                            w = child;
                            ok = true;
                            break;
                        }
                        pos--;
                    }
                }

                if (!ok) {
                    // Did not find the child
                    // ApplicationConnection.getConsole().error(
                    // "getWidgetFromPath(" + path + ") - did not find '"
                    // + part + "' for "
                    // + Util.getSimpleName(parent));

                    return null;
                }
            } else {
                // ApplicationConnection.getConsole().error(
                // "getWidgetFromPath(" + path + ") - failed for '" + part
                // + "'");
                return null;
            }
        }

        return w;
    }

    private boolean isStaticPid(String pid) {
        if (pid == null) {
            return false;
        }

        return pid.startsWith("PID_S");
    }

}
