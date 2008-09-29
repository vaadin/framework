package com.itmill.toolkit.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * 
 * A layout that enables absolute positioning for its children.
 * 
 * 
 */
public class CoordinateLayout extends AbstractLayout {

    protected final ArrayList<Component> componentList;
    protected final HashMap<Component, Coordinates> componentToCoord;

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int WIDTH = 2;
    public static final int HEIGHT = 3;
    public static final int RIGHT = 4;
    public static final int BOTTOM = 5;

    /**
     * Creates an empty coordinatelayout. The coordinateLayout is full size by
     * default.
     */
    public CoordinateLayout() {
        super();
        componentList = new ArrayList<Component>();
        componentToCoord = new HashMap<Component, Coordinates>();

        this.setSizeFull();
    }

    /**
     * 
     * <p>
     * Adds a component to the layout at the specified coordinates. The values
     * are treated as pixel values by default and for example
     * {@link com.itmill.toolkit.ui.CoordinateLayout.Coordinates#setUnitPercent(int, boolean)}
     * can be used to set the values as percentage.
     * </p>
     * 
     * <p>
     * If the value is negative, it is interpreted as automatic (terminal
     * decides). Null argument is not permitted and will throw an
     * IllegalArgumentException.
     * </p>
     * <dl>
     * <dt><b>Examples:</b></dt>
     * 
     * <dd>
     * <code>addComponent(c, 0, 0, -1, -1, -1, -1)</code> to attach to
     * upper-left corner and let terminal decide size</dd>
     * 
     * <dd><code>Coordinates c = addComponent(c, 5, 5, 200, 200, -1, -1)</dd>
     * <dd>c.setUnitPercent(CoordinateLayout.LEFT, true)</dd>
     * <dd>c.setUnitPercent(CoordinateLayout.TOP, true) </code> to attach 5%
     * from upper-left corner and define size to 200 x 200 pixels</dd>
     * 
     * <dd>
     * <code>addComponent(c, 0, 0, -1, -1, 0, 0)</code> to stretch component to
     * cover layout area (attached to all four corners)</dd>
     * </dl>
     * 
     * 
     * @param c
     *            component to be added
     * @param left
     *            distance of component left from layout left
     * @param top
     *            distance of component top from layout top
     * @param width
     *            component width
     * @param height
     *            component height
     * @param right
     *            distance of component right from layout right
     * @param bottom
     *            distance of component bottom from layout bottom
     * @return the coordinates object for the component
     * 
     * @throws IllegalArgumentException
     */
    public Coordinates addComponent(Component c, int left, int top, int width,
            int height, int right, int bottom) throws IllegalArgumentException {
        if (c == null) {
            throw new IllegalArgumentException();
        }

        Coordinates newCoords = new Coordinates(left, top, width, height,
                right, bottom);
        componentToCoord.put(c, newCoords);
        addComponent(c);

        return newCoords;
    }

    /**
     * <p>
     * Add a component to the specified coordinates. The string format is
     * <i>"left[%], top[%] [,width[%], height[%] [,right[%], bottom[%]]]"</i>.
     * Null arguments will throw an IllegalArgumentException.
     * </p>
     * 
     * <dl>
     * <dt><b>Examples for string format:</b></dt>
     * 
     * <dd>
     * <code>addComponent(c, "0, 0")</code> to attach to upper-left corner and
     * let terminal decide size</dd>
     * 
     * <dd>
     * <code>addComponent(c, "5%, 5%, 200, 200")</code> to attach 5% from
     * upper-left corner and define size to 200 x 200 pixels</dd>
     * 
     * <dd>
     * <code>addComponent(c,"0, 0, -1, -1,0, 0")</code> to stretch component to
     * cover coordinateLayout area (attached to all four corners)</dd>
     * 
     * </dl>
     * 
     * @param c
     *            the component to be added
     * @param coordString
     *            coordinates in the specified string format
     * @return the created Coordinates object for the component
     * 
     * @throws IllegalArgumentException
     */
    public Coordinates addComponent(Component c, String coordString)
            throws IllegalArgumentException {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        Coordinates newCoords = new Coordinates(coordString);
        componentToCoord.put(c, newCoords);
        addComponent(c);

        return newCoords;
    }

    /**
     * Adds a component to the layout at the specified coordinates. Null
     * arguments are not permitted and will throw an IllegalArgumentException.
     * 
     * @param c
     *            the component to be added
     * @param coord
     *            the coordinates of the component as a Coordinates object
     * @throws IllegalArgument
     *             Exception
     */
    public void addComponent(Component c, CoordinateLayout.Coordinates coord)
            throws IllegalArgumentException {
        if (c == null || coord == null) {
            throw new IllegalArgumentException();
        }
        componentToCoord.put(c, coord);
        addComponent(c);
    }

    /**
     * Sets the coordinates of a child component. Null arguments are not
     * permitted and will throw an IllegalArgumentException. In addition, the
     * user of this method must make sure that the given component is a child of
     * this layout.
     * 
     * @param c
     *            the target component
     * @param left
     *            distance of component left from layout left
     * @param top
     *            distance of component top from layout top
     * @param width
     *            component width
     * @param height
     *            component height
     * @param right
     *            distance of component right from layout right
     * @param bottom
     *            distance of component bottom from layout bottom
     * @throws IllegalArgumentException
     */
    public void setCoordinates(Component c, int left, int top, int width,
            int height, int right, int bottom) throws IllegalArgumentException {
        if (c == null || !componentList.contains(c)) {
            throw new IllegalArgumentException();
        }
        Coordinates coords = (Coordinates) componentToCoord.get(c);
        coords.setCoordinates(left, top, width, height, right, bottom);
    }

    /**
     * Sets the coordinates of a child component. Null arguments are not
     * permitted and will throw an IllegalArgumentException. In addition, the
     * user of this method must make sure that the given component is a child of
     * this coordinateLayout.
     * 
     * @param c
     *            the component thats position is changed
     * @param newCoord
     *            the new coordinates as a Coordinates object
     * @throws IllegalArgumentException
     */
    public void setCoordinates(Component c,
            CoordinateLayout.Coordinates newCoord)
            throws IllegalArgumentException {
        if (c == null || newCoord == null || !componentList.contains(c)) {
            throw new IllegalArgumentException();
        }
        // Detach old coordinate object from this layout
        Coordinates oldCoord = (Coordinates) componentToCoord.remove(c);
        oldCoord.removeParentLayout(this);

        // Attach new
        newCoord.addParentLayout(this);
        componentToCoord.put(c, newCoord);

        requestRepaint();
    }

    /**
     * This will put the given component at the top of the list.
     * 
     * @param c
     *            the component to be put to the top
     */
    public void sendToTop(Component c) {
        componentList.remove(c);
        componentList.add(c);

        requestRepaint();
    }

    /**
     * This will put the given component at the bottom of the list.
     * 
     * @param c
     *            the component to be put to the bottom
     */
    public void sendToBottom(Component c) {
        componentList.remove(c);
        componentList.add(0, c);

        requestRepaint();
    }

    /**
     * If a component is added with this method the component is added to the
     * top-left corner of the layout. The coordinates can later be accessed via
     * {@link CoordinateLayout#getCoordinates(Component)}
     * 
     * @param c
     *            the component to be added
     * @throws IllegalArgument
     *             Exception
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#addComponent(com.itmill.toolkit.ui.Component)
     */
    public void addComponent(Component c) throws IllegalArgumentException {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        componentList.add(c);

        // Someone might call this outside this object
        if (componentToCoord.get(c) == null) {
            componentToCoord.put(c, new Coordinates(0, 0, -1, -1, -1, -1));

        }

        super.addComponent(c);
        requestRepaint();
        ((Coordinates) componentToCoord.get(c)).addParentLayout(this);
    }

    /**
     * Removes the component from the coordinateLayout. Method will throw
     * IllegalArgumentException if the given component is not a child of this
     * layout.
     * 
     * @param c
     *            the component to be removed
     * @throws IllegalArgumentException
     * 
     * @see com.itmill.toolkit.ui.AbstractComponentContainer#removeComponent(com.itmill.toolkit.ui.Component)
     */
    public void removeComponent(Component c) throws IllegalArgumentException {
        if (c == null || !componentList.contains(c)) {
            throw new IllegalArgumentException();
        }
        ((Coordinates) componentToCoord.get(c)).removeParentLayout(this);

        super.removeComponent(c);
        componentList.remove(c);
        componentToCoord.remove(c);

        requestRepaint();
    }

    /**
     * Replaces a child component with a new one. The new component uses the
     * same coordinates as the previous component. If the arguments are null or
     * the component is not a child of this layout IllegalArgumentException is
     * thrown.
     * 
     * @param c1
     *            the existing child component
     * @param c2
     *            the new component
     * 
     * @throws IllegalArgumentException
     * 
     * @see com.itmill.toolkit.ui.ComponentContainer#replaceComponent(com.itmill.toolkit.ui.Component,
     *      com.itmill.toolkit.ui.Component)
     */
    public void replaceComponent(Component c1, Component c2) {
        if (c1 == null || c2 == null || !componentList.contains(c1)) {
            throw new IllegalArgumentException();
        }
        componentList.set(componentList.indexOf(c1), c2);
        requestRepaint();
    }

    /**
     * Get the coordinates for a given component in this layout
     * 
     * @param c
     * @return the Coordinates object for the given component
     * @throws IllegalArgumentException
     *             thrown if component is not in this coordinateLayout
     */
    public Coordinates getCoordinates(Component c)
            throws IllegalArgumentException {
        if (c == null || componentToCoord.get(c) == null) {
            throw new IllegalArgumentException();
        }
        return (Coordinates) componentToCoord.get(c);
    }

    /**
     * @see com.itmill.toolkit.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator<Component> getComponentIterator() {
        return componentList.iterator();
    }

    /**
     * Returns the iterator for the coordinates of the child components in this
     * layout.
     * 
     * @return the iterator for the coordinate list
     */
    public Iterator<Coordinates> getComponentCoordinateIterator() {
        return componentToCoord.values().iterator();
    }

    /**
     * Check if this layout has components.
     * 
     * @return true if this coordinateLayout has children, false otherwise
     */
    public boolean hasComponents() {
        return componentList.isEmpty();
    }

    /**
     * Check if a given component is in this layout
     * 
     * @param c
     * @return true if the component is in this layout, false otherwise
     */
    public boolean contains(Component c) {
        return componentList.contains(c);
    }

    /**
     * Get child component by index.
     * 
     * @param index
     *            must be index >= 0 and index < size() -1
     * @return the child component
     * @throws IllegalArgumentException
     *             if given index is illegal
     */
    public Component getComponentByIndex(int index)
            throws IllegalArgumentException {
        if (index < 0 || index > componentList.size() - 1) {
            throw new IllegalArgumentException("Illegal index");
        }

        return (Component) componentList.get(index);
    }

    /**
     * Returns the number of child components.
     * 
     * @return number of children
     */
    public int size() {
        return componentList.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.ui.AbstractLayout#getTag()
     */
    public String getTag() {
        return "coordinatelayout";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.ui.AbstractLayout#paintContent(com.itmill.toolkit.
     * terminal.PaintTarget)
     */
    public void paintContent(PaintTarget target) throws PaintException {
        // Superclass writes any common attributes in the paint target.
        super.paintContent(target);

        for (Iterator<Component> componentIterator = getComponentIterator(); componentIterator
                .hasNext();) {

            Component component = (Component) componentIterator.next();
            Coordinates coords = (Coordinates) componentToCoord.get(component);

            target.startTag("component");
            target.addAttribute("position", coords.toString());

            component.paint(target);

            target.endTag("component");

        }// for

    }// paintContent

    /**
     * 
     * This class is used as a container for the coordinates used by
     * CoordinateLayout. When attached to a CoordinateLayout object, changes to
     * this object are reflected on the layout.
     * 
     */
    public static class Coordinates {

        // Length of the property arrays
        protected static final int NUMBEROFPROPERTIES = 6;

        // The actual coordinates
        protected final int[] properties;

        // These help to decipher the coordinates
        protected final boolean[] isUnitPercent;

        // Host layout(s)
        protected final ArrayList<CoordinateLayout> listeners = new ArrayList<CoordinateLayout>(
                1);

        /**
         * Creates a new Coordinates object with the specified values. If the
         * user does not wish to specify a value, -1 can be used to let the
         * terminal decide the value.
         * 
         * The values are treated as absolute. You can change them to be
         * relative with setUnitsPercent or setAllUnitsPercent.
         * 
         * @param top
         *            distance of component top from coordinateLayout top
         * @param right
         *            distance of component right from coordinateLayout right
         * @param bottom
         *            distance of component bottom from coordinateLayout bottom
         * @param left
         *            distance of component left from coordinateLayout left
         * @param width
         *            component width
         * @param height
         *            component height
         * 
         * @throws NumberFormatException
         */
        public Coordinates(int left, int top, int width, int height, int right,
                int bottom) throws IllegalArgumentException {

            properties = new int[NUMBEROFPROPERTIES];
            isUnitPercent = new boolean[NUMBEROFPROPERTIES];

            properties[LEFT] = left;
            properties[TOP] = top;
            properties[WIDTH] = width;
            properties[HEIGHT] = height;
            properties[RIGHT] = right;
            properties[BOTTOM] = bottom;

            for (int i = 0; i < NUMBEROFPROPERTIES; i++) {
                if (properties[i] < -1) {
                    throw new IllegalArgumentException(
                            "Illegal coordinate value " + properties[i]);
                }
            }

        }

        /**
         * Create a new Coordinates object with the given value string.
         * 
         * @param coordinateString
         * @throws IllegalArgumentException
         * @see com.itmill.toolkit.ui.CoordinateLayout#addComponent(Component,
         *      String)
         */
        public Coordinates(String coordinateString)
                throws IllegalArgumentException {

            properties = new int[NUMBEROFPROPERTIES];
            isUnitPercent = new boolean[NUMBEROFPROPERTIES];

            Arrays.fill(properties, -1);

            setValuesFromString(coordinateString);

        }

        /**
         * Set unit of the given argument to be percentage. Initially all are
         * false.
         * 
         * @param top
         * @param right
         * @param bottom
         * @param left
         * @param width
         * @param height
         */
        public void setUnitsPercent(boolean left, boolean top, boolean width,
                boolean height, boolean right, boolean bottom) {
            isUnitPercent[TOP] = top;
            isUnitPercent[LEFT] = left;
            isUnitPercent[WIDTH] = width;
            isUnitPercent[HEIGHT] = height;
            isUnitPercent[RIGHT] = right;
            isUnitPercent[BOTTOM] = bottom;

            notifyParents();
        }

        /**
         * Set if a property should be treated as a percentage value.
         * 
         * @param valueId
         *            either CoordinateLayout.TOP, CoordinateLayout.RIGHT,
         *            CoordinateLayout.BOTTOM, CoordinateLayout.LEFT,
         *            CoordinateLayout.WIDTH or CoordinateLayout.HEIGHT
         * @param isPercent
         *            true if the value should be treated as a percentage, false
         *            otherwise
         */
        public void setUnitPercent(int valueId, boolean isPercent) {
            isUnitPercent[valueId] = isPercent;

            notifyParents();
        }

        /**
         * Set if the values should be treated as percentage.
         * 
         * @param value
         *            true if all the values should be treated as percentage,
         *            false if all the values should in treated as abslute pixel
         *            values
         */
        public void setAllUnitsPercent(boolean value) {
            java.util.Arrays.fill(isUnitPercent, value);
            notifyParents();
        }

        /**
         * Set a value for a direction.
         * 
         * @param direction
         *            either CoordinateLayout.TOP, CoordinateLayout.RIGHT,
         *            CoordinateLayout.BOTTOM, CoordinateLayout.LEFT,
         *            CoordinateLayout.WIDTH or CoordinateLayout.HEIGHT
         * @value the value to be set
         */
        public void setCoordinate(int direction, int value) {
            properties[direction] = value;
            notifyParents();
        }

        /**
         * Set coordinates for this object.
         * 
         * @param top
         * @param right
         * @param bottom
         * @param left
         */
        public void setCoordinates(int left, int top, int width, int height,
                int right, int bottom) {
            properties[LEFT] = left;
            properties[TOP] = top;
            properties[WIDTH] = width;
            properties[HEIGHT] = height;
            properties[RIGHT] = right;
            properties[BOTTOM] = bottom;

            for (int i = 0; i < NUMBEROFPROPERTIES; i++) {
                if (properties[i] < -1) {
                    throw new IllegalArgumentException(
                            "Illegal coordinate value " + properties[i]);
                }
            }
            notifyParents();
        }

        /**
         * Set the values in string format. For reference, see
         * {@link CoordinateLayout#addComponent(Component, String)}
         * 
         * @param coordinateString
         * @throws IllegalArgumentException
         */
        public void setCoordinates(String coordinateString)
                throws IllegalArgumentException {
            setValuesFromString(coordinateString);
            notifyParents();
        }

        /**
         * Returns the coordinates for this object.
         * 
         * @param direction
         *            either CoordinateLayout.TOP, CoordinateLayout.RIGHT,
         *            CoordinateLayout.BOTTOM, CoordinateLayout.LEFT,
         *            CoordinateLayout.WIDTH or CoordinateLayout.HEIGHT
         * @return coordinates for the given direction
         */
        public int getCoordinate(int direction) {
            return properties[direction];
        }

        /**
         * Check if a given value is a percentage value. The default is false.
         * 
         * @param value
         *            either CoordinateLayout.TOP, CoordinateLayout.RIGHT,
         *            CoordinateLayout.BOTTOM, CoordinateLayout.LEFT,
         *            CoordinateLayout.WIDTH or CoordinateLayout.HEIGHT
         * 
         * @return true if the given value has percentages as its unit, false
         *         otherwise
         */
        public boolean isValuePercent(int value) {
            return isUnitPercent[value];
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            StringBuffer returnString = new StringBuffer();

            for (int i = 0; i < properties.length; i++) {
                returnString.append(properties[i]);
                if (isUnitPercent[i]) {
                    returnString.append("%");
                }
                if (i != properties.length - 1) {
                    returnString.append(",");
                }
            }

            return returnString.toString();
        }

        /*
         * Parses the string to integer values
         */
        protected void setValuesFromString(String coordinateString) {

            int[] newProperties = new int[6];
            boolean[] newPercent = new boolean[6];

            resetContents(newProperties, newPercent);

            String coordStringArray[] = coordinateString.split(",");
            if (coordStringArray.length > 6) {
                throw new IllegalArgumentException(
                        "Incorrect string syntax: too many arguments");
            }

            Pattern numberPattern = Pattern.compile("[-]??\\d+");
            String percentRegex = ".*%.*";
            Matcher matcher = null;
            try {
                for (int i = 0; i < coordStringArray.length; i++) {

                    matcher = numberPattern.matcher(coordStringArray[i].trim());
                    if (matcher.find()) {
                        newProperties[i] = Integer.parseInt(matcher.group());

                        newPercent[i] = coordStringArray[i]
                                .matches(percentRegex);
                    } else {
                        throw new IllegalArgumentException(
                                "Error parsing number: " + coordStringArray[i]);
                    }
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("Error parsing string: "
                        + e.toString());
            }

            // Only set new values if there were no parsing errors
            resetContents(properties, isUnitPercent);
            System.arraycopy(newProperties, 0, properties, 0,
                    NUMBEROFPROPERTIES);
            System.arraycopy(newPercent, 0, isUnitPercent, 0,
                    NUMBEROFPROPERTIES);
        }

        /*
         * Reset coordinate properties. Does not notify listeners
         */
        protected void resetContents(int[] prop, boolean[] perc) {
            Arrays.fill(prop, -1);
            Arrays.fill(perc, false);
        }

        /*
         * These methods ensure that changes to the coordinate objects are
         * reflected to the host layout
         */
        protected void addParentLayout(CoordinateLayout listener) {
            listeners.add(listener);
        }

        protected void removeParentLayout(CoordinateLayout listener) {
            listeners.remove(listener);
        }

        protected void notifyParents() {
            for (int i = 0; i < listeners.size(); i++) {
                ((CoordinateLayout) listeners.get(i)).requestRepaint();
            }
        }
    }// class Coordinates

}// class CoordinateLayout

