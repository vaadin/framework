/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo;
import com.itmill.toolkit.terminal.gwt.client.ui.IMarginInfo;

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
     * TODO make javadocs, remove from implementing classes
     */
    public interface AlignmentHandler {

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

        /**
         * Set alignment for one contained component in this layout. Alignment
         * is calculated as a bit mask of the two passed values.
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
        public void setComponentAlignment(Component childComponent,
                int horizontalAlignment, int verticalAlignment);

        /**
         * 
         * @param childComponent
         * @return
         */
        public int getComponentAlignment(Component childComponent);

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
     * 
     * TODO refine javadocs
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
