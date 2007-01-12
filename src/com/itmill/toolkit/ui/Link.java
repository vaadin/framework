/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;

/** Link component.
 *  Link is used to create external or internal URL links.
 *
 *  Internal links can be used to create action items, which
 *  change the state to application to one of the predefined states.
 *  For example, a link can be created for existing MenuTree items.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Link extends AbstractComponent {

	/* Target window border type constant: No window border */
	public static final int TARGET_BORDER_NONE = Window.BORDER_NONE;
	/* Target window border type constant: Minimal window border */
	public static final int TARGET_BORDER_MINIMAL = Window.BORDER_MINIMAL;
	/* Target window border type constant: Default window border */
	public static final int TARGET_BORDER_DEFAULT = Window.BORDER_DEFAULT;

	private Resource resource = null;
	private Window window = null;
	private String targetName;
	private int targetBorder = TARGET_BORDER_DEFAULT;
	private int targetWidth = -1;
	private int targetHeight = -1;

	/** Creates a new link. */
	public Link() {
		
	}
	/** Creates a new link to a window. */
	public Link(Window window) {

		// Set the link caption to match window caption
		setCaption(window.getCaption());

		// Set the target
		setTargetName(window.getName());

		setTargetName(window.getName());
		setTargetWidth(window.getWidth());
		setTargetHeight(window.getHeight());
		setTargetBorder(window.getBorder());
	}

	/** Creates a new instance of Link */
	public Link(String caption, Resource resource) {
		setCaption(caption);
		this.resource = resource;
	}

	/** Creates a new instance of Link that opens a new window. 
	 * 
	 * 
	 * @param caption Link text.
	 * @param targetName The name of the target window where the link 
	 * opens to. Empty name of null implies that
	 * the target is opened to the window containing the link.
	 * @param width Width of the target window.
	 * @param height Height of the target window.
	 * @param border Borget style of the target window.
	 * 
	 */
	public Link(
		String caption,
		Resource resource,
		String targetName,
		int width,
		int height,
		int border) {
		setCaption(caption);
		this.resource = resource;
		setTargetName(targetName);
		setTargetWidth(width);
		setTargetHeight(height);
		setTargetBorder(border);
	}

	/** Get component UIDL tag.
	 *  @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "link";
	}

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		if (window != null)
			target.addAttribute("src", window.getURL().toString());
		else if (resource != null)
			target.addAttribute("src", resource);
		else
			return;
		
		// Target window name
		String name = getTargetName();
		if (name != null && name.length()>0)
			target.addAttribute("name", name);

		// Target window size
		if (getTargetWidth() >= 0)
			target.addAttribute("width", getTargetWidth());
		if (getTargetHeight() >= 0)
			target.addAttribute("height", getTargetHeight());
			
		// Target window border
		switch (getTargetBorder()) {
			case TARGET_BORDER_MINIMAL :
				target.addAttribute("border", "minimal");
				break;
			case TARGET_BORDER_NONE :
				target.addAttribute("border", "none");
				break;
		}
	}

	/** Returns the target window border.
	 * @return int
	 */
	public int getTargetBorder() {
		return targetBorder;
	}

	/** Returns the target window height or -1 if not set.
	 * @return int
	 */
	public int getTargetHeight() {
		return targetHeight < 0 ? -1 : targetHeight;
	}

	/** Returns the target window name. Empty name of null implies that
	 * the target is opened to the window containing the link.
	 * @return String
	 */
	public String getTargetName() {
		return targetName;
	}

	/** Returns the target window width or -1 if not set.
	 * @return int
	 */
	public int getTargetWidth() {
		return targetWidth < 0 ? -1 : targetWidth;
	}

	/** Sets the border of the target window.
	 * @param targetBorder The targetBorder to set
	 */
	public void setTargetBorder(int targetBorder) {
		if (targetBorder == TARGET_BORDER_DEFAULT
			|| targetBorder == TARGET_BORDER_MINIMAL
			|| targetBorder == TARGET_BORDER_NONE) {
			this.targetBorder = targetBorder;
			requestRepaint();
		}
	}

	/** Sets the target window height.
	 * @param targetHeight The targetHeight to set
	 */
	public void setTargetHeight(int targetHeight) {
		this.targetHeight = targetHeight;
		requestRepaint();
	}

	/** Sets the target window name.
	 * @param targetName The targetName to set
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
		requestRepaint();
	}

	/** Sets the target window width.
	 * @param targetWidth The targetWidth to set
	 */
	public void setTargetWidth(int targetWidth) {
		this.targetWidth = targetWidth;
		requestRepaint();
	}
	/** Returns the resource this link opens.
	 * @return Resource
	 */
	public Resource getResource() {
		return resource;
	}

	/** Returns the window this link opens.
	 * @return Window
	 */
	public Window getWindow() {
		return window;
	}

	/** Sets the resource this link opens.
	 * @param resource The resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
		if (resource != null) {
			window = null;
		}
		requestRepaint();
	}

	/** Sets the window this link opens.
	 * @param window The window to set
	 */
	public void setWindow(Window window) {
		this.window = window;
		if (window != null) {
			this.resource = null;
		}
		requestRepaint();
	}

}
