package com.itmill.toolkit.ui;

import java.util.Collection;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * This is a simple list select without, for instance, support for new items,
 * lazyloading, and other advanced features.
 */
public class ListSelect extends AbstractSelect {

    public ListSelect() {
        super();
    }

    public ListSelect(String caption, Collection options) {
        super(caption, options);
    }

    public ListSelect(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public ListSelect(String caption) {
        super(caption);
    }

    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "list");
        super.paintContent(target);
    }
}
