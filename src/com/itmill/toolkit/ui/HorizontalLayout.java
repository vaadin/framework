package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Horizontal layout
 * 
 * <code>HorizontalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (horizontally).
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.3
 */
public class HorizontalLayout extends AbstractOrderedLayout {

    public HorizontalLayout() {

    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Adds the attributes: orientation
        target.addAttribute("orientation", "horizontal");

    }

}
