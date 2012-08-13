/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import com.vaadin.event.ComponentEventListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelRpc;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState.SplitterState;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.ui.ClickEventHandler;
import com.vaadin.tools.ReflectTools;

/**
 * AbstractSplitPanel.
 * 
 * <code>AbstractSplitPanel</code> is base class for a component container that
 * can contain two components. The components are split by a divider element.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 6.5
 */
public abstract class AbstractSplitPanel extends AbstractComponentContainer {

    // TODO use Unit in AbstractSplitPanelState and remove these
    private Unit posUnit;
    private Unit posMinUnit;
    private Unit posMaxUnit;

    private AbstractSplitPanelRpc rpc = new AbstractSplitPanelRpc() {

        @Override
        public void splitterClick(MouseEventDetails mouseDetails) {
            fireEvent(new SplitterClickEvent(AbstractSplitPanel.this,
                    mouseDetails));
        }

        @Override
        public void setSplitterPosition(float position) {
            getSplitterState().setPosition(position);
        }
    };

    public AbstractSplitPanel() {
        registerRpc(rpc);
        setSplitPosition(50, Unit.PERCENTAGE, false);
        setSplitPositionLimits(0, Unit.PERCENTAGE, 100, Unit.PERCENTAGE);
    }

    /**
     * Modifiable and Serializable Iterator for the components, used by
     * {@link AbstractSplitPanel#getComponentIterator()}.
     */
    private class ComponentIterator implements Iterator<Component>,
            Serializable {

        int i = 0;

        @Override
        public boolean hasNext() {
            if (i < getComponentCount()) {
                return true;
            }
            return false;
        }

        @Override
        public Component next() {
            if (!hasNext()) {
                return null;
            }
            i++;
            if (i == 1) {
                return (getFirstComponent() == null ? getSecondComponent()
                        : getFirstComponent());
            } else if (i == 2) {
                return getSecondComponent();
            }
            return null;
        }

        @Override
        public void remove() {
            if (i == 1) {
                if (getFirstComponent() != null) {
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
        if (getFirstComponent() == null) {
            setFirstComponent(c);
        } else if (getSecondComponent() == null) {
            setSecondComponent(c);
        } else {
            throw new UnsupportedOperationException(
                    "Split panel can contain only two components");
        }
    }

    /**
     * Sets the first component of this split panel. Depending on the direction
     * the first component is shown at the top or to the left.
     * 
     * @param c
     *            The component to use as first component
     */
    public void setFirstComponent(Component c) {
        if (getFirstComponent() == c) {
            // Nothing to do
            return;
        }

        if (getFirstComponent() != null) {
            // detach old
            removeComponent(getFirstComponent());
        }
        getState().setFirstChild(c);
        if (c != null) {
            super.addComponent(c);
        }

        requestRepaint();
    }

    /**
     * Sets the second component of this split panel. Depending on the direction
     * the second component is shown at the bottom or to the left.
     * 
     * @param c
     *            The component to use as first component
     */
    public void setSecondComponent(Component c) {
        if (getSecondComponent() == c) {
            // Nothing to do
            return;
        }

        if (getSecondComponent() != null) {
            // detach old
            removeComponent(getSecondComponent());
        }
        getState().setSecondChild(c);
        if (c != null) {
            super.addComponent(c);
        }
        requestRepaint();
    }

    /**
     * Gets the first component of this split panel. Depending on the direction
     * this is either the component shown at the top or to the left.
     * 
     * @return the first component of this split panel
     */
    public Component getFirstComponent() {
        return (Component) getState().getFirstChild();
    }

    /**
     * Gets the second component of this split panel. Depending on the direction
     * this is either the component shown at the top or to the left.
     * 
     * @return the second component of this split panel
     */
    public Component getSecondComponent() {
        return (Component) getState().getSecondChild();
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
        if (c == getFirstComponent()) {
            getState().setFirstChild(null);
        } else if (c == getSecondComponent()) {
            getState().setSecondChild(null);
        }
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */

    @Override
    public Iterator<Component> getComponentIterator() {
        return new ComponentIterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components (zero, one or two)
     */

    @Override
    public int getComponentCount() {
        int count = 0;
        if (getFirstComponent() != null) {
            count++;
        }
        if (getSecondComponent() != null) {
            count++;
        }
        return count;
    }

    /* Documented in superclass */

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        if (oldComponent == getFirstComponent()) {
            setFirstComponent(newComponent);
        } else if (oldComponent == getSecondComponent()) {
            setSecondComponent(newComponent);
        }
        requestRepaint();
    }

    /**
     * Moves the position of the splitter.
     * 
     * @param pos
     *            the new size of the first region in the unit that was last
     *            used (default is percentage). Fractions are only allowed when
     *            unit is percentage.
     */
    public void setSplitPosition(float pos) {
        setSplitPosition(pos, posUnit, false);
    }

    /**
     * Moves the position of the splitter.
     * 
     * @param pos
     *            the new size of the region in the unit that was last used
     *            (default is percentage). Fractions are only allowed when unit
     *            is percentage.
     * 
     * @param reverse
     *            if set to true the split splitter position is measured by the
     *            second region else it is measured by the first region
     */
    public void setSplitPosition(float pos, boolean reverse) {
        setSplitPosition(pos, posUnit, reverse);
    }

    /**
     * Moves the position of the splitter with given position and unit.
     * 
     * @param pos
     *            the new size of the first region. Fractions are only allowed
     *            when unit is percentage.
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     */
    public void setSplitPosition(float pos, Unit unit) {
        setSplitPosition(pos, unit, false);
    }

    /**
     * Moves the position of the splitter with given position and unit.
     * 
     * @param pos
     *            the new size of the first region. Fractions are only allowed
     *            when unit is percentage.
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     * @param reverse
     *            if set to true the split splitter position is measured by the
     *            second region else it is measured by the first region
     * 
     */
    public void setSplitPosition(float pos, Unit unit, boolean reverse) {
        if (unit != Unit.PERCENTAGE && unit != Unit.PIXELS) {
            throw new IllegalArgumentException(
                    "Only percentage and pixel units are allowed");
        }
        if (unit != Unit.PERCENTAGE) {
            pos = Math.round(pos);
        }
        SplitterState splitterState = getSplitterState();
        splitterState.setPosition(pos);
        splitterState.setPositionUnit(unit.getSymbol());
        splitterState.setPositionReversed(reverse);
        posUnit = unit;

        requestRepaint();
    }

    /**
     * Returns the current position of the splitter, in
     * {@link #getSplitPositionUnit()} units.
     * 
     * @return position of the splitter
     */
    public float getSplitPosition() {
        return getSplitterState().getPosition();
    }

    /**
     * Returns the unit of position of the splitter
     * 
     * @return unit of position of the splitter
     */
    public Unit getSplitPositionUnit() {
        return posUnit;
    }

    /**
     * Sets the minimum split position to the given position and unit. If the
     * split position is reversed, maximum and minimum are also reversed.
     * 
     * @param pos
     *            the minimum position of the split
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     *            Allowed units are UNITS_PERCENTAGE and UNITS_PIXELS
     */
    public void setMinSplitPosition(int pos, Unit unit) {
        setSplitPositionLimits(pos, unit, getSplitterState().getMaxPosition(),
                posMaxUnit);
    }

    /**
     * Returns the current minimum position of the splitter, in
     * {@link #getMinSplitPositionUnit()} units.
     * 
     * @return the minimum position of the splitter
     */
    public float getMinSplitPosition() {
        return getSplitterState().getMinPosition();
    }

    /**
     * Returns the unit of the minimum position of the splitter.
     * 
     * @return the unit of the minimum position of the splitter
     */
    public Unit getMinSplitPositionUnit() {
        return posMinUnit;
    }

    /**
     * Sets the maximum split position to the given position and unit. If the
     * split position is reversed, maximum and minimum are also reversed.
     * 
     * @param pos
     *            the maximum position of the split
     * @param unit
     *            the unit (from {@link Sizeable}) in which the size is given.
     *            Allowed units are UNITS_PERCENTAGE and UNITS_PIXELS
     */
    public void setMaxSplitPosition(float pos, Unit unit) {
        setSplitPositionLimits(getSplitterState().getMinPosition(), posMinUnit,
                pos, unit);
    }

    /**
     * Returns the current maximum position of the splitter, in
     * {@link #getMaxSplitPositionUnit()} units.
     * 
     * @return the maximum position of the splitter
     */
    public float getMaxSplitPosition() {
        return getSplitterState().getMaxPosition();
    }

    /**
     * Returns the unit of the maximum position of the splitter
     * 
     * @return the unit of the maximum position of the splitter
     */
    public Unit getMaxSplitPositionUnit() {
        return posMaxUnit;
    }

    /**
     * Sets the maximum and minimum position of the splitter. If the split
     * position is reversed, maximum and minimum are also reversed.
     * 
     * @param minPos
     *            the new minimum position
     * @param minPosUnit
     *            the unit (from {@link Sizeable}) in which the minimum position
     *            is given.
     * @param maxPos
     *            the new maximum position
     * @param maxPosUnit
     *            the unit (from {@link Sizeable}) in which the maximum position
     *            is given.
     */
    private void setSplitPositionLimits(float minPos, Unit minPosUnit,
            float maxPos, Unit maxPosUnit) {
        if ((minPosUnit != Unit.PERCENTAGE && minPosUnit != Unit.PIXELS)
                || (maxPosUnit != Unit.PERCENTAGE && maxPosUnit != Unit.PIXELS)) {
            throw new IllegalArgumentException(
                    "Only percentage and pixel units are allowed");
        }

        SplitterState state = getSplitterState();

        state.setMinPosition(minPos);
        state.setMinPositionUnit(minPosUnit.getSymbol());
        posMinUnit = minPosUnit;

        state.setMaxPosition(maxPos);
        state.setMaxPositionUnit(maxPosUnit.getSymbol());
        posMaxUnit = maxPosUnit;

        requestRepaint();
    }

    /**
     * Lock the SplitPanels position, disabling the user from dragging the split
     * handle.
     * 
     * @param locked
     *            Set <code>true</code> if locked, <code>false</code> otherwise.
     */
    public void setLocked(boolean locked) {
        getSplitterState().setLocked(locked);
        requestRepaint();
    }

    /**
     * Is the SplitPanel handle locked (user not allowed to change split
     * position by dragging).
     * 
     * @return <code>true</code> if locked, <code>false</code> otherwise.
     */
    public boolean isLocked() {
        return getSplitterState().isLocked();
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
        addListener(ClickEventHandler.CLICK_EVENT_IDENTIFIER,
                SplitterClickEvent.class, listener,
                SplitterClickListener.clickMethod);
    }

    public void removeListener(SplitterClickListener listener) {
        removeListener(ClickEventHandler.CLICK_EVENT_IDENTIFIER,
                SplitterClickEvent.class, listener);
    }

    @Override
    public AbstractSplitPanelState getState() {
        return (AbstractSplitPanelState) super.getState();
    }

    private SplitterState getSplitterState() {
        return getState().getSplitterState();
    }
}
