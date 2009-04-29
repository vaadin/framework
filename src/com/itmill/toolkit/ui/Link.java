/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;

/**
 * Link is used to create external or internal URL links.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Link extends AbstractComponent {

    /* Target window border type constant: No window border */
    public static final int TARGET_BORDER_NONE = Window.BORDER_NONE;

    /* Target window border type constant: Minimal window border */
    public static final int TARGET_BORDER_MINIMAL = Window.BORDER_MINIMAL;

    /* Target window border type constant: Default window border */
    public static final int TARGET_BORDER_DEFAULT = Window.BORDER_DEFAULT;

    private Resource resource = null;

    private String targetName;

    private int targetBorder = TARGET_BORDER_DEFAULT;

    private int targetWidth = -1;

    private int targetHeight = -1;

    /**
     * Creates a new link.
     */
    public Link() {

    }

    /**
     * Creates a new instance of Link.
     * 
     * @param caption
     * @param resource
     */
    public Link(String caption, Resource resource) {
        setCaption(caption);
        this.resource = resource;
    }

    /**
     * Creates a new instance of Link that opens a new window.
     * 
     * 
     * @param caption
     *            the Link text.
     * @param targetName
     *            the name of the target window where the link opens to. Empty
     *            name of null implies that the target is opened to the window
     *            containing the link.
     * @param width
     *            the Width of the target window.
     * @param height
     *            the Height of the target window.
     * @param border
     *            the Border style of the target window.
     * 
     */
    public Link(String caption, Resource resource, String targetName,
            int width, int height, int border) {
        setCaption(caption);
        this.resource = resource;
        setTargetName(targetName);
        setTargetWidth(width);
        setTargetHeight(height);
        setTargetBorder(border);
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    @Override
    public String getTag() {
        return "link";
    }

    /**
     * Paints the content of this component.
     * 
     * @param target
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        if (resource != null) {
            target.addAttribute("src", resource);
        } else {
            return;
        }

        // Target window name
        final String name = getTargetName();
        if (name != null && name.length() > 0) {
            target.addAttribute("name", name);
        }

        // Target window size
        if (getTargetWidth() >= 0) {
            target.addAttribute("targetWidth", getTargetWidth());
        }
        if (getTargetHeight() >= 0) {
            target.addAttribute("targetHeight", getTargetHeight());
        }

        // Target window border
        switch (getTargetBorder()) {
        case TARGET_BORDER_MINIMAL:
            target.addAttribute("border", "minimal");
            break;
        case TARGET_BORDER_NONE:
            target.addAttribute("border", "none");
            break;
        }
    }

    /**
     * Returns the target window border.
     * 
     * @return the target window border.
     */
    public int getTargetBorder() {
        return targetBorder;
    }

    /**
     * Returns the target window height or -1 if not set.
     * 
     * @return the target window height.
     */
    public int getTargetHeight() {
        return targetHeight < 0 ? -1 : targetHeight;
    }

    /**
     * Returns the target window name. Empty name of null implies that the
     * target is opened to the window containing the link.
     * 
     * @return the target window name.
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Returns the target window width or -1 if not set.
     * 
     * @return the target window width.
     */
    public int getTargetWidth() {
        return targetWidth < 0 ? -1 : targetWidth;
    }

    /**
     * Sets the border of the target window.
     * 
     * @param targetBorder
     *            the targetBorder to set.
     */
    public void setTargetBorder(int targetBorder) {
        if (targetBorder == TARGET_BORDER_DEFAULT
                || targetBorder == TARGET_BORDER_MINIMAL
                || targetBorder == TARGET_BORDER_NONE) {
            this.targetBorder = targetBorder;
            requestRepaint();
        }
    }

    /**
     * Sets the target window height.
     * 
     * @param targetHeight
     *            the targetHeight to set.
     */
    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
        requestRepaint();
    }

    /**
     * Sets the target window name.
     * 
     * @param targetName
     *            the targetName to set.
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
        requestRepaint();
    }

    /**
     * Sets the target window width.
     * 
     * @param targetWidth
     *            the targetWidth to set.
     */
    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
        requestRepaint();
    }

    /**
     * Returns the resource this link opens.
     * 
     * @return the Resource.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the resource this link opens.
     * 
     * @param resource
     *            the resource to set.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
        requestRepaint();
    }
}
