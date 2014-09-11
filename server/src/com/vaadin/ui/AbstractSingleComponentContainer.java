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

import java.util.Collections;
import java.util.Iterator;

import com.vaadin.server.ComponentSizeValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

/**
 * Abstract base class for component containers that have only one child
 * component.
 * 
 * For component containers that support multiple children, inherit
 * {@link AbstractComponentContainer} instead of this class.
 * 
 * @since 7.0
 */
public abstract class AbstractSingleComponentContainer extends
        AbstractComponent implements SingleComponentContainer {

    private Component content;

    @Override
    public int getComponentCount() {
        return (content != null) ? 1 : 0;
    }

    @Override
    public Iterator<Component> iterator() {
        if (content != null) {
            return Collections.singletonList(content).iterator();
        } else {
            return Collections.<Component> emptyList().iterator();
        }
    }

    /* documented in interface */
    @Override
    public void addComponentAttachListener(ComponentAttachListener listener) {
        addListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);

    }

    /* documented in interface */
    @Override
    public void removeComponentAttachListener(ComponentAttachListener listener) {
        removeListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);
    }

    /* documented in interface */
    @Override
    public void addComponentDetachListener(ComponentDetachListener listener) {
        addListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /* documented in interface */
    @Override
    public void removeComponentDetachListener(ComponentDetachListener listener) {
        removeListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /**
     * Fires the component attached event. This is called by the
     * {@link #setContent(Component)} method after the component has been set as
     * the content.
     * 
     * @param component
     *            the component that has been added to this container.
     */
    protected void fireComponentAttachEvent(Component component) {
        fireEvent(new ComponentAttachEvent(this, component));
    }

    /**
     * Fires the component detached event. This is called by the
     * {@link #setContent(Component)} method after the content component has
     * been replaced by other content.
     * 
     * @param component
     *            the component that has been removed from this container.
     */
    protected void fireComponentDetachEvent(Component component) {
        fireEvent(new ComponentDetachEvent(this, component));
    }

    @Override
    public Component getContent() {
        return content;
    }

    /**
     * Sets the content of this container. The content is a component that
     * serves as the outermost item of the visual contents.
     * 
     * The content must always be set, either with a constructor parameter or by
     * calling this method.
     * 
     * Previous versions of Vaadin used a {@link VerticalLayout} with margins
     * enabled as the default content but that is no longer the case.
     * 
     * @param content
     *            a component (typically a layout) to use as content
     */
    @Override
    public void setContent(Component content) {
        // Make sure we're not adding the component inside it's own content
        if (isOrHasAncestor(content)) {
            throw new IllegalArgumentException(
                    "Component cannot be added inside it's own content");
        }

        Component oldContent = getContent();
        if (oldContent == content) {
            // do not set the same content twice
            return;
        }
        if (oldContent != null && equals(oldContent.getParent())) {
            oldContent.setParent(null);
            fireComponentDetachEvent(oldContent);
        }
        this.content = content;
        if (content != null) {
            removeFromParent(content);

            content.setParent(this);
            fireComponentAttachEvent(content);
        }

        markAsDirty();
    }

    /**
     * Utility method for removing a component from its parent (if possible).
     * 
     * @param content
     *            component to remove
     */
    // TODO move utility method elsewhere?
    public static void removeFromParent(Component content)
            throws IllegalArgumentException {
        // Verify the appropriate session is locked
        UI parentUI = content.getUI();
        if (parentUI != null) {
            VaadinSession parentSession = parentUI.getSession();
            if (parentSession != null && !parentSession.hasLock()) {
                String message = "Cannot remove from parent when the session is not locked.";
                if (VaadinService.isOtherSessionLocked(parentSession)) {
                    message += " Furthermore, there is another locked session, indicating that the component might be about to be moved from one session to another.";
                }
                throw new IllegalStateException(message);
            }
        }

        HasComponents parent = content.getParent();
        if (parent instanceof ComponentContainer) {
            // If the component already has a parent, try to remove it
            ComponentContainer oldParent = (ComponentContainer) parent;
            oldParent.removeComponent(content);
        } else if (parent instanceof SingleComponentContainer) {
            SingleComponentContainer oldParent = (SingleComponentContainer) parent;
            if (oldParent.getContent() == content) {
                oldParent.setContent(null);
            }
        } else if (parent != null) {
            throw new IllegalArgumentException(
                    "Content is already attached to another parent");
        }
    }

    // the setHeight()/setWidth() methods duplicated and simplified from
    // AbstractComponentContainer

    @Override
    public void setWidth(float width, Unit unit) {
        /*
         * child tree repaints may be needed, due to our fall back support for
         * invalid relative sizes
         */
        boolean dirtyChild = false;
        boolean childrenMayBecomeUndefined = false;
        if (getWidth() == SIZE_UNDEFINED && width != SIZE_UNDEFINED) {
            // children currently in invalid state may need repaint
            dirtyChild = getInvalidSizedChild(false);
        } else if ((width == SIZE_UNDEFINED && getWidth() != SIZE_UNDEFINED)
                || (unit == Unit.PERCENTAGE
                        && getWidthUnits() != Unit.PERCENTAGE && !ComponentSizeValidator
                            .parentCanDefineWidth(this))) {
            /*
             * relative width children may get to invalid state if width becomes
             * invalid. Width may also become invalid if units become percentage
             * due to the fallback support
             */
            childrenMayBecomeUndefined = true;
            dirtyChild = getInvalidSizedChild(false);
        }
        super.setWidth(width, unit);
        repaintChangedChildTree(dirtyChild, childrenMayBecomeUndefined, false);
    }

    private void repaintChangedChildTree(boolean invalidChild,
            boolean childrenMayBecomeUndefined, boolean vertical) {
        if (getContent() == null) {
            return;
        }
        boolean needRepaint = false;
        if (childrenMayBecomeUndefined) {
            // if became invalid now
            needRepaint = !invalidChild && getInvalidSizedChild(vertical);
        } else if (invalidChild) {
            // if not still invalid
            needRepaint = !getInvalidSizedChild(vertical);
        }
        if (needRepaint) {
            getContent().markAsDirtyRecursive();
        }
    }

    private boolean getInvalidSizedChild(final boolean vertical) {
        Component content = getContent();
        if (content == null) {
            return false;
        }
        if (vertical) {
            return !ComponentSizeValidator.checkHeights(content);
        } else {
            return !ComponentSizeValidator.checkWidths(content);
        }
    }

    @Override
    public void setHeight(float height, Unit unit) {
        /*
         * child tree repaints may be needed, due to our fall back support for
         * invalid relative sizes
         */
        boolean dirtyChild = false;
        boolean childrenMayBecomeUndefined = false;
        if (getHeight() == SIZE_UNDEFINED && height != SIZE_UNDEFINED) {
            // children currently in invalid state may need repaint
            dirtyChild = getInvalidSizedChild(true);
        } else if ((height == SIZE_UNDEFINED && getHeight() != SIZE_UNDEFINED)
                || (unit == Unit.PERCENTAGE
                        && getHeightUnits() != Unit.PERCENTAGE && !ComponentSizeValidator
                            .parentCanDefineHeight(this))) {
            /*
             * relative height children may get to invalid state if height
             * becomes invalid. Height may also become invalid if units become
             * percentage due to the fallback support.
             */
            childrenMayBecomeUndefined = true;
            dirtyChild = getInvalidSizedChild(true);
        }
        super.setHeight(height, unit);
        repaintChangedChildTree(dirtyChild, childrenMayBecomeUndefined, true);
    }

}
