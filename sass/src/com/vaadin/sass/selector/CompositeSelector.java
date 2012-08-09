/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.selector;

import org.w3c.css.sac.Selector;

public class CompositeSelector implements Selector {
    public static final short SCSS_COMPOSITE_SELECTOR = 100;
    private Selector first;
    private Selector second;

    public CompositeSelector(Selector first, Selector second) {
        this.first = first;
        this.second = second;
    }

    public Selector getFirst() {
        return first;
    }

    public Selector getSecond() {
        return second;
    }

    @Override
    public short getSelectorType() {
        return SCSS_COMPOSITE_SELECTOR;
    }
}
