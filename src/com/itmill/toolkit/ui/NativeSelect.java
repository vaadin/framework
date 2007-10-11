/**
 * 
 */
package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Since TK5 default select is customized component with mane advanced features
 * over terminals native select components. Sometimes "native" select may still
 * be the best option. Terminal renders this select with its native select
 * widget.
 */
public class NativeSelect extends Select {

	public void paintContent(PaintTarget target) throws PaintException {
		target.addAttribute("type", "native");
		super.paintContent(target);
	}

}
