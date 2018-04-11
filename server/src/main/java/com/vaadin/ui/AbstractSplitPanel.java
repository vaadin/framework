/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.Collection;
import java.util.Iterator;

import org.jsoup.nodes.Element;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.HasUserOriginated;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.server.SizeWithUnit;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelRpc;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState.SplitterState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
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

    private final AbstractSplitPanelRpc rpc = new AbstractSplitPanelRpc() {

        @Override
        public void splitterClick(MouseEventDetails mouseDetails) {
            fireEvent(new SplitterClickEvent(AbstractSplitPanel.this,
                    mouseDetails));
        }

        @Override
        public void setSplitterPosition(float position) {
            float oldPosition = getSplitPosition();

            getSplitterState().position = position;

            fireEvent(new SplitPositionChangeEvent(AbstractSplitPanel.this,
                    true, oldPosition, getSplitPositionUnit(), position,
                    getSplitPositionUnit()));
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
    private class ComponentIterator
            implements Iterator<Component>, Serializable {

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
     * the second component is shown at the bottom or to the right.
     *
     * @param c
     *            The component to use as second component
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
    }

    /**
     * Gets an iterator to the collection of contained components. Using this
     * iterator it is possible to step through all components contained in this
     * container and remove components from it.
     *
     * @return the component iterator.
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
    public void replaceComponent(Component oldComponent,
            Component newComponent) {
        if (oldComponent == getFirstComponent()) {
            setFirstComponent(newComponent);
        } else if (oldComponent == getSecondComponent()) {
            setSecondComponent(newComponent);
        }
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
        float oldPosition = getSplitPosition();
        Unit oldUnit = getSplitPositionUnit();

        SplitterState splitterState = getSplitterState();
        splitterState.position = pos;
        splitterState.positionUnit = unit.getSymbol();
        splitterState.positionReversed = reverse;
        posUnit = unit;
        fireEvent(new SplitPositionChangeEvent(AbstractSplitPanel.this, false,
                oldPosition, oldUnit, pos, posUnit));
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
     * Returns the unit of position of the splitter.
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
     * Returns the unit of the maximum position of the splitter.
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
                || (maxPosUnit != Unit.PERCENTAGE
                        && maxPosUnit != Unit.PIXELS)) {
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
    @FunctionalInterface
    public interface SplitterClickListener extends ConnectorEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                SplitterClickListener.class, "splitterClick",
                SplitterClickEvent.class);

        /**
         * SplitPanel splitter has been clicked.
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

    /**
     * Interface for listening for {@link SplitPositionChangeEvent}s fired by a
     * SplitPanel.
     *
     * @since 7.5.0
     */
    @FunctionalInterface
    public interface SplitPositionChangeListener
            extends ConnectorEventListener {

        public static final Method moveMethod = ReflectTools.findMethod(
                SplitPositionChangeListener.class, "onSplitPositionChanged",
                SplitPositionChangeEvent.class);

        /**
         * SplitPanel splitter position has been changed.
         *
         * @param event
         *            SplitPositionChangeEvent event.
         */
        public void onSplitPositionChanged(SplitPositionChangeEvent event);
    }

    /**
     * Event that indicates a change in SplitPanel's splitter position.
     *
     * @since 7.5.0
     */
    public static class SplitPositionChangeEvent extends Component.Event
            implements HasUserOriginated {

        private final float oldPosition;
        private final Unit oldUnit;

        private final float position;
        private final Unit unit;

        private final boolean userOriginated;

        /**
         * Creates a split position change event.
         *
         * @param source
         *            split panel from which the event originates
         * @param userOriginated
         *            true if the event is directly based on user actions
         * @param oldPosition
         *            old split position
         * @param oldUnit
         *            old unit of split position
         * @param position
         *            new split position
         * @param unit
         *            new split position unit
         * @since 8.1
         */
        public SplitPositionChangeEvent(final Component source,
                final boolean userOriginated, final float oldPosition,
                final Unit oldUnit, final float position, final Unit unit) {
            super(source);
            this.userOriginated = userOriginated;
            this.oldUnit = oldUnit;
            this.oldPosition = oldPosition;
            this.position = position;
            this.unit = unit;
        }

        /**
         * Returns the new split position that triggered this change event.
         *
         * @return the new value of split position
         */
        public float getSplitPosition() {
            return position;
        }

        /**
         * Returns the new split position unit that triggered this change event.
         *
         * @return the new value of split position
         */
        public Unit getSplitPositionUnit() {
            return unit;
        }

        /**
         * Returns the position of the split before this change event occurred.
         *
         * @since 8.1
         *
         * @return the split position previously set to the source of this event
         */
        public float getOldSplitPosition() {
            return oldPosition;
        }

        /**
         * Returns the position unit of the split before this change event
         * occurred.
         *
         * @since 8.1
         *
         * @return the split position unit previously set to the source of this
         *         event
         */
        public Unit getOldSplitPositionUnit() {
            return oldUnit;
        }

        /**
         * {@inheritDoc}
         *
         * @since 8.1
         */
        @Override
        public boolean isUserOriginated() {
            return userOriginated;
        }
    }

    public Registration addSplitterClickListener(
            SplitterClickListener listener) {
        return addListener(EventId.CLICK_EVENT_IDENTIFIER,
                SplitterClickEvent.class, listener,
                SplitterClickListener.clickMethod);
    }

    @Deprecated
    public void removeSplitterClickListener(SplitterClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, SplitterClickEvent.class,
                listener);
    }

    /**
     * Register a listener to handle {@link SplitPositionChangeEvent}s.
     *
     * @since 8.0
     * @param listener
     *            {@link SplitPositionChangeListener} to be registered.
     */
    public Registration addSplitPositionChangeListener(
            SplitPositionChangeListener listener) {
        return addListener(SplitPositionChangeEvent.class, listener,
                SplitPositionChangeListener.moveMethod);
    }

    /**
     * Removes a {@link SplitPositionChangeListener}.
     *
     * @since 7.5.0
     * @param listener
     *            SplitPositionChangeListener to be removed.
     */
    @Deprecated
    public void removeSplitPositionChangeListener(
            SplitPositionChangeListener listener) {
        removeListener(SplitPositionChangeEvent.class, listener);
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
        return ((AbstractSplitPanelState) super.getState(
                markAsDirty)).splitterState;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#readDesign(org.jsoup.nodes .Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        // handle default attributes
        super.readDesign(design, designContext);
        // handle custom attributes, use default values if no explicit value
        // set
        // There is no setter for reversed, so it will be handled using
        // setSplitPosition.
        boolean reversed = false;
        if (design.hasAttr("reversed")) {
            reversed = DesignAttributeHandler.readAttribute("reversed",
                    design.attributes(), Boolean.class);
            setSplitPosition(getSplitPosition(), reversed);
        }
        if (design.hasAttr("split-position")) {
            SizeWithUnit splitPosition = SizeWithUnit.parseStringSize(
                    design.attr("split-position"), Unit.PERCENTAGE);
            setSplitPosition(splitPosition.getSize(), splitPosition.getUnit(),
                    reversed);
        }
        if (design.hasAttr("min-split-position")) {
            SizeWithUnit minSplitPosition = SizeWithUnit.parseStringSize(
                    design.attr("min-split-position"), Unit.PERCENTAGE);
            setMinSplitPosition(minSplitPosition.getSize(),
                    minSplitPosition.getUnit());
        }
        if (design.hasAttr("max-split-position")) {
            SizeWithUnit maxSplitPosition = SizeWithUnit.parseStringSize(
                    design.attr("max-split-position"), Unit.PERCENTAGE);
            setMaxSplitPosition(maxSplitPosition.getSize(),
                    maxSplitPosition.getUnit());
        }
        // handle children
        if (design.children().size() > 2) {
            throw new DesignException(
                    "A split panel can contain at most two components.");
        }
        for (Element childElement : design.children()) {
            Component childComponent = designContext.readDesign(childElement);
            if (childElement.hasAttr(":second")) {
                setSecondComponent(childComponent);
            } else {
                addComponent(childComponent);
            }
        }
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        // the setters of the properties do not accept strings such as "20px"
        attributes.add("split-position");
        attributes.add("min-split-position");
        attributes.add("max-split-position");
        // no explicit setter for reversed
        attributes.add("reversed");
        return attributes;
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        // handle default attributes (also clears children and attributes)
        super.writeDesign(design, designContext);
        // handle custom attributes (write only if a value is not the
        // default value)
        AbstractSplitPanel def = designContext.getDefaultInstance(this);
        if (getSplitPosition() != def.getSplitPosition()
                || !def.getSplitPositionUnit().equals(getSplitPositionUnit())) {
            String splitPositionString = asString(getSplitPosition())
                    + getSplitPositionUnit();
            design.attr("split-position", splitPositionString);
        }
        if (getMinSplitPosition() != def.getMinSplitPosition() || !def
                .getMinSplitPositionUnit().equals(getMinSplitPositionUnit())) {
            design.attr("min-split-position", asString(getMinSplitPosition())
                    + getMinSplitPositionUnit());
        }
        if (getMaxSplitPosition() != def.getMaxSplitPosition() || !def
                .getMaxSplitPositionUnit().equals(getMaxSplitPositionUnit())) {
            design.attr("max-split-position", asString(getMaxSplitPosition())
                    + getMaxSplitPositionUnit());
        }
        if (getSplitterState().positionReversed) {
            design.attr("reversed", true);
        }
        // handle child components
        if (!designContext.shouldWriteChildren(this, def)) {
            return;
        }
        Component firstComponent = getFirstComponent();
        Component secondComponent = getSecondComponent();
        if (firstComponent != null) {
            Element childElement = designContext.createElement(firstComponent);
            design.appendChild(childElement);
        }
        if (secondComponent != null) {
            Element childElement = designContext.createElement(secondComponent);
            if (firstComponent == null) {
                childElement.attr(":second", true);
            }
            design.appendChild(childElement);
        }
    }

    private String asString(float number) {
        int truncated = (int) number;
        if (truncated == number) {
            return "" + truncated;
        }
        return "" + number;
    }
}
