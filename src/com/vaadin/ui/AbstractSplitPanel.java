/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.event.ComponentEventListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VSplitPanel;
import com.vaadin.tools.ReflectTools;

/**
 * AbstractSplitPanel.
 * 
 * <code>AbstractSplitPanel</code> is base class for a component container that
 * can contain two components. The comopnents are split by a divider element.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 6.5
 */
public abstract class AbstractSplitPanel extends AbstractLayout {

    private Component firstComponent;

    private Component secondComponent;

    private int pos = 50;

    private int posUnit = UNITS_PERCENTAGE;

    private boolean posReversed = false;

    private boolean locked = false;

    private static final String SPLITTER_CLICK_EVENT = VSplitPanel.SPLITTER_CLICK_EVENT_IDENTIFIER;

    /**
     * Modifiable and Serializable Iterator for the components, used by
     * {@link AbstractSplitPanel#getComponentIterator()}.
     */
    private class ComponentIterator implements Iterator<Component>,
            Serializable {

        int i = 0;

        public boolean hasNext() {
            if (i < getComponentCount()) {
                return true;
            }
            return false;
        }

        public Component next() {
            if (!hasNext()) {
                return null;
            }
            i++;
            if (i == 1) {
                return firstComponent == null ? secondComponent
                        : firstComponent;
            } else if (i == 2) {
                return secondComponent;
            }
            return null;
        }

        public void remove() {
            if (i == 1) {
                if (firstComponent != null) {
                    setFirstComponent(null);
                    i = 0;
                } else {
                    setSecondComponent(null);
                }
            } else if (i == 2) {
                setSecondComponent(null);
            }
        }
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
        if (firstComponent == null) {
            firstComponent = c;
        } else if (secondComponent == null) {
            secondComponent = c;
        } else {
            throw new UnsupportedOperationException(
                    "Split panel can contain only two components");
        }
        super.addComponent(c);
        requestRepaint();
    }

    public void setFirstComponent(Component c) {
        if (firstComponent == c) {
            // Nothing to do
            return;
        }

        if (firstComponent != null) {
            // detach old
            removeComponent(firstComponent);
        }
        firstComponent = c;
        super.addComponent(c);
        requestRepaint();
    }

    public void setSecondComponent(Component c) {
        if (c == secondComponent) {
            // Nothing to do
            return;
        }

        if (secondComponent != null) {
            // detach old
            removeComponent(secondComponent);
        }
        secondComponent = c;
        super.addComponent(c);
        requestRepaint();
    }

    /**
     * @return the first component of this SplitPanel.
     */
    public Component getFirstComponent() {
        return firstComponent;
    }

    /**
     * @return the second component of this SplitPanel.
     */
    public Component getSecondComponent() {
        return secondComponent;
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
        if (c == firstComponent) {
            firstComponent = null;
        } else if (c == secondComponent) {
            secondComponent = null;
        }
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator<Component> getComponentIterator() {
        return new ComponentIterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components (zero, one or two)
     */
    public int getComponentCount() {
        int count = 0;
        if (firstComponent != null) {
            count++;
        }
        if (secondComponent != null) {
            count++;
        }
        return count;
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

        final String position = pos + UNIT_SYMBOLS[posUnit];

        target.addAttribute("position", position);

        if (isLocked()) {
            target.addAttribute("locked", true);
        }

        target.addAttribute("reversed", posReversed);

        if (firstComponent != null) {
            firstComponent.paint(target);
        } else {
            VerticalLayout temporaryComponent = new VerticalLayout();
            temporaryComponent.setParent(this);
            temporaryComponent.paint(target);
        }
        if (secondComponent != null) {
            secondComponent.paint(target);
        } else {
            VerticalLayout temporaryComponent = new VerticalLayout();
            temporaryComponent.setParent(this);
            temporaryComponent.paint(target);
        }
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {
        if (oldComponent == firstComponent) {
            setFirstComponent(newComponent);
        } else if (oldComponent == secondComponent) {
            setSecondComponent(newComponent);
        }
        requestRepaint();
    }

    /**
     * Moves the position of the splitter.
     * 
     * @param pos
     *            the new size of the first region in the unit that was last
     *            used (default is percentage)
     */
    public void setSplitPosition(int pos) {
        setSplitPosition(pos, posUnit, true, false);
    }

    /**
     * Moves the position of the splitter.
     * 
     * @param pos
     *            the new size of the region in the unit that was last used
     *            (default is percentage)
     * @param reverse
     *            if set to true the split splitter position is measured by the
     *            second region else it is measured by the first region
     */
    public void setSplitPosition(int pos, boolean reverse) {
        setSplitPosition(pos, posUnit, true, reverse);
    }

    /**
     * Moves the position of the splitter with given position and unit.
     * 
     * @param pos
     *            size of the first region
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     */
    public void setSplitPosition(int pos, int unit) {
        setSplitPosition(pos, unit, true, false);
    }

    /**
     * Moves the position of the splitter with given position and unit.
     * 
     * @param pos
     *            size of the first region
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     * @param reverse
     *            if set to true the split splitter position is measured by the
     *            second region else it is measured by the first region
     * 
     */
    public void setSplitPosition(int pos, int unit, boolean reverse) {
        setSplitPosition(pos, unit, true, reverse);
    }

    /**
     * Returns the current position of the splitter, in
     * {@link #getSplitPositionUnit()} units.
     * 
     * @return position of the splitter
     */
    public int getSplitPosition() {
        return pos;
    }

    /**
     * Returns the unit of position of the splitter
     * 
     * @return unit of position of the splitter
     */
    public int getSplitPositionUnit() {
        return posUnit;
    }

    /**
     * Moves the position of the splitter.
     * 
     * @param pos
     *            the new size of the first region
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     * @param repaintNotNeeded
     *            true if client side needs to be updated. Use false if the
     *            position info has come from the client side, thus it already
     *            knows the position.
     */
    private void setSplitPosition(int pos, int unit, boolean repaintNeeded,
            boolean reverse) {
        if (unit != UNITS_PERCENTAGE && unit != UNITS_PIXELS) {
            throw new IllegalArgumentException(
                    "Only percentage and pixel units are allowed");
        }
        this.pos = pos;
        posUnit = unit;
        posReversed = reverse;
        if (repaintNeeded) {
            requestRepaint();
        }
    }

    /**
     * Lock the SplitPanels position, disabling the user from dragging the split
     * handle.
     * 
     * @param locked
     *            Set <code>true</code> if locked, <code>false</code> otherwise.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
        requestRepaint();
    }

    /**
     * Is the SplitPanel handle locked (user not allowed to change split
     * position by dragging).
     * 
     * @return <code>true</code> if locked, <code>false</code> otherwise.
     */
    public boolean isLocked() {
        return locked;
    }

    /*
     * Invoked when a variable of the component changes. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        super.changeVariables(source, variables);

        if (variables.containsKey("position") && !isLocked()) {
            Integer newPos = (Integer) variables.get("position");
            setSplitPosition(newPos, posUnit, posReversed);
        }

        if (variables.containsKey(SPLITTER_CLICK_EVENT)) {
            fireClick((Map<String, Object>) variables.get(SPLITTER_CLICK_EVENT));
        }

    }

    @Override
    protected void fireClick(Map<String, Object> parameters) {
        MouseEventDetails mouseDetails = MouseEventDetails
                .deSerialize((String) parameters.get("mouseDetails"));

        fireEvent(new SplitterClickEvent(this, mouseDetails));
    }

    /**
     * <code>SplitterClickListener</code> interface for listening for
     * <code>SplitterClickEvent</code> fired by a <code>SplitPanel</code>.
     * 
     * @see SplitterClickEvent
     * @since 6.2
     */
    public interface SplitterClickListener extends ComponentEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                SplitterClickListener.class, "splitterClick",
                SplitterClickEvent.class);

        /**
         * SplitPanel splitter has been clicked
         * 
         * @param event
         *            SplitterClickEvent event.
         */
        public void splitterClick(SplitterClickEvent event);
    }

    public class SplitterClickEvent extends ClickEvent {

        public SplitterClickEvent(Component source,
                MouseEventDetails mouseEventDetails) {
            super(source, mouseEventDetails);
        }

    }

    public void addListener(SplitterClickListener listener) {
        addListener(SPLITTER_CLICK_EVENT, SplitterClickEvent.class, listener,
                SplitterClickListener.clickMethod);
    }

    public void removeListener(SplitterClickListener listener) {
        removeListener(SPLITTER_CLICK_EVENT, SplitterClickEvent.class, listener);
    }

}
