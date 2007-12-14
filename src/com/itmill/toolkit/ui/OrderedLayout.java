/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo;

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
public class OrderedLayout extends AbstractLayout {

    /* Predefined orientations ***************************************** */

    /**
     * Components are to be laid out vertically.
     */
    public static int ORIENTATION_VERTICAL = 0;

    /**
     * Components are to be laid out horizontally.
     */
    public static int ORIENTATION_HORIZONTAL = 1;

    /**
     * Custom layout slots containing the components.
     */
    protected LinkedList components = new LinkedList();

    /* Child component alignments ************************************** */

    /**
     * Mapping from components to alignments (horizontal + vertical).
     */
    private final Map componentToAlignment = new HashMap();

    /**
     * Contained component should be aligned horizontally to the left.
     */
    public static final int ALIGNMENT_LEFT = AlignmentInfo.ALIGNMENT_LEFT;

    /**
     * Contained component should be aligned horizontally to the right.
     */
    public static final int ALIGNMENT_RIGHT = AlignmentInfo.ALIGNMENT_RIGHT;

    /**
     * Contained component should be aligned vertically to the top.
     */
    public static final int ALIGNMENT_TOP = AlignmentInfo.ALIGNMENT_TOP;

    /**
     * Contained component should be aligned vertically to the bottom.
     */
    public static final int ALIGNMENT_BOTTOM = AlignmentInfo.ALIGNMENT_BOTTOM;

    /**
     * Contained component should be horizontally aligned to center.
     */
    public static final int ALIGNMENT_HORIZONTAL_CENTER = AlignmentInfo.ALIGNMENT_HORIZONTAL_CENTER;

    /**
     * Contained component should be vertically aligned to center.
     */
    public static final int ALIGNMENT_VERTICAL_CENTER = AlignmentInfo.ALIGNMENT_VERTICAL_CENTER;

    private static final int ALIGNMENT_DEFAULT = ALIGNMENT_TOP + ALIGNMENT_LEFT;

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
     *                the Orientation of the layout.
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
     *                the component to be added.
     */
    public void addComponent(Component c) {
        components.add(c);
        super.addComponent(c);
        requestRepaint();
    }

    /**
     * Adds a component into this container. The component is added to the left
     * or on top of the other components.
     * 
     * @param c
     *                the component to be added.
     */
    public void addComponentAsFirst(Component c) {
        components.addFirst(c);
        super.addComponent(c);
        requestRepaint();
    }

    /**
     * Adds a component into indexed position in this container.
     * 
     * @param c
     *                the component to be added.
     * @param index
     *                the Index of the component position. The components
     *                currently in and after the position are shifted forwards.
     */
    public void addComponent(Component c, int index) {
        components.add(index, c);
        super.addComponent(c);
        requestRepaint();
    }

    /**
     * Removes the component from this container.
     * 
     * @param c
     *                the component to be removed.
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
     *                the Paint Event.
     * @throws PaintException
     *                 if the paint operation failed.
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
        // Adds all items in all the locations
        int index = 0;
        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (c != null) {
                // Paint child component UIDL
                c.paint(target);
                alignmentsArray[index++] = String
                        .valueOf(getComponentAlignment(c));
            }
        }

        // Add child component alignment info to layout tag
        target.addAttribute("alignments", alignmentsArray);
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
     *                the New value of property orientation.
     */
    public void setOrientation(int orientation) {

        // Checks the validity of the argument
        if (orientation < ORIENTATION_VERTICAL
                || orientation > ORIENTATION_HORIZONTAL) {
            throw new IllegalArgumentException();
        }

        this.orientation = orientation;

        // requestRepaint()
        // FIXME remove lines below and uncomment above
        // Workaround to bypass IOrderedLayouts limitations (separate classes
        // for different orientation + subtreecacing)
        final Iterator it = getComponentIterator();
        while (it.hasNext()) {
            final Component c = (Component) it.next();
            c.requestRepaint();
        }
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

    /**
     * Set alignment for one contained component in this layout. Alignment is
     * calculated as a bit mask of the two passed values.
     * 
     * @param childComponent
     *                the component to align within it's layout cell.
     * @param horizontalAlignment
     *                the horizontal alignment for the child component (left,
     *                center, right).
     * @param verticalAlignment
     *                the vertical alignment for the child component (top,
     *                center, bottom).
     */
    public void setComponentAlignment(Component childComponent,
            int horizontalAlignment, int verticalAlignment) {
        componentToAlignment.put(childComponent, new Integer(
                horizontalAlignment + verticalAlignment));
    }

    public int getComponentAlignment(Component childComponent) {
        final Integer bitMask = (Integer) componentToAlignment
                .get(childComponent);
        if (bitMask != null) {
            return bitMask.intValue();
        } else {
            return ALIGNMENT_DEFAULT;
        }
    }

    /**
     * Enable spacing between child components within this layout.
     * 
     * <p>
     * <strong>NOTE:</strong> This will only affect spaces between components,
     * not also all around spacing of the layout (i.e. do not mix this with HTML
     * Table elements cellspacing-attribute). Use {@link #setMargin(boolean)} to
     * add extra space around the layout.
     * </p>
     * 
     * @param enabled
     */
    public void setSpacing(boolean enabled) {
        spacing = enabled;
        requestRepaint();
    }

    /**
     * 
     * @return true if spacing, layout leaves space between components
     */
    public boolean isSpacingEnabled() {
        return spacing;
    }
}
