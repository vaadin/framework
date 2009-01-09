/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.gwt.client.ui.IMarginInfo;
import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.Bits;

/**
 * Extension to the {@link ComponentContainer} interface which adds the
 * layouting control to the elements in the container. This is required by the
 * various layout components to enable them to place other components in
 * specific locations in the UI.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Layout extends ComponentContainer {

    /**
     * Enable layout margins. Affects all four sides of the layout. This will
     * tell the client-side implementation to leave extra space around the
     * layout. The client-side implementation decides the actual amount, and it
     * can vary between themes.
     * 
     * @param enabled
     */
    public void setMargin(boolean enabled);

    /**
     * Enable specific layout margins. This will tell the client-side
     * implementation to leave extra space around the layout in specified edges,
     * clockwise from top (top, right, bottom, left). The client-side
     * implementation decides the actual amount, and it can vary between themes.
     * 
     * @param top
     * @param right
     * @param bottom
     * @param left
     */
    public void setMargin(boolean top, boolean right, boolean bottom,
            boolean left);

    /**
     * AlignmentHandler is most commonly an advanced {@link Layout} that can
     * align its components.
     */
    public interface AlignmentHandler {

        /**
         * Contained component should be aligned horizontally to the left.
         * 
         * @deprecated Use of {@link Alignment} class and its constants
         */
        @Deprecated
        public static final int ALIGNMENT_LEFT = Bits.ALIGNMENT_LEFT;

        /**
         * Contained component should be aligned horizontally to the right.
         * 
         * @deprecated Use of {@link Alignment} class and its constants
         */
        @Deprecated
        public static final int ALIGNMENT_RIGHT = Bits.ALIGNMENT_RIGHT;

        /**
         * Contained component should be aligned vertically to the top.
         * 
         * @deprecated Use of {@link Alignment} class and its constants
         */
        @Deprecated
        public static final int ALIGNMENT_TOP = Bits.ALIGNMENT_TOP;

        /**
         * Contained component should be aligned vertically to the bottom.
         * 
         * @deprecated Use of {@link Alignment} class and its constants
         */
        @Deprecated
        public static final int ALIGNMENT_BOTTOM = Bits.ALIGNMENT_BOTTOM;

        /**
         * Contained component should be horizontally aligned to center.
         * 
         * @deprecated Use of {@link Alignment} class and its constants
         */
        @Deprecated
        public static final int ALIGNMENT_HORIZONTAL_CENTER = Bits.ALIGNMENT_HORIZONTAL_CENTER;

        /**
         * Contained component should be vertically aligned to center.
         * 
         * @deprecated Use of {@link Alignment} class and its constants
         */
        @Deprecated
        public static final int ALIGNMENT_VERTICAL_CENTER = Bits.ALIGNMENT_VERTICAL_CENTER;

        /**
         * Set alignment for one contained component in this layout. Alignment
         * is calculated as a bit mask of the two passed values.
         * 
         * @deprecated Use {@link #setComponentAlignment(Component, Alignment)}
         *             instead
         * 
         * @param childComponent
         *            the component to align within it's layout cell.
         * @param horizontalAlignment
         *            the horizontal alignment for the child component (left,
         *            center, right). Use ALIGNMENT constants.
         * @param verticalAlignment
         *            the vertical alignment for the child component (top,
         *            center, bottom). Use ALIGNMENT constants.
         */
        @Deprecated
        public void setComponentAlignment(Component childComponent,
                int horizontalAlignment, int verticalAlignment);

        /**
         * Set alignment for one contained component in this layout. Use
         * predefined alignments from Alignment class.
         * 
         * Example: <code>
         *      layout.setComponentAlignment(myComponent, Alignment.TOP_RIGHT);
         * </code>
         * 
         * @param childComponent
         *            the component to align within it's layout cell.
         * @param alignment
         *            the Alignment value to be set
         */
        public void setComponentAlignment(Component childComponent,
                Alignment alignment);

        /**
         * Returns the current Alignment of given component.
         * 
         * @param childComponent
         * @return the {@link Alignment}
         */
        public Alignment getComponentAlignment(Component childComponent);

    }

    /**
     * This type of layout can set spacing between its components on of off.
     * 
     * TODO refine javadocs
     */
    public interface SpacingHandler {
        /**
         * Enable spacing between child components within this layout.
         * 
         * <p>
         * <strong>NOTE:</strong> This will only affect spaces between
         * components, not also all around spacing of the layout (i.e. do not
         * mix this with HTML Table elements cellspacing-attribute). Use
         * {@link #setMargin(boolean)} to add extra space around the layout.
         * </p>
         * 
         * @param enabled
         */
        public void setSpacing(boolean enabled);

        /**
         * 
         * @return true if spacing, layout leaves space between components
         */
        public boolean isSpacingEnabled();
    }

    /**
     * This type of layout can enable margins.
     */
    public interface MarginHandler {
        /**
         * Enable margins for this layout.
         * 
         * <p>
         * <strong>NOTE:</strong> This will only affect margins for the layout,
         * not spacing between components inside the layout. Use
         * {@link #setSpacing(boolean)} to add space between components in the
         * layout.
         * </p>
         * 
         * @param marginInfo
         *            MarginInfo object containing the new margins.
         */
        public void setMargin(MarginInfo marginInfo);

        /**
         * 
         * @return MarginInfo containing the currently enabled margins.
         */
        public MarginInfo getMargin();
    }

    public static class MarginInfo extends IMarginInfo {

        public MarginInfo(boolean enabled) {
            super(enabled, enabled, enabled, enabled);
        }

        public MarginInfo(boolean top, boolean right, boolean bottom,
                boolean left) {
            super(top, right, bottom, left);
        }
    }
}
