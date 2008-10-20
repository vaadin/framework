/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

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
 * @deprecated Deprecated in favor of new OrderedLayout
 */
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
            setExpandRatio(expanded, 0.0f);
        }

        expanded = c;
        setExpandRatio(expanded, 1.0f);
        requestRepaint();
    }

}
