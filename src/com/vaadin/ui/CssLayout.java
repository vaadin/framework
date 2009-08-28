package com.vaadin.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.ui.VCssLayout;

/**
 * CssLayout is a layout component that can be used in browser environment only.
 * It simply renders components and their captions into a same div element.
 * Component layout can then be adjusted with css.
 * <p>
 * In comparison to {@link HorizontalLayout} and {@link VerticalLayout}
 * <ul>
 * <li>rather similar server side api
 * <li>no spacing, alignment or expand ratios
 * <li>much simpler DOM that can be styled by skilled web developer
 * <li>no abstraction of browser differences (developer must ensure that the
 * result works properly on each browser)
 * <li>different kind of handling for relative sizes (that are set from server
 * side) (*)
 * <li>noticeable faster rendering time in some situations as we rely more on
 * browsers rendering engine.
 * </ul>
 *<p>
 * With {@link CustomLayout} one can often achieve similar results (good looking
 * layouts with web technologies), but with CustomLayout developer needs to work
 * with fixed templates.
 * <p>
 * By extending CssLayout one can also inject some css rules straight to child
 * components using {@link #getCss(Component)}.
 * 
 * <p>
 * (*) Relative sizes (set from server side) are treated bit differently than in
 * other layouts in Vaadin. In cssLayout the size is calculated relatively to
 * CSS layouts content area which is pretty much as in html and css. In other
 * layouts the size of component is calculated relatively to the "slot" given by
 * layout.
 * <p>
 * Also note that client side framework in Vaadin modifies inline style
 * properties width and height. This happens on each update to component. If one
 * wants to set component sizes with CSS, component must have undefined size on
 * server side (which is not the default for all components) and the size must
 * be defined with class styles - not by directly injecting width and height.
 * 
 * @since 6.1 brought in from "FastLayouts" incubator project
 * 
 */
public class CssLayout extends AbstractLayout {

    private static final long serialVersionUID = -6408703812053460073L;

    @Override
    public String getTag() {
        return VCssLayout.TAGNAME;
    }

    /**
     * Custom layout slots containing the components.
     */
    protected LinkedList<Component> components = new LinkedList<Component>();

    /**
     * Add a component into this container. The component is added to the right
     * or under the previous component.
     * 
     * @param c
     *            the component to be added.
     */
    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        components.add(c);
        requestRepaint();
    }

    /**
     * Adds a component into this container. The component is added to the left
     * or on top of the other components.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponentAsFirst(Component c) {
        super.addComponent(c);
        components.addFirst(c);
        requestRepaint();
    }

    /**
     * Adds a component into indexed position in this container.
     * 
     * @param c
     *            the component to be added.
     * @param index
     *            the Index of the component position. The components currently
     *            in and after the position are shifted forwards.
     */
    public void addComponent(Component c, int index) {
        super.addComponent(c);
        components.add(index, c);
        requestRepaint();
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component c) {
        super.removeComponent(c);
        components.remove(c);
        requestRepaint();
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator getComponentIterator() {
        return components.iterator();
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        HashMap<Paintable, String> componentCss = null;
        // Adds all items in all the locations
        for (Component c : components) {
            // Paint child component UIDL
            c.paint(target);
            String componentCssString = getCss(c);
            if (componentCssString != null) {
                if (componentCss == null) {
                    componentCss = new HashMap<Paintable, String>();
                }
                componentCss.put(c, componentCssString);
            }
        }
        if (componentCss != null) {
            target.addAttribute("css", componentCss);
        }
    }

    /**
     * Returns styles to be applied to given component. Override this method to
     * inject custom style rules to components.
     * 
     * <p>
     * Note that styles are injected over previous styles before actual child
     * rendering. Previous styles are not cleared, but overridden.
     * 
     * <p>
     * Note that one most often achieves better code style, by separating
     * styling to theme (with custom theme and {@link #addStyleName(String)}.
     * With own custom styles it is also very easy to break browser
     * compatibility.
     * 
     * @param c
     *            the component
     * @return css rules to be applied to component
     */
    protected String getCss(Component c) {
        return null;
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component component = (Component) i.next();

            if (component == oldComponent) {
                oldLocation = location;
            }
            if (component == newComponent) {
                newLocation = location;
            }

            location++;
        }

        if (oldLocation == -1) {
            addComponent(newComponent);
        } else if (newLocation == -1) {
            removeComponent(oldComponent);
            addComponent(newComponent, oldLocation);
        } else {
            if (oldLocation > newLocation) {
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
            } else {
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
            }

            requestRepaint();
        }
    }

}
