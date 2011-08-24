/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.google.gwt.dom.client;

/**
 * Overridden to workaround GWT issue #6194. Remove this when updating to a
 * newer GWT that fixes the problem (2.3.0 possibly). Must be in this package as
 * the whole DOMImpl hierarchy is package private and I really did not want to
 * copy all the parent classes into this one...
 */
class VaadinDOMImplSafari extends DOMImplSafari {
    @Override
    public int getAbsoluteLeft(Element elem) {
        // Chrome returns a float in certain cases (at least when zoom != 100%).
        // The |0 ensures it is converted to an int.
        return super.getAbsoluteLeft(elem) | 0;
    }

    @Override
    public int getAbsoluteTop(Element elem) {
        // Chrome returns a float in certain cases (at least when zoom != 100%).
        // The |0 ensures it is converted to an int.
        return super.getAbsoluteTop(elem) | 0;
    }
}
