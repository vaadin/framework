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

package com.vaadin.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vaadin.server.JsonPaintTarget;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.ui.customlayout.CustomLayoutState;

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
 * layout would then be implemented as an HTML template for each theme.
 * </p>
 * 
 * <p>
 * The default theme handles the styles that are not defined by drawing the
 * subcomponents just as in OrderedLayout.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CustomLayout extends AbstractLayout implements LegacyComponent {

    private static final int BUFFER_SIZE = 10000;

    /**
     * Custom layout slots containing the components.
     */
    private final HashMap<String, Component> slots = new HashMap<String, Component>();

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
     *            ByteArrayInputStream("&lt;template&gt;".getBytes()).
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
     * fetched from "&lt;theme&gt;/layout/&lt;templateName&gt;".
     */
    public CustomLayout(String template) {
        this();
        setTemplateName(template);
    }

    protected void initTemplateContentsFromInputStream(
            InputStream templateStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                templateStream, "UTF-8"));
        StringBuilder builder = new StringBuilder(BUFFER_SIZE);
        try {
            char[] cbuf = new char[BUFFER_SIZE];
            int nRead;
            while ((nRead = reader.read(cbuf, 0, BUFFER_SIZE)) > 0) {
                builder.append(cbuf, 0, nRead);
            }
        } finally {
            reader.close();
        }

        setTemplateContents(builder.toString());
    }

    @Override
    protected CustomLayoutState getState() {
        return (CustomLayoutState) super.getState();
    }

    @Override
    protected CustomLayoutState getState(boolean markAsDirty) {
        return (CustomLayoutState) super.getState(markAsDirty);
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
        getState().childLocations.put(c, location);
        c.setParent(this);
        fireComponentAttachEvent(c);
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
        getState().childLocations.remove(c);
        super.removeComponent(c);
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
    @Override
    public Iterator<Component> iterator() {
        return slots.values().iterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components
     */
    @Override
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

    /* Documented in superclass */
    @Override
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
            getState().childLocations.put(newComponent, oldLocation);
            getState().childLocations.put(oldComponent, newLocation);
        }
    }

    /** Get the name of the template */
    public String getTemplateName() {
        return getState(false).templateName;
    }

    /** Get the contents of the template */
    public String getTemplateContents() {
        return getState(false).templateContents;
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
        getState().templateName = templateName;
        getState().templateContents = null;
    }

    /**
     * Set the contents of the template used to draw the custom layout.
     * 
     * @param templateContents
     */
    public void setTemplateContents(String templateContents) {
        getState().templateContents = templateContents;
        getState().templateName = null;
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        // Nothing to see here
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        // Workaround to make the CommunicationManager read the template file
        // and send it to the client
        String templateName = getState(false).templateName;
        if (templateName != null && templateName.length() != 0) {
            Set<Object> usedResources = ((JsonPaintTarget) target)
                    .getUsedResources();
            String resourceName = "layouts/" + templateName + ".html";
            usedResources.add(resourceName);
        }
    }

}
