/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutServerRpc;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutState;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutState.ChildComponentData;

@SuppressWarnings("serial")
public abstract class AbstractOrderedLayout extends AbstractLayout implements
        Layout.AlignmentHandler, Layout.SpacingHandler, LayoutClickNotifier {

    private AbstractOrderedLayoutServerRpc rpc = new AbstractOrderedLayoutServerRpc() {

        public void layoutClick(MouseEventDetails mouseDetails,
                Connector clickedConnector) {
            fireEvent(LayoutClickEvent.createEvent(AbstractOrderedLayout.this,
                    mouseDetails, clickedConnector));
        }
    };

    public static final Alignment ALIGNMENT_DEFAULT = Alignment.TOP_LEFT;

    /**
     * Custom layout slots containing the components.
     */
    protected LinkedList<Component> components = new LinkedList<Component>();

    /* Child component alignments */

    /**
     * Mapping from components to alignments (horizontal + vertical).
     */
    public AbstractOrderedLayout() {
        registerRpc(rpc);
    }

    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    /**
     * Add a component into this container. The component is added to the right
     * or under the previous component.
     * 
     * @param c
     *            the component to be added.
     */
    @Override
    public void addComponent(Component c) {
        // Add to components before calling super.addComponent
        // so that it is available to AttachListeners
        components.add(c);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }
        componentAdded(c);
    }

    /**
     * Adds a component into this container. The component is added to the left
     * or on top of the other components.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponentAsFirst(Component c) {
        // If c is already in this, we must remove it before proceeding
        // see ticket #7668
        if (c.getParent() == this) {
            removeComponent(c);
        }
        components.addFirst(c);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }
        componentAdded(c);

    }

    /**
     * Adds a component into indexed position in this container.
     * 
     * @param c
     *            the component to be added.
     * @param index
     *            the index of the component position. The components currently
     *            in and after the position are shifted forwards.
     */
    public void addComponent(Component c, int index) {
        // If c is already in this, we must remove it before proceeding
        // see ticket #7668
        if (c.getParent() == this) {
            // When c is removed, all components after it are shifted down
            if (index > getComponentIndex(c)) {
                index--;
            }
            removeComponent(c);
        }
        components.add(index, c);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }

        componentAdded(c);
    }

    private void componentRemoved(Component c) {
        getState().getChildData().remove(c);
        requestRepaint();
    }

    private void componentAdded(Component c) {
        getState().getChildData().put(c, new ChildComponentData());
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
        components.remove(c);
        super.removeComponent(c);
        componentRemoved(c);
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator<Component> getComponentIterator() {
        return components.iterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components
     */
    public int getComponentCount() {
        return components.size();
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator<Component> i = components.iterator(); i.hasNext();) {
            final Component component = i.next();

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
            // Both old and new are in the layout
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.AlignmentHandler#setComponentAlignment(com
     * .vaadin.ui.Component, int, int)
     */
    public void setComponentAlignment(Component childComponent,
            int horizontalAlignment, int verticalAlignment) {
        Alignment a = new Alignment(horizontalAlignment + verticalAlignment);
        setComponentAlignment(childComponent, a);
    }

    public void setComponentAlignment(Component childComponent,
            Alignment alignment) {
        ChildComponentData childData = getState().getChildData().get(
                childComponent);
        if (childData != null) {
            // Alignments are bit masks
            childData.setAlignmentBitmask(alignment.getBitMask());
            requestRepaint();
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using setComponentAlignment()");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.AlignmentHandler#getComponentAlignment(com
     * .vaadin.ui.Component)
     */
    public Alignment getComponentAlignment(Component childComponent) {
        ChildComponentData childData = getState().getChildData().get(
                childComponent);
        if (childData == null) {
            throw new IllegalArgumentException(
                    "The given component is not a child of this layout");
        }

        return new Alignment(childData.getAlignmentBitmask());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.SpacingHandler#setSpacing(boolean)
     */
    public void setSpacing(boolean spacing) {
        getState().setSpacing(spacing);
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Layout.SpacingHandler#isSpacing()
     */
    public boolean isSpacing() {
        return getState().isSpacing();
    }

    /**
     * <p>
     * This method is used to control how excess space in layout is distributed
     * among components. Excess space may exist if layout is sized and contained
     * non relatively sized components don't consume all available space.
     * 
     * <p>
     * Example how to distribute 1:3 (33%) for component1 and 2:3 (67%) for
     * component2 :
     * 
     * <code>
     * layout.setExpandRatio(component1, 1);<br>
     * layout.setExpandRatio(component2, 2);
     * </code>
     * 
     * <p>
     * If no ratios have been set, the excess space is distributed evenly among
     * all components.
     * 
     * <p>
     * Note, that width or height (depending on orientation) needs to be defined
     * for this method to have any effect.
     * 
     * @see Sizeable
     * 
     * @param component
     *            the component in this layout which expand ratio is to be set
     * @param ratio
     */
    public void setExpandRatio(Component component, float ratio) {
        ChildComponentData childData = getState().getChildData().get(component);
        if (childData == null) {
            throw new IllegalArgumentException(
                    "The given component is not a child of this layout");
        }

        childData.setExpandRatio(ratio);
        requestRepaint();
    };

    /**
     * Returns the expand ratio of given component.
     * 
     * @param component
     *            which expand ratios is requested
     * @return expand ratio of given component, 0.0f by default.
     */
    public float getExpandRatio(Component component) {
        ChildComponentData childData = getState().getChildData().get(component);
        if (childData == null) {
            throw new IllegalArgumentException(
                    "The given component is not a child of this layout");
        }

        return childData.getExpandRatio();
    }

    public void addListener(LayoutClickListener listener) {
        addListener(LayoutClickEventHandler.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    public void removeListener(LayoutClickListener listener) {
        removeListener(LayoutClickEventHandler.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutClickEvent.class, listener);
    }

    /**
     * Returns the index of the given component.
     * 
     * @param component
     *            The component to look up.
     * @return The index of the component or -1 if the component is not a child.
     */
    public int getComponentIndex(Component component) {
        return components.indexOf(component);
    }

    /**
     * Returns the component at the given position.
     * 
     * @param index
     *            The position of the component.
     * @return The component at the given index.
     * @throws IndexOutOfBoundsException
     *             If the index is out of range.
     */
    public Component getComponent(int index) throws IndexOutOfBoundsException {
        return components.get(index);
    }

}
