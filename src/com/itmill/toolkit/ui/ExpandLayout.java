/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Iterator;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * A layout that will give one of it's components as much space as possible,
 * while still showing the other components in the layout. The other components
 * will in effect be given a fixed sized space, while the space given to the
 * expanded component will grow/shrink to fill the rest of the space available -
 * for instance when re-sizing the window.
 * 
 * Note that this layout is 100% in both directions by default ({link
 * {@link #setSizeFull()}). Remember to set the units if you want to specify a
 * fixed size. If the layout fails to show up, check that the parent layout is
 * actually giving some space.
 * 
 */
public class ExpandLayout extends OrderedLayout {

    private Component expanded;

    public ExpandLayout() {
        setSizeFull();
    }

    public ExpandLayout(int orientation) {
        this();
        setOrientation(orientation);
    }

    /**
     * @param c
     *            Component which container will be maximized
     */
    public void expand(Component c) {
        expanded = c;
        requestRepaint();
    }

    public String getTag() {
        return "expandlayout";
    }

    public void paintContent(PaintTarget target) throws PaintException {

        // Add margin info. Defaults to false.
        target.addAttribute("margins", margins.getBitMask());

        // Add spacing attribute (omitted if false)
        if (isSpacingEnabled()) {
            target.addAttribute("spacing", true);
        }

        // Adds the attributes: orientation
        // note that the default values (b/vertival) are omitted
        if (getOrientation() == ORIENTATION_HORIZONTAL) {
            target.addAttribute("orientation", "horizontal");
        }

        final String[] alignmentsArray = new String[components.size()];

        // Adds all items in all the locations
        int index = 0;
        for (final Iterator i = getComponentIterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (c != null) {
                target.startTag("cc");
                if (c == expanded) {
                    target.addAttribute("expanded", true);
                }
                c.paint(target);
                target.endTag("cc");
            }
            alignmentsArray[index++] = String.valueOf(getComponentAlignment(c));

        }

        // Add child component alignment info to layout tag
        target.addAttribute("alignments", alignmentsArray);

    }

    public void addComponent(Component c, int index) {
        if (expanded == null) {
            expanded = c;
        }
        super.addComponent(c, index);
    }

    public void addComponent(Component c) {
        if (expanded == null) {
            expanded = c;
        }
        super.addComponent(c);
    }

    public void addComponentAsFirst(Component c) {
        if (expanded == null) {
            expanded = c;
        }
        super.addComponentAsFirst(c);
    }

    public void removeComponent(Component c) {
        super.removeComponent(c);
        if (c == expanded) {
            if (getComponentIterator().hasNext()) {
                expanded = (Component) getComponentIterator().next();
            } else {
                expanded = null;
            }
        }
    }

    public void replaceComponent(Component oldComponent, Component newComponent) {
        super.replaceComponent(oldComponent, newComponent);
        if (oldComponent == expanded) {
            expanded = newComponent;
        }
    }

}
