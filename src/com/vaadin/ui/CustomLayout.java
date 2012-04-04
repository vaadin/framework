/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * <p>
 * A container component with freely designed layout and style. The layout
 * consists of items with textually represented locations. Each item contains
 * one sub-component, which can be any Vaadin component, such as a layout. The
 * adapter and theme are responsible for rendering the layout with a given style
 * by placing the items in the defined locations.
 * </p>
 * 
 * <p>
 * The placement of the locations is not fixed - different themes can define the
 * locations in a way that is suitable for them. One typical example would be to
 * create visual design for a web site as a custom layout: the visual design
 * would define locations for "menu", "body", and "title", for example. The
 * layout would then be implemented as an XHTML template for each theme.
 * </p>
 * 
 * <p>
 * The default theme handles the styles that are not defined by drawing the
 * subcomponents just as in OrderedLayout.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @author Duy B. Vo (<a
 *         href="mailto:devduy@gmail.com?subject=Vaadin">devduy@gmail.com</a>)
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CustomLayout extends AbstractLayout {

    private static final int BUFFER_SIZE = 10000;

    /**
     * Custom layout slots containing the components.
     */
    private final HashMap<String, Component> slots = new HashMap<String, Component>();

    private String templateContents = null;

    private String templateName = null;

    /**
     * Default constructor only used by subclasses. Subclasses are responsible
     * for setting the appropriate fields. Either
     * {@link #setTemplateName(String)}, that makes layout fetch the template
     * from theme, or {@link #setTemplateContents(String)}.
     */
    protected CustomLayout() {
        setWidth(100, UNITS_PERCENTAGE);
    }

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
        this();
        initTemplateContentsFromInputStream(templateStream);
    }

    /**
     * Constructor for custom layout with given template name. Template file is
     * fetched from "<theme>/layout/<templateName>".
     */
    public CustomLayout(String template) {
        this();
        templateName = template;
    }

    protected void initTemplateContentsFromInputStream(
            InputStream templateStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(templateStream,
                "UTF-8");
        StringBuilder b = new StringBuilder(BUFFER_SIZE);

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
     * Adds the component into this container to given location. If the location
     * is already populated, the old component is removed.
     * 
     * @param c
     *            the component to be added.
     * @param location
     *            the location of the component.
     */
    public void addComponent(Component c, String location) {
        final Component old = slots.get(location);
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
    @Override
    public void addComponent(Component c) {
        this.addComponent(c, "");
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component c) {
        if (c == null) {
            return;
        }
        slots.values().remove(c);
        super.removeComponent(c);
        requestRepaint();
    }

    /**
     * Removes the component from this container from given location.
     * 
     * @param location
     *            the Location identifier of the component.
     */
    public void removeComponent(String location) {
        this.removeComponent(slots.get(location));
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator<Component> getComponentIterator() {
        return slots.values().iterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components
     */
    public int getComponentCount() {
        return slots.values().size();
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
        return slots.get(location);
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (templateName != null) {
            target.addAttribute("template", templateName);
        } else {
            target.addAttribute("templateContents", templateContents);
        }
        // Adds all items in all the locations
        for (final Iterator<String> i = slots.keySet().iterator(); i.hasNext();) {
            // Gets the (location,component)
            final String location = i.next();
            final Component c = slots.get(location);
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
        for (final Iterator<String> i = slots.keySet().iterator(); i.hasNext();) {
            final String location = i.next();
            final Component component = slots.get(location);
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
     * @deprecated Use {@link #setTemplateName(String)} instead
     */
    @Deprecated
    @Override
    public void setStyle(String name) {
        setTemplateName(name);
    }

    /** Get the name of the template */
    public String getTemplateName() {
        return templateName;
    }

    /** Get the contents of the template */
    public String getTemplateContents() {
        return templateContents;
    }

    /**
     * Set the name of the template used to draw custom layout.
     * 
     * With GWT-adapter, the template with name 'templatename' is loaded from
     * VAADIN/themes/themename/layouts/templatename.html. If the theme has not
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
     * Set the contents of the template used to draw the custom layout.
     * 
     * @param templateContents
     */
    public void setTemplateContents(String templateContents) {
        this.templateContents = templateContents;
        templateName = null;
        requestRepaint();
    }

    /**
     * Although most layouts support margins, CustomLayout does not. The
     * behaviour of this layout is determined almost completely by the actual
     * template.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
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
    @Override
    public void setMargin(boolean topEnabled, boolean rightEnabled,
            boolean bottomEnabled, boolean leftEnabled) {
        throw new UnsupportedOperationException(
                "CustomLayout does not support margins.");
    }

}
