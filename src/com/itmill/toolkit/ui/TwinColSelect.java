/**
 * 
 */
package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Multiselect component with two lists: left side for available items and right
 * side for selected items.
 */
public class TwinColSelect extends AbstractSelect {

    /**
     * 
     */
    public TwinColSelect() {
        super();
        setMultiSelect(true);
    }

    /**
     * @param caption
     */
    public TwinColSelect(String caption) {
        super(caption);
        setMultiSelect(true);
    }

    /**
     * @param caption
     * @param dataSource
     */
    public TwinColSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        setMultiSelect(true);
    }

    /**
     * @param caption
     * @param options
     */
    public TwinColSelect(String caption, Collection options) {
        super(caption, options);
        setMultiSelect(true);
    }

    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "twincol");
        super.paintContent(target);
    }

}
