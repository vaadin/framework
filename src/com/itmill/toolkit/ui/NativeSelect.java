/**
 * 
 */
package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Since TK5 default select is customized component with many advanced features
 * over terminals native select components. Sometimes "native" select may still
 * be the best option. Terminal renders this select with its native select
 * widget.
 */
public class NativeSelect extends AbstractSelect {

	public NativeSelect() {
		super();
	}

	public NativeSelect(String caption, Collection options) {
		super(caption, options);
	}

	public NativeSelect(String caption, Container dataSource) {
		super(caption, dataSource);
	}

	public NativeSelect(String caption) {
		super(caption);
	}

	public void paintContent(PaintTarget target) throws PaintException {
		target.addAttribute("type", "native");
		super.paintContent(target);
	}

}
