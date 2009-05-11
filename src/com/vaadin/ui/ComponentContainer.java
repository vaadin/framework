/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Extension to the {@link Component} interface which adds to it the capacity to
 * contain other components. All UI elements that can have child elements
 * implement this interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface ComponentContainer extends Component {

    /**
     * Adds the component into this container.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponent(Component c);

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be added.
     */
    public void removeComponent(Component c);

    /**
     * Removes all components from this container.
     */
    public void removeAllComponents();

    /**
     * Replaces the component in the container with another one without changing
     * position.
     * 
     * <p>
     * This method replaces component with another one is such way that the new
     * component overtakes the position of the old component. If the old
     * component is not in the container, the new component is added to the
     * container. If the both component are already in the container, their
     * positions are swapped. Component attach and detach events should be taken
     * care as with add and remove.
     * </p>
     * 
     * @param oldComponent
     *            the old component that will be replaced.
     * @param newComponent
     *            the new component to be replaced.
     */
    public void replaceComponent(Component oldComponent, Component newComponent);

    /**
     * Gets an iterator to the collection of contained components. Using this
     * iterator it is possible to step through all components contained in this
     * container.
     * 
     * @return the component iterator.
     */
    public Iterator getComponentIterator();

    /**
     * Causes a repaint of this component, and all components below it.
     * 
     * This should only be used in special cases, e.g when the state of a
     * descendant depends on the state of a ancestor.
     * 
     */
    public void requestRepaintAll();

    /**
     * Moves all components from an another container into this container. The
     * components are removed from <code>source</code>.
     * 
     * @param source
     *            the container which contains the components that are to be
     *            moved to this container.
     */
    public void moveComponentsFrom(ComponentContainer source);

    /**
     * Listens the component attach events.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(ComponentAttachListener listener);

    /**
     * Stops the listening component attach events.
     * 
     * @param listener
     *            the listener to removed.
     */
    public void removeListener(ComponentAttachListener listener);

    /**
     * Listens the component detach events.
     */
    public void addListener(ComponentDetachListener listener);

    /**
     * Stops the listening component detach events.
     */
    public void removeListener(ComponentDetachListener listener);

    /**
     * Component attach listener interface.
     */
    public interface ComponentAttachListener extends Serializable {

        /**
         * A new component is attached to container.
         * 
         * @param event
         *            the component attach event.
         */
        public void componentAttachedToContainer(ComponentAttachEvent event);
    }

    /**
     * Component detach listener interface.
     */
    public interface ComponentDetachListener extends Serializable {

        /**
         * A component has been detached from container.
         * 
         * @param event
         *            the component detach event.
         */
        public void componentDetachedFromContainer(ComponentDetachEvent event);
    }

    /**
     * Component attach event sent when a component is attached to container.
     */
    @SuppressWarnings("serial")
    public class ComponentAttachEvent extends Component.Event {

        private final Component component;

        /**
         * Creates a new attach event.
         * 
         * @param container
         *            the component container the component has been detached
         *            to.
         * @param attachedComponent
         *            the component that has been attached.
         */
        public ComponentAttachEvent(ComponentContainer container,
                Component attachedComponent) {
            super(container);
            component = attachedComponent;
        }

        /**
         * Gets the component container.
         * 
         * @param the
         *            component container.
         */
        public ComponentContainer getContainer() {
            return (ComponentContainer) getSource();
        }

        /**
         * Gets the attached component.
         * 
         * @param the
         *            attach component.
         */
        public Component getAttachedComponent() {
            return component;
        }
    }

    /**
     * Component detach event sent when a component is detached from container.
     */
    @SuppressWarnings("serial")
    public class ComponentDetachEvent extends Component.Event {

        private final Component component;

        /**
         * Creates a new detach event.
         * 
         * @param container
         *            the component container the component has been detached
         *            from.
         * @param detachedComponent
         *            the component that has been detached.
         */
        public ComponentDetachEvent(ComponentContainer container,
                Component detachedComponent) {
            super(container);
            component = detachedComponent;
        }

        /**
         * Gets the component container.
         * 
         * @param the
         *            component container.
         */
        public ComponentContainer getContainer() {
            return (ComponentContainer) getSource();
        }

        /**
         * Gets the detached component.
         * 
         * @return the detached component.
         */
        public Component getDetachedComponent() {
            return component;
        }
    }

}
