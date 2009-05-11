/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

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
 * @deprecated Deprecated in favor of the new OrderedLayout
 */
@SuppressWarnings("serial")
@Deprecated
public class ExpandLayout extends OrderedLayout {

    private Component expanded = null;

    public ExpandLayout() {
        this(ORIENTATION_VERTICAL);
    }

    public ExpandLayout(int orientation) {
        super(orientation);

        setSizeFull();
    }

    /**
     * @param c
     *            Component which container will be maximized
     */
    public void expand(Component c) {
        if (expanded != null) {
            try {
                setExpandRatio(expanded, 0.0f);
            } catch (IllegalArgumentException e) {
                // Ignore error if component has been removed
            }
        }

        expanded = c;
        if (expanded != null) {
            setExpandRatio(expanded, 1.0f);
        }

        requestRepaint();
    }

    @Override
    public void addComponent(Component c, int index) {
        super.addComponent(c, index);
        if (expanded == null) {
            expand(c);
        }
    }

    @Override
    public void addComponent(Component c) {
        super.addComponent(c);
        if (expanded == null) {
            expand(c);
        }
    }

    @Override
    public void addComponentAsFirst(Component c) {
        super.addComponentAsFirst(c);
        if (expanded == null) {
            expand(c);
        }
    }

    @Override
    public void removeComponent(Component c) {
        super.removeComponent(c);
        if (c == expanded) {
            if (getComponentIterator().hasNext()) {
                expand((Component) getComponentIterator().next());
            } else {
                expand(null);
            }
        }
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        super.replaceComponent(oldComponent, newComponent);
        if (oldComponent == expanded) {
            expand(newComponent);
        }
    }
}
