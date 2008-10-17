/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Ordered layout.
 * 
 * <code>OrderedLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition in specified orientation.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class OrderedLayout extends AbstractLayout implements
        Layout.AlignmentHandler, Layout.SpacingHandler {

    /* Predefined orientations */

    /**
     * Components are to be laid out vertically.
     */
    public static int ORIENTATION_VERTICAL = 0;

    /**
     * Components are to be laid out horizontally.
     */
    public static int ORIENTATION_HORIZONTAL = 1;

    private static final int ALIGNMENT_DEFAULT = ALIGNMENT_TOP + ALIGNMENT_LEFT;

    /**
     * Custom layout slots containing the components.
     */
    protected LinkedList components = new LinkedList();

    /* Child component alignments */

    /**
     * Mapping from components to alignments (horizontal + vertical).
     */
    private final Map componentToAlignment = new HashMap();

    private final Map<Component, Float> componentToExpandRatio = new HashMap<Component, Float>();

    /**
     * Orientation of the layout.
     */
    private int orientation;

    /**
     * Is spacing between contained components enabled. Defaults to false.
     */
    private boolean spacing = false;

    /**
     * Creates a new ordered layout. The order of the layout is
     * <code>ORIENTATION_VERTICAL</code>.
     */
    public OrderedLayout() {
        orientation = ORIENTATION_VERTICAL;
    }

    /**
     * Create a new ordered layout. The orientation of the layout is given as
     * parameters.
     * 
     * @param orientation
     *            the Orientation of the layout.
     */
    public OrderedLayout(int orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    public String getTag() {
        return "orderedlayout";
    }

    /**
     * Add a component into this container. The component is added to the right
     * or under the previous component.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponent(Component c) {
        super.addComponent(c);
        components.add(c);
        requestRepaint();
    }

    /**
     * Adds a component into this container. The component is added to the left
     * or on top of the other components.
     * 
     * @param c
     *            the component to be added.
     */
    public void addComponentAsFirst(Component c) {
        super.addComponent(c);
        components.addFirst(c);
        requestRepaint();
    }

    /**
     * Adds a component into indexed position in this container.
     * 
     * @param c
     *            the component to be added.
     * @param index
     *            the Index of the component position. The components currently
     *            in and after the position are shifted forwards.
     */
    public void addComponent(Component c, int index) {
        super.addComponent(c);
        components.add(index, c);
        requestRepaint();
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *            the component to be removed.
     */
    public void removeComponent(Component c) {
        super.removeComponent(c);
        components.remove(c);
        componentToAlignment.remove(c);
        requestRepaint();
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     * 
     * @return the Iterator of the components inside the container.
     */
    public Iterator getComponentIterator() {
        return components.iterator();
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Adds the attributes: orientation
        // note that the default values (b/vertical) are omitted
        if (orientation == ORIENTATION_HORIZONTAL) {
            target.addAttribute("orientation", "horizontal");
        }

        // Add spacing attribute (omitted if false)
        if (spacing) {
            target.addAttribute("spacing", spacing);
        }

        final String[] alignmentsArray = new String[components.size()];
        final Integer[] expandRatioArray = new Integer[components.size()];
        float sum = getExpandRatioSum();
        boolean equallyDivided = false;
        int realSum = 0;
        if (sum == 0 && components.size() > 0) {
            // no component has been expanded, all components have same expand
            // rate
            equallyDivided = true;
            float equalSize = 1 / (float) components.size();
            int myRatio = Math.round(equalSize * 1000);
            for (int i = 0; i < expandRatioArray.length; i++) {
                expandRatioArray[i] = myRatio;
            }
            realSum = myRatio * components.size();
        }

        // Adds all items in all the locations
        int index = 0;
        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (c != null) {
                // Paint child component UIDL
                c.paint(target);
                alignmentsArray[index] = String
                        .valueOf(getComponentAlignment(c));
                if (!equallyDivided) {
                    int myRatio = Math.round((getExpandRatio(c) / sum) * 1000);
                    expandRatioArray[index] = myRatio;
                    realSum += myRatio;
                }
                index++;
            }
        }

        // correct possible rounding error
        expandRatioArray[0] -= realSum - 1000;

        // Add child component alignment info to layout tag
        target.addAttribute("alignments", alignmentsArray);
        target.addAttribute("expandRatios", expandRatioArray);
    }

    private float getExpandRatioSum() {
        float sum = 0;
        for (Iterator<Entry<Component, Float>> iterator = componentToExpandRatio
                .entrySet().iterator(); iterator.hasNext();) {
            sum += iterator.next().getValue();
        }
        return sum;
    }

    /**
     * Gets the orientation of the container.
     * 
     * @return the Value of property orientation.
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the container.
     * 
     * @param orientation
     *            the New value of property orientation.
     */
    public void setOrientation(int orientation) {

        // Checks the validity of the argument
        if (orientation < ORIENTATION_VERTICAL
                || orientation > ORIENTATION_HORIZONTAL) {
            throw new IllegalArgumentException();
        }

        this.orientation = orientation;

        requestRepaint();
    }

    /* Documented in superclass */
    public void replaceComponent(Component oldComponent, Component newComponent) {

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component component = (Component) i.next();

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
            if (oldLocation > newLocation) {
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
                components.remove(newComponent);
                componentToAlignment.remove(newComponent);
                components.add(oldLocation, newComponent);
            } else {
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
                components.remove(oldComponent);
                componentToAlignment.remove(oldComponent);
                components.add(newLocation, oldComponent);
            }

            requestRepaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.ui.Layout.AlignmentHandler#setComponentAlignment(com
     * .itmill.toolkit.ui.Component, int, int)
     */
    public void setComponentAlignment(Component childComponent,
            int horizontalAlignment, int verticalAlignment) {
        componentToAlignment.put(childComponent, new Integer(
                horizontalAlignment + verticalAlignment));
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.ui.Layout.AlignmentHandler#getComponentAlignment(com
     * .itmill.toolkit.ui.Component)
     */
    public int getComponentAlignment(Component childComponent) {
        final Integer bitMask = (Integer) componentToAlignment
                .get(childComponent);
        if (bitMask != null) {
            return bitMask.intValue();
        } else {
            return ALIGNMENT_DEFAULT;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.ui.Layout.SpacingHandler#setSpacing(boolean)
     */
    public void setSpacing(boolean enabled) {
        spacing = enabled;
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.ui.Layout.SpacingHandler#isSpacingEnabled()
     */
    public boolean isSpacingEnabled() {
        return spacing;
    }

    /**
     * TODO
     * 
     * @param component
     * @param ratio
     */
    public void setExpandRatio(Component component, float ratio) {
        componentToExpandRatio.put(component, ratio);
    };

    /**
     * TODO
     * 
     * @param component
     * @return
     */
    public float getExpandRatio(Component component) {
        Float ratio = componentToExpandRatio.get(component);
        return (ratio == null) ? 0 : ratio.floatValue();
    }

}
