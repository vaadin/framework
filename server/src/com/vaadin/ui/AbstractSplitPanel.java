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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelRpc;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState.SplitterState;
import com.vaadin.util.ReflectTools;

/**
 * AbstractSplitPanel.
 * 
 * <code>AbstractSplitPanel</code> is base class for a component container that
 * can contain two components. The components are split by a divider element.
 * 
 * @author Vaadin Ltd.
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
            getSplitterState().position = position;
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
        getState().firstChild = c;
        if (c != null) {
            super.addComponent(c);
        }
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
        getState().secondChild = c;
        if (c != null) {
            super.addComponent(c);
        }
    }

    /**
     * Gets the first component of this split panel. Depending on the direction
     * this is either the component shown at the top or to the left.
     * 
     * @return the first component of this split panel
     */
    public Component getFirstComponent() {
        return (Component) getState(false).firstChild;
    }

    /**
     * Gets the second component of this split panel. Depending on the direction
     * this is either the component shown at the top or to the left.
     * 
     * @return the second component of this split panel
     */
    public Component getSecondComponent() {
        return (Component) getState(false).secondChild;
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
            getState().firstChild = null;
        } else if (c == getSecondComponent()) {
            getState().secondChild = null;
        }
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */

    @Override
    public Iterator<Component> iterator() {
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
        markAsDirty();
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
        splitterState.position = pos;
        splitterState.positionUnit = unit.getSymbol();
        splitterState.positionReversed = reverse;
        posUnit = unit;
    }

    /**
     * Returns the current position of the splitter, in
     * {@link #getSplitPositionUnit()} units.
     * 
     * @return position of the splitter
     */
    public float getSplitPosition() {
        return getSplitterState(false).position;
    }

    /**
     * Returns the unit of position of the splitter
     * 
     * @return unit of position of the splitter
     * @see #setSplitPosition(float, Unit)
     */
    public Unit getSplitPositionUnit() {
        return posUnit;
    }

    /**
     * Is the split position reversed. By default the split position is measured
     * by the first region, but if split position is reversed the measuring is
     * done by the second region instead.
     * 
     * @since 7.3.6
     * @return {@code true} if reversed, {@code false} otherwise.
     * @see #setSplitPosition(float, boolean)
     */
    public boolean isSplitPositionReversed() {
        return getSplitterState(false).positionReversed;
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
    public void setMinSplitPosition(float pos, Unit unit) {
        setSplitPositionLimits(pos, unit, getSplitterState(false).maxPosition,
                posMaxUnit);
    }

    /**
     * Returns the current minimum position of the splitter, in
     * {@link #getMinSplitPositionUnit()} units.
     * 
     * @return the minimum position of the splitter
     */
    public float getMinSplitPosition() {
        return getSplitterState(false).minPosition;
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
        setSplitPositionLimits(getSplitterState(false).minPosition, posMinUnit,
                pos, unit);
    }

    /**
     * Returns the current maximum position of the splitter, in
     * {@link #getMaxSplitPositionUnit()} units.
     * 
     * @return the maximum position of the splitter
     */
    public float getMaxSplitPosition() {
        return getSplitterState(false).maxPosition;
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

        state.minPosition = minPos;
        state.minPositionUnit = minPosUnit.getSymbol();
        posMinUnit = minPosUnit;

        state.maxPosition = maxPos;
        state.maxPositionUnit = maxPosUnit.getSymbol();
        posMaxUnit = maxPosUnit;
    }

    /**
     * Lock the SplitPanels position, disabling the user from dragging the split
     * handle.
     * 
     * @param locked
     *            Set <code>true</code> if locked, <code>false</code> otherwise.
     */
    public void setLocked(boolean locked) {
        getSplitterState().locked = locked;
    }

    /**
     * Is the SplitPanel handle locked (user not allowed to change split
     * position by dragging).
     * 
     * @return <code>true</code> if locked, <code>false</code> otherwise.
     */
    public boolean isLocked() {
        return getSplitterState(false).locked;
    }

    /**
     * <code>SplitterClickListener</code> interface for listening for
     * <code>SplitterClickEvent</code> fired by a <code>SplitPanel</code>.
     * 
     * @see SplitterClickEvent
     * @since 6.2
     */
    public interface SplitterClickListener extends ConnectorEventListener {

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

    public static class SplitterClickEvent extends ClickEvent {

        public SplitterClickEvent(Component source,
                MouseEventDetails mouseEventDetails) {
            super(source, mouseEventDetails);
        }

    }

    public void addSplitterClickListener(SplitterClickListener listener) {
        addListener(EventId.CLICK_EVENT_IDENTIFIER, SplitterClickEvent.class,
                listener, SplitterClickListener.clickMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addSplitterClickListener(SplitterClickListener)}
     **/
    @Deprecated
    public void addListener(SplitterClickListener listener) {
        addSplitterClickListener(listener);
    }

    public void removeSplitterClickListener(SplitterClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER,
                SplitterClickEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeSplitterClickListener(SplitterClickListener)}
     **/
    @Deprecated
    public void removeListener(SplitterClickListener listener) {
        removeSplitterClickListener(listener);
    }

    @Override
    protected AbstractSplitPanelState getState() {
        return (AbstractSplitPanelState) super.getState();
    }

    @Override
    protected AbstractSplitPanelState getState(boolean markAsDirty) {
        return (AbstractSplitPanelState) super.getState(markAsDirty);
    }

    private SplitterState getSplitterState() {
        return ((AbstractSplitPanelState) super.getState()).splitterState;
    }

    private SplitterState getSplitterState(boolean markAsDirty) {
        return ((AbstractSplitPanelState) super.getState(markAsDirty)).splitterState;
    }
}
