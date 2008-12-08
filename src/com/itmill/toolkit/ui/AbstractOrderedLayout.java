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
import com.itmill.toolkit.terminal.Sizeable;

public abstract class AbstractOrderedLayout extends AbstractLayout implements
        Layout.AlignmentHandler, Layout.SpacingHandler {

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
     * Is spacing between contained components enabled. Defaults to false.
     */
    private boolean spacing = false;

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
        componentToExpandRatio.remove(c);
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
        if (expandRatioArray.length > 0) {
            expandRatioArray[0] -= realSum - 1000;
        }

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
        if (components.contains(childComponent)) {
            componentToAlignment.put(childComponent, new Integer(
                    horizontalAlignment + verticalAlignment));
            requestRepaint();
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using setComponentAlignment()");
        }
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
     * <p>
     * This method is used to control how excess space in layout is distributed
     * among components. Excess space may exist if layout is sized and contained
     * non relatively sized components don't consume all available space.
     * 
     * <p>
     * Example how to distribute 1:3 (33%) for component1 and 2:3 (67%) for
     * component2 :
     * 
     * <code>
     * layout.setExpandRatio(component1, 1);<br>
     * layout.setExpandRatio(component2, 2);
     * </code>
     * 
     * <p>
     * If no ratios have been set, the excess space is distributed evenly among
     * all components.
     * 
     * <p>
     * Note, that width or height (depending on orientation) needs to be defined
     * for this method to have any effect.
     * 
     * @see Sizeable
     * 
     * @param component
     *            the component in this layout which expand ratio is to be set
     * @param ratio
     */
    public void setExpandRatio(Component component, float ratio) {
        if (components.contains(component)) {
            componentToExpandRatio.put(component, ratio);
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using setExpandRatio()");
        }
    };

    /**
     * Returns the expand ratio of given component.
     * 
     * @param component
     *            which expand ratios is requested
     * @return expand ratio of given component, 0.0f by default
     */
    public float getExpandRatio(Component component) {
        Float ratio = componentToExpandRatio.get(component);
        return (ratio == null) ? 0 : ratio.floatValue();
    }

}
