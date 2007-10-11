/**
 * 
 */
package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Configures select to be used as an option group.
 */
public class OptionGroup extends Select {

	public void paintContent(PaintTarget target) throws PaintException {
		target.addAttribute("type", "optiongroup");
		super.paintContent(target);
	}

}
