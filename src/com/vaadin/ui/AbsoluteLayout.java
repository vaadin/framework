/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout;

/**
 * AbsoluteLayout is a layout implementation that mimics html absolute
 * positioning.
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(VAbsoluteLayout.class)
public class AbsoluteLayout extends AbstractLayout {

    private static final String CLICK_EVENT = VAbsoluteLayout.CLICK_EVENT_IDENTIFIER;

    // The components in the layout
    private Collection<Component> components = new LinkedHashSet<Component>();

    // Maps each component to a position
    private Map<Component, ComponentPosition> componentToCoordinates = new HashMap<Component, ComponentPosition>();

    /**
     * Creates an AbsoluteLayout with full size.
     */
    public AbsoluteLayout() {
        setSizeFull();
    }

    /**
     * Gets an iterator for going through all components enclosed in the
     * absolute layout.
     */
    public Iterator<Component> getComponentIterator() {
        return components.iterator();
    }

    /**
     * Replaces one component with another one. The new component inherits the
     * old components position.
     */
    public void replaceComponent(Component oldComponent, Component newComponent) {
        ComponentPosition position = getPosition(oldComponent);
        removeComponent(oldComponent);
        addComponent(newComponent);
        componentToCoordinates.put(newComponent, position);
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
        components.add(c);
        super.addComponent(c);
        requestRepaint();
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
        components.remove(c);
        super.removeComponent(c);
        requestRepaint();
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
        addComponent(c);
        getPosition(c).setCSSString(cssPosition);
    }

    /**
     * Gets the position of a component in the layout. Returns null if component
     * is not attached to the layout.
     * 
     * @param component
     *            The component which position is needed
     * @return An instance of ComponentPosition containing the position of the
     *         component, or null if the component is not enclosed in the
     *         layout.
     */
    public ComponentPosition getPosition(Component component) {
        if (componentToCoordinates.containsKey(component)) {
            return componentToCoordinates.get(component);
        } else {
            ComponentPosition coords = new ComponentPosition();
            componentToCoordinates.put(component, coords);
            return coords;
        }
    }

    /**
     * The CompontPosition class represents a components position within the
     * absolute layout. It contains the CSS attributes for left, right, top and
     * bottom and the units used to specify them. *
     * 
     * TODO symmetric getters and setters for fields to make this simpler to use
     * in generic java tools
     * 
     */
    public class ComponentPosition implements Serializable {

        private int zIndex = -1;
        private Float topValue = null;
        private Float rightValue = null;
        private Float bottomValue = null;
        private Float leftValue = null;

        private int topUnits;
        private int rightUnits;
        private int bottomUnits;
        private int leftUnits;

        /**
         * Sets the position attributes using CSS syntax. Example usage:
         * 
         * <code><pre>
         * setCSSString("top:10px;left:20%;z-index:16;");
         * </pre></code>
         * 
         * @param css
         */
        public void setCSSString(String css) {
            String[] cssProperties = css.split(";");
            for (int i = 0; i < cssProperties.length; i++) {
                String[] keyValuePair = cssProperties[i].split(":");
                String key = keyValuePair[0].trim();
                if (key.equals("")) {
                    continue;
                }
                if (key.equals("z-index")) {
                    zIndex = Integer.parseInt(keyValuePair[1]);
                } else {
                    String value;
                    if (keyValuePair.length > 1) {
                        value = keyValuePair[1].trim();
                    } else {
                        value = "";
                    }
                    String unit = value.replaceAll("[0-9\\.\\-]+", "");
                    if (!unit.equals("")) {
                        value = value.substring(0, value.indexOf(unit)).trim();
                    }
                    float v = Float.parseFloat(value);
                    int unitInt = parseCssUnit(unit);
                    if (key.equals("top")) {
                        topValue = v;
                        topUnits = unitInt;
                    } else if (key.equals("right")) {
                        rightValue = v;
                        rightUnits = unitInt;
                    } else if (key.equals("bottom")) {
                        bottomValue = v;
                        bottomUnits = unitInt;
                    } else if (key.equals("left")) {
                        leftValue = v;
                        leftUnits = unitInt;
                    }
                }
            }
            requestRepaint();
        }

        /**
         * Parses a string and checks if a unit is found. If a unit is not found
         * from the string the unit pixels is used.
         * 
         * @param string
         *            The string to parse the unit from
         * @return The found unit
         */
        private int parseCssUnit(String string) {
            for (int i = 0; i < UNIT_SYMBOLS.length; i++) {
                if (UNIT_SYMBOLS[i].equals(string)) {
                    return i;
                }
            }
            return 0; // defaults to px (eg. top:0;)
        }

        /**
         * Converts the internal values into a valid CSS string.
         * 
         * @return A valid CSS string
         */
        public String getCSSString() {
            String s = "";
            if (topValue != null) {
                s += "top:" + topValue + UNIT_SYMBOLS[topUnits] + ";";
            }
            if (rightValue != null) {
                s += "right:" + rightValue + UNIT_SYMBOLS[rightUnits] + ";";
            }
            if (bottomValue != null) {
                s += "bottom:" + bottomValue + UNIT_SYMBOLS[bottomUnits] + ";";
            }
            if (leftValue != null) {
                s += "left:" + leftValue + UNIT_SYMBOLS[leftUnits] + ";";
            }
            if (zIndex >= 0) {
                s += "z-index:" + zIndex + ";";
            }
            return s;
        }

        /**
         * Sets the 'top' CSS-attribute
         * 
         * @param topValue
         *            The value of the 'top' attribute
         * @param topUnits
         *            The unit of the 'top' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setTop(float topValue, int topUnits) {
            validateLength(topValue, topUnits);
            this.topValue = topValue;
            this.topUnits = topUnits;
            requestRepaint();
        }

        /**
         * Sets the 'right' CSS-attribute
         * 
         * @param rightValue
         *            The value of the 'right' attribute
         * @param rightUnits
         *            The unit of the 'right' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setRight(float rightValue, int rightUnits) {
            validateLength(rightValue, rightUnits);
            this.rightValue = rightValue;
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        /**
         * Sets the 'bottom' CSS-attribute
         * 
         * @param bottomValue
         *            The value of the 'bottom' attribute
         * @param units
         *            The unit of the 'bottom' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setBottom(float bottomValue, int units) {
            validateLength(bottomValue, units);
            this.bottomValue = bottomValue;
            bottomUnits = units;
            requestRepaint();
        }

        /**
         * Sets the 'left' CSS-attribute
         * 
         * @param leftValue
         *            The value of the 'left' attribute
         * @param units
         *            The unit of the 'left' attribute. See UNIT_SYMBOLS for a
         *            description of the available units.
         */
        public void setLeft(float leftValue, int units) {
            validateLength(leftValue, units);
            this.leftValue = leftValue;
            leftUnits = units;
            requestRepaint();
        }

        /**
         * Sets the 'z-index' CSS-attribute
         * 
         * @param zIndex
         *            The z-index for the component.
         */
        public void setZIndex(int zIndex) {
            this.zIndex = zIndex;
            requestRepaint();
        }

        /**
         * Sets the value of the 'top' CSS-attribute
         * 
         * @param topValue
         *            The value of the 'left' attribute
         */
        public void setTopValue(float topValue) {
            validateLength(topValue, topUnits);
            this.topValue = topValue;
            requestRepaint();
        }

        /**
         * Gets the 'top' CSS-attributes value in specified units.
         * 
         * @return The value of the 'top' CSS-attribute
         */
        public float getTopValue() {
            return topValue == null ? 0 : rightValue.floatValue();
        }

        /**
         * Gets the 'right' CSS-attributes value in specified units.
         * 
         * @return The value of the 'right' CSS-attribute
         */
        public float getRightValue() {
            return rightValue == null ? 0 : rightValue.floatValue();
        }

        /**
         * Sets the 'right' CSS-attributes value in specified units.
         * 
         * @param rightValue
         *            The value of the 'right' CSS-attribute
         */
        public void setRightValue(float rightValue) {
            validateLength(rightValue, rightUnits);
            this.rightValue = rightValue;
            requestRepaint();
        }

        /**
         * Gets the 'bottom' CSS-attributes value in specified units.
         * 
         * @return The value of the 'bottom' CSS-attribute
         */
        public float getBottomValue() {
            return bottomValue == null ? 0 : bottomValue.floatValue();
        }

        /**
         * Sets the 'bottom' CSS-attributes value in specified units.
         * 
         * @param bottomValue
         *            The value of the 'bottom' CSS-attribute
         */
        public void setBottomValue(float bottomValue) {
            validateLength(bottomValue, bottomUnits);
            this.bottomValue = bottomValue;
            requestRepaint();
        }

        /**
         * Gets the 'left' CSS-attributes value in specified units.
         * 
         * @return The value of the 'left' CSS-attribute
         */
        public float getLeftValue() {
            return leftValue == null ? 0 : leftValue.floatValue();
        }

        /**
         * Sets the 'left' CSS-attributes value in specified units.
         * 
         * @param leftValue
         *            The value of the 'left' CSS-attribute
         */
        public void setLeftValue(float leftValue) {
            validateLength(leftValue, leftUnits);
            this.leftValue = leftValue;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'top' CSS-attribute
         * 
         * @return See UNIT_SYMBOLS for a description of the available units.
         */
        public int getTopUnits() {
            return topUnits;
        }

        /**
         * Sets the unit for the 'top' CSS-attribute
         * 
         * @param topUnits
         *            See UNIT_SYMBOLS for a description of the available units.
         */
        public void setTopUnits(int topUnits) {
            validateLength(topValue, topUnits);
            this.topUnits = topUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'right' CSS-attribute
         * 
         * @return See UNIT_SYMBOLS for a description of the available units.
         */
        public int getRightUnits() {
            return rightUnits;
        }

        /**
         * Sets the unit for the 'right' CSS-attribute
         * 
         * @param rightUnits
         *            See UNIT_SYMBOLS for a description of the available units.
         */
        public void setRightUnits(int rightUnits) {
            validateLength(rightValue, rightUnits);
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'bottom' CSS-attribute
         * 
         * @return See UNIT_SYMBOLS for a description of the available units.
         */
        public int getBottomUnits() {
            return bottomUnits;
        }

        /**
         * Sets the unit for the 'bottom' CSS-attribute
         * 
         * @param bottomUnits
         *            See UNIT_SYMBOLS for a description of the available units.
         */
        public void setBottomUnits(int bottomUnits) {
            validateLength(bottomValue, bottomUnits);
            this.bottomUnits = bottomUnits;
            requestRepaint();
        }

        /**
         * Gets the unit for the 'left' CSS-attribute
         * 
         * @return See UNIT_SYMBOLS for a description of the available units.
         */
        public int getLeftUnits() {
            return leftUnits;
        }

        /**
         * Sets the unit for the 'left' CSS-attribute
         * 
         * @param leftUnits
         *            See UNIT_SYMBOLS for a description of the available units.
         */
        public void setLeftUnits(int leftUnits) {
            validateLength(leftValue, leftUnits);
            this.leftUnits = leftUnits;
            requestRepaint();
        }

        /**
         * Gets the 'z-index' CSS-attribute.
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractLayout#paintContent(com.vaadin.terminal.PaintTarget
     * )
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (Component component : components) {
            target.startTag("cc");
            target.addAttribute("css", getPosition(component).getCSSString());
            component.paint(target);
            target.endTag("cc");
        }
    }

    /**
     * Validates a value with the unit
     * 
     * @param topValue
     *            The value to validate
     * @param topUnits2
     *            The unit to validate
     */
    private static void validateLength(float topValue, int topUnits2) {
        // TODO throw on invalid value

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey(CLICK_EVENT)) {
            fireClick((Map<String, Object>) variables.get(CLICK_EVENT));
        }

    }

    /**
     * Fires a click event when the layout is clicked
     * 
     * @param parameters
     *            The parameters recieved from the client side implementation
     */
    private void fireClick(Map<String, Object> parameters) {
        MouseEventDetails mouseDetails = MouseEventDetails
                .deSerialize((String) parameters.get("mouseDetails"));
        Component childComponent = (Component) parameters.get("component");

        fireEvent(new LayoutClickEvent(this, mouseDetails, childComponent));
    }

    /**
     * Add a click listener to the layout. The listener is called whenever the
     * user clicks inside the layout. Also when the click targets a component
     * inside the Panel, provided the targeted component does not prevent the
     * click event from propagating.
     * 
     * The child component that was clicked is included in the
     * {@link LayoutClickEvent}.
     * 
     * Use {@link #removeListener(LayoutClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addListener(LayoutClickListener listener) {
        addListener(CLICK_EVENT, LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the layout. The listener should earlier have
     * been added using {@link #addListener(LayoutClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeListener(LayoutClickListener listener) {
        removeListener(CLICK_EVENT, LayoutClickEvent.class, listener);
    }

}
