/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VFormLayout;

/**
 * FormLayout is used by {@link Form} to layout fields. It may also be used
 * separately without {@link Form}.
 * 
 * FormLayout is a close relative to vertical {@link OrderedLayout}, but in
 * FormLayout caption is rendered on left side of component. Required and
 * validation indicators are between captions and fields.
 * 
 * FormLayout does not currently support some advanced methods from
 * OrderedLayout like setExpandRatio and setComponentAlignment.
 * 
 * FormLayout by default has component spacing on. Also margin top and margin
 * bottom are by default on.
 * 
 */
@SuppressWarnings({ "deprecation", "serial" })
@ClientWidget(VFormLayout.class)
public class FormLayout extends OrderedLayout {

    public FormLayout() {
        super();
        setSpacing(true);
        setMargin(true, false, true, false);
    }

}
