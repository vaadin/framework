/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.Sizeable;

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
public interface Layout extends ComponentContainer, Sizeable {

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

}
