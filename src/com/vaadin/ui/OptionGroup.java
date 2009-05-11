/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * Configures select to be used as an option group.
 */
@SuppressWarnings("serial")
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

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "optiongroup");
        super.paintContent(target);
    }

}
