package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * An abstract class that defines default implementation for the {@link Layout}
 * interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public abstract class AbstractLayout extends AbstractComponentContainer
		implements Layout {

	/**
	 * Layout edge margins, clockwise from top: top, right, bottom, left. Each
	 * is set to true, if the client-side implementation should leave extra
	 * space at that edge.
	 */
	protected boolean[] margins;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#getTag()
	 */
	public abstract String getTag();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.Layout#setMargin(boolean)
	 */
	public void setMargin(boolean enabled) {
		margins = new boolean[] { enabled, enabled, enabled, enabled };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.Layout#setMargin(boolean, boolean, boolean,
	 *      boolean)
	 */
	public void setMargin(boolean topEnabled, boolean rightEnabled,
			boolean bottomEnabled, boolean leftEnabled) {
		margins = new boolean[] { topEnabled, rightEnabled, bottomEnabled,
				leftEnabled };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#paintContent(com.itmill.toolkit.terminal.PaintTarget)
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Add margin info. Defaults to false.
		if (margins == null)
			setMargin(false);
		if (margins[0])
			target.addAttribute("marginTop", margins[0]);
		if (margins[1])
			target.addAttribute("marginRight", margins[1]);
		if (margins[2])
			target.addAttribute("marginBottom", margins[2]);
		if (margins[3])
			target.addAttribute("marginLeft", margins[3]);
	}

}
