/**
 * 
 */
package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Configures select to be used as an option group.
 */
public class OptionGroup extends AbstractSelect {

	public OptionGroup() {
		super();
	}

	public OptionGroup(String caption, Collection options) {
		super(caption, options);
	}

	public OptionGroup(String caption, Container dataSource) {
		super(caption, dataSource);
	}

	public OptionGroup(String caption) {
		super(caption);
	}

	public void paintContent(PaintTarget target) throws PaintException {
		target.addAttribute("type", "optiongroup");
		super.paintContent(target);
	}

}
