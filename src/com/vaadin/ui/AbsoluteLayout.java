/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.AbsoluteLayoutConnector.AbsoluteLayoutServerRPC;
import com.vaadin.terminal.gwt.client.ui.AbsoluteLayoutConnector.AbsoluteLayoutState;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;

/**
 * AbsoluteLayout is a layout implementation that mimics html absolute
 * positioning.
 * 
 */
@SuppressWarnings("serial")
public class AbsoluteLayout extends AbstractLayout implements
        LayoutClickNotifier {

    private AbsoluteLayoutServerRPC rpc = new AbsoluteLayoutServerRPC() {

        public void layoutClick(MouseEventDetails mouseDetails,
                Connector clickedConnector) {
            fireEvent(LayoutClickEvent.createEvent(AbsoluteLayout.this,
                    mouseDetails, clickedConnector));
        }
    };
    // Maps each component to a position
    private LinkedHashMap<Component, ComponentPosition> componentToCoordinates = new LinkedHashMap<Component, ComponentPosition>();

    /**
     * Creates an AbsoluteLayout with full size.
     */
    public AbsoluteLayout() {
        registerRpc(rpc);
        setSizeFull();
    }

    @Override
    public AbsoluteLayoutState getState() {
        return (AbsoluteLayoutState) super.getState();
    }

    /**
     * Gets an iterator for going through all components enclosed in the
     * absolute layout.
     */
    public Iterator<Component> getComponentIterator() {
        return componentToCoordinates.keySet().iterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components
     */
    public int getComponentCount() {
        return componentToCoordinates.size();
    }

    /**
     * Replaces one component with another one. The new component inherits the
     * old components position.
     */
    public void replaceComponent(Component oldComponent, Component newComponent) {
        ComponentPosition position = getPosition(oldComponent);
        removeComponent(oldComponent);
        addComponent(newComponent, position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponentContainer#addComponent(com.vaadin.ui.Component
     * )
     */
    @Override
    public void addComponent(Component c) {
        addComponent(c, new ComponentPosition());
    }

    /**
     * Adds a component to the layout. The component can be positioned by
     * providing a string formatted in CSS-format.
     * <p>
     * For example the string "top:10px;left:10px" will position the component
     * 10 pixels from the left and 10 pixels from the top. The identifiers:
     * "top","left","right" and "bottom" can be used to specify the position.
     * </p>
     * 
     * @param c
     *            The component to add to the layout
     * @param cssPosition
     *            The css position string
     */
    public void addComponent(Component c, String cssPosition) {
        ComponentPosition position = new ComponentPosition();
        position.setCSSString(cssPosition);
        addComponent(c, position);
    }

    /**
     * Adds the component using the given position. Ensures the position is only
     * set if the component is added correctly.
     * 
     * @param c
     *            The component to add
     * @param position
     *            The position info for the component. Must not be null.
     * @throws IllegalArgumentException
     *             If adding the component failed
     */
    private void addComponent(Component c, ComponentPosition position)
            throws IllegalArgumentException {
        /*
         * Create position instance and add it to componentToCoordinates map. We
         * need to do this before we call addComponent so the attachListeners
         * can access this position. #6368
         */
        internalSetPosition(c, position);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            internalRemoveComponent(c);
            throw e;
        }
        requestRepaint();
    }

    /**
     * Removes the component from all internal data structures. Does not
     * actually remove the component from the layout (this is assumed to have
     * been done by the caller).
     * 
     * @param c
     *            The component to remove
     */
    private void internalRemoveComponent(Component c) {
        componentToCoordinates.remove(c);
    }

    @Override
    public void updateState() {
        super.updateState();

        // This could be in internalRemoveComponent and internalSetComponent if
        // Map<Connector,String> was supported. We cannot get the child
        // connectorId unless the component is attached to the application so
        // the String->String map cannot be populated in internal* either.
        Map<String, String> connectorToPosition = new HashMap<String, String>();
        for (Component c : this) {
            connectorToPosition.put(c.getConnectorId(), getPosition(c)
                    .getCSSString());
        }
        getState().setConnectorToCssPosition(connectorToPosition);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponentContainer#removeComponent(com.vaadin.ui
     * .Component)
     */
    @Override
    public void removeComponent(Component c) {
        internalRemoveComponent(c);
        super.removeComponent(c);
        requestRepaint();
    }

    /**
     * Gets the position of a component in the layout. Returns null if component
     * is not attached to the layout.
     * <p>
     * Note that you cannot update the position by updating this object. Call
     * {@link #setPosition(Component, ComponentPosition)} with the updated
     * {@link ComponentPosition} object.
     * </p>
     * 
     * @param component
     *            The component which position is needed
     * @return An instance of ComponentPosition containing the position of the
     *         component, or null if the component is not enclosed in the
     *         layout.
     */
    public ComponentPosition getPosition(Component component) {
        return componentToCoordinates.get(component);
    }

    /**
     * Sets the position of a component in the layout.
     * 
     * @param component
     * @param position
     */
    public void setPosition(Component component, ComponentPosition position) {
        if (!componentToCoordinates.containsKey(component)) {
            throw new IllegalArgumentException(
                    "Component must be a child of this layout");
        }
        internalSetPosition(component, position);
    }

    /**
     * Updates the position for a component. Caller must ensure component is a
     * child of this layout.
     * 
     * @param component
     *            The component. Must be a child for this layout. Not enforced.
     * @param position
     *            New position. Must not be null.
     */
    private void internalSetPosition(Component component,
            ComponentPosition position) {
        componentToCoordinates.put(component, position);
        requestRepaint();
    }

    /**
     * The CompontPosition class represents a components position within the
     * absolute layout. It contains the attributes for left, right, top and
     * bottom and the units used to specify them.
     */
    public class ComponentPosition implements Serializable {

        private int zIndex = -1;
        private Float topValue = null;
        private Float rightValue = null;
        private Float bottomValue = null;
        private Float leftValue = null;

        private Unit topUnits = Unit.PIXELS;
        private Unit rightUnits = Unit.PIXELS;
        private Unit bottomUnits = Unit.PIXELS;
        private Unit leftUnits = Unit.PIXELS;

        /**
         * Sets the position attributes using CSS syntax. Attributes not
         * included in the string are reset to their unset states.
         * 
         * <code><pre>
         * setCSSString("top:10px;left:20%;z-index:16;");
         * </pre></code>
         * 
         * @param css
         */
        public void setCSSString(String css) {
            topValue = rightValue = bottomValue = leftValue = null;
            topUnits = rightUnits = bottomUnits = leftUnits = Unit.PIXELS;
            zIndex = -1;
            if (css == null) {
                return;
            }

            String[] cssProperties = css.split(";");
            for (int i = 0; i < cssProperties.length; i++) {
                String[] keyValuePair = cssProperties[i].split(":");
                String key = keyValuePair[0].trim();
                if (key.equals("")) {
                    continue;
                }
                if (key.equals("z-index")) {
                    zIndex = Integer.parseInt(keyValuePair[1].trim());
                } else {
                    String value;
                    if (keyValuePair.length > 1) {
                        value = keyValuePair[1].trim();
                    } else {
                        value = "";
                    }
                    String symbol = value.replaceAll("[0-9\\.\\-]+", "");
                    if (!symbol.equals("")) {
                        value = value.substring(0, value.indexOf(symbol))
                                .trim();
                    }
                    float v = Float.parseFloat(value);
                    Unit unit = Unit.getUnitFromSymbol(symbol);
                    if (key.equals("top")) {
                        topValue = v;
                        topUnits = unit;
                    } else if (key.equals("right")) {
                        rightValue = v;
                        rightUnits = unit;
                    } else if (key.equals("bottom")) {
                        bottomValue = v;
                        bottomUnits = unit;
                    } else if (key.equals("left")) {
                        leftValue = v;
                        leftUnits = unit;
                    }
                }
            }
            requestRepaint();
        }

        /**
         * Converts the internal values into a valid CSS string.
         * 
         * @return A valid CSS string
         */
        public String getCSSString() {
            String s = "";
            if (topValue != null) {
                s += "top:" + topValue + topUnits.getSymbol() + ";";
            }
            if (rightValue != null) {
                s += "right:" + rightValue + rightUnits.getSymbol() + ";";
            }
            if (bottomValue != null) {
                s += "bottom:" + bottomValue + bottomUnits.getSymbol() + ";";
            }
            if (leftValue != null) {
                s += "left:" + leftValue + leftUnits.getSymbol() + ";";
            }
            if (zIndex >= 0) {
                s += "z-index:" + zIndex + ";";
            }
            return s;
        }

        /**
         * Sets the 'top' attribute; distance from the top of the component to
         * the top edge of the layout.
         * 
         * @param topValue
         *            The value of the 'top' attribute
         * @param topUnits
         *            The unit of the 'top' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setTop(Float topValue, Unit topUnits) {
            this.topValue = topValue;
            this.topUnits = topUnits;
            requestRepaint();
        }

        /**
         * Sets the 'right' attribute; distance from the right of the component
         * to the right edge of the layout.
         * 
         * @param rightValue
         *            The value of the 'right' attribute
         * @param rightUnits
         *            The unit of the 'right' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setRight(Float rightValue, Unit rightUnits) {
            this.rightValue = rightValue;
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        /**
         * Sets the 'bottom' attribute; distance from the bottom of the
         * component to the bottom edge of the layout.
         * 
         * @param bottomValue
         *            The value of the 'bottom' attribute
         * @param units
         *            The unit of the 'bottom' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setBottom(Float bottomValue, Unit bottomUnits) {
            this.bottomValue = bottomValue;
            this.bottomUnits = bottomUnits;
            requestRepaint();
        }

        /**
         * Sets the 'left' attribute; distance from the left of the component to
         * the left edge of the layout.
         * 
         * @param leftValue
         *            The value of the 'left' attribute
         * @param units
         *            The unit of the 'left' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setLeft(Float leftValue, Unit leftUnits) {
            this.leftValue = leftValue;
            this.leftUnits = leftUnits;
            requestRepaint();
        }

        /**
         * Sets the 'z-index' attribute; the visual stacking order
         * 
         * @param zIndex
         *            The z-index for the component.
         */
        public void setZIndex(int zIndex) {
            this.zIndex = zIndex;
            requestRepaint();
        }

        /**
         * Sets the value of the 'top' attribute; distance from the top of the
         * component to the top edge of the layout.
         * 
         * @param topValue
         *            The value of the 'left' attribute
         */
        public void setTopValue(Float topValue) {
            this.topValue = topValue;
            requestRepaint();
        }

        /**
         * Gets the 'top' attributes value in current units.
         * 
         * @see #getTopUnits()
         * @return The value of the 'top' attribute, null if not set
         */
        public Float getTopValue() {
            return topValue;
        }

        /**
         * Gets the 'right' attributes value in current units.
         * 
         * @return The value of the 'right' attribute, null if not set
         * @see #getRightUnits()
         */
        public Float getRightValue() {
            return rightValue;
        }

        /**
         * Sets the 'right' attribute value (distance from the right of the
         * component to the right edge of the layout). Currently active units
         * are maintained.
         * 
         * @param rightValue
         *            The value of the 'right' attribute
         * @see #setRightUnits(int)
         */
        public void setRightValue(Float rightValue) {
            this.rightValue = rightValue;
            requestRepaint();
        }

        /**
         * Gets the 'bottom' attributes value using current units.
         * 
         * @return The value of the 'bottom' attribute, null if not set
         * @see #getBottomUnits()
         */
        public Float getBottomValue() {
            return bottomValue;
        }

        /**
         * Sets the 'bottom' attribute value (distance from the bottom of the
         * component to the bottom edge of the layout). Currently active units
         * are maintained.
         * 
         * @param bottomValue
         *            The value of the 'bottom' attribute
         * @see #setBottomUnits(int)
         */
        public void setBottomValue(Float bottomValue) {
            this.bottomValue = bottomValue;
            requestRepaint();
        }

        /**
         * Gets the 'left' attributes value using current units.
         * 
         * @return The value of the 'left' attribute, null if not set
         * @see #getLeftUnits()
         */
        public Float getLeftValue() {
            return leftValue;
        }

        /**
         * Sets the 'left' attribute value (distance from the left of the
         * component to the left edge of the layout). Currently active units are
         * maintained.
         * 
         * @param leftValue
         *            The value of the 'left' CSS-attribute
         * @see #setLeftUnits(int)
         */
        public void setLeftValue(Float leftValue) {
            this.leftValue = leftValue;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'top' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public Unit getTopUnits() {
            return topUnits;
        }

        /**
         * Sets the unit for the 'top' attribute
         * 
         * @param topUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setTopUnits(Unit topUnits) {
            this.topUnits = topUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'right' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public Unit getRightUnits() {
            return rightUnits;
        }

        /**
         * Sets the unit for the 'right' attribute
         * 
         * @param rightUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setRightUnits(Unit rightUnits) {
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'bottom' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public Unit getBottomUnits() {
            return bottomUnits;
        }

        /**
         * Sets the unit for the 'bottom' attribute
         * 
         * @param bottomUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setBottomUnits(Unit bottomUnits) {
            this.bottomUnits = bottomUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'left' attribute
         * 
         * @return See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *         available units.
         */
        public Unit getLeftUnits() {
            return leftUnits;
        }

        /**
         * Sets the unit for the 'left' attribute
         * 
         * @param leftUnits
         *            See {@link Sizeable} UNIT_SYMBOLS for a description of the
         *            available units.
         */
        public void setLeftUnits(Unit leftUnits) {
            this.leftUnits = leftUnits;
            requestRepaint();
        }

        /**
         * Gets the 'z-index' attribute.
         * 
         * @return the zIndex The z-index attribute
         */
        public int getZIndex() {
            return zIndex;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return getCSSString();
        }

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

}
