/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * <p>
 * A container component with freely designed layout and style. The container
 * consists of items with textually represented locations. Each item contains
 * one sub-component. The adapter and theme are responsible for rendering the
 * layout with given style by placing the items on the screen in defined
 * locations.
 * </p>
 * 
 * <p>
 * The definition of locations is not fixed - the each style can define its
 * locations in a way that is suitable for it. One typical example would be to
 * create visual design for a web site as a custom layout: the visual design
 * could define locations for "menu", "body" and "title" for example. The layout
 * would then be implemented as XLS-template with for given style.
 * </p>
 * 
 * <p>
 * The default theme handles the styles that are not defined by just drawing the
 * subcomponents as in OrderedLayout.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class CustomLayout extends AbstractLayout {

    private static final int BUFFER_SIZE = 10000;

    /**
     * Custom layout slots containing the components.
     */
    private final HashMap slots = new HashMap();

    private String templateContents = null;

    private String templateName = null;

    /**
     * Constructs a custom layout with the template given in the stream.
     * 
     * @param templateStream
     *            Stream containing template data. Must be using UTF-8 encoding.
     *            To use a String as a template use for instance new
     *            ByteArrayInputStream("<template>".getBytes()).
     * @param streamLength
     *            Length of the templateStream
     * @throws IOException
     */
    public CustomLayout(InputStream templateStream) throws IOException {

        InputStreamReader reader = new InputStreamReader(templateStream,
                "UTF-8");
        StringBuffer b = new StringBuffer(BUFFER_SIZE);

        char[] cbuf = new char[BUFFER_SIZE];
        int offset = 0;

        while (true) {
            int nrRead = reader.read(cbuf, offset, BUFFER_SIZE);
            b.append(cbuf, 0, nrRead);
            if (nrRead < BUFFER_SIZE) {
                break;
            }
        }

        templateContents = b.toString();
    }

    /**
     * Constructor for custom layout with given template name. Template file is
     * fetched from "<theme>/layout/<templateName>".
     */
    public CustomLayout(String template) {
        templateName = template;
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    public String getTag() {
        return "customlayout";
    }

    /**
     * Adds the component into this container to given location. If the location
     * is already populated, the old component is removed.
     * 
     * @param c
     *            the component to be added.
     * @param location
     *            the location of the component.
     */
    public void addComponent(Component c, String location) {
        final Component old = (Component) slots.get(location);
        if (old != null) {
            removeComponent(old);
        }
        slots.put(location, c);
        c.setParent(this);
        fireComponentAttachEvent(c);
        requestRepaint();
    }

    /**
     * Adds the component into this container. The component is added without
     * specifying the location (empty string is then used as location). Only one
     * component can be added to the default "" location and adding more
     * components into that location overwrites the old components.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponent(Component c) {
        this.addComponent(c, "");
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    public void removeComponent(Component c) {
        if (c == null) {
            return;
        }
        slots.values().remove(c);
        c.setParent(null);
        fireComponentDetachEvent(c);
        requestRepaint();
    }

    /**
     * Removes the component from this container from given location.
     * 
     * @param location
     *            the Location identifier of the component.
     */
    public void removeComponent(String location) {
        this.removeComponent((Component) slots.get(location));
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator getComponentIterator() {
        return slots.values().iterator();
    }

    /**
     * Gets the child-component by its location.
     * 
     * @param location
     *            the name of the location where the requested component
     *            resides.
     * @return the Component in the given location or null if not found.
     */
    public Component getComponent(String location) {
        return (Component) slots.get(location);
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (templateName != null) {
            target.addAttribute("template", templateName);
        } else {
            target.addAttribute("templateContents", templateContents);
        }
        // Adds all items in all the locations
        for (final Iterator i = slots.keySet().iterator(); i.hasNext();) {
            // Gets the (location,component)
            final String location = (String) i.next();
            final Component c = (Component) slots.get(location);
            if (c != null) {
                // Writes the item
                target.startTag("location");
                target.addAttribute("name", location);
                c.paint(target);
                target.endTag("location");
            }
        }
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        // Gets the locations
        String oldLocation = null;
        String newLocation = null;
        for (final Iterator i = slots.keySet().iterator(); i.hasNext();) {
            final String location = (String) i.next();
            final Component component = (Component) slots.get(location);
            if (component == oldComponent) {
                oldLocation = location;
            }
            if (component == newComponent) {
                newLocation = location;
            }
        }

        if (oldLocation == null) {
            addComponent(newComponent);
        } else if (newLocation == null) {
            removeComponent(oldLocation);
            addComponent(newComponent, oldLocation);
        } else {
            slots.put(newLocation, oldComponent);
            slots.put(oldLocation, newComponent);
            requestRepaint();
        }
    }

    /**
     * CustomLayout's template selecting was previously implemented with
     * setStyle. Overriding to improve backwards compatibility.
     * 
     * @param name
     *            template name
     */
    public void setStyle(String name) {
        setTemplateName(name);
    }

    /** Get the name of the template */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Set the name of the template used to draw custom layout.
     * 
     * With GWT-adapter, the template with name 'templatename' is loaded from
     * ITMILL/themes/themename/layouts/templatename.html. If the theme has not
     * been set (with Application.setTheme()), themename is 'default'.
     * 
     * @param templateName
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
        templateContents = null;
        requestRepaint();
    }

    /**
     * Although most layouts support margins, CustomLayout does not. The
     * behaviour of this layout is determined almost completely by the actual
     * template.
     * 
     * @throws UnsupportedOperationException
     */
    public void setMargin(boolean enabled) {
        throw new UnsupportedOperationException(
                "CustomLayout does not support margins.");
    }

    /**
     * Although most layouts support margins, CustomLayout does not. The
     * behaviour of this layout is determined almost completely by the actual
     * template.
     * 
     * @throws UnsupportedOperationException
     */
    public void setMargin(boolean topEnabled, boolean rightEnabled,
            boolean bottomEnabled, boolean leftEnabled) {
        throw new UnsupportedOperationException(
                "CustomLayout does not support margins.");
    }

}
