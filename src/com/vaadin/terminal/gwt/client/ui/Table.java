/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HasWidgets;
import com.vaadin.terminal.gwt.client.ComponentConnector;

public interface Table extends ComponentConnector, HasWidgets {
    final int SELECT_MODE_NONE = 0;
    final int SELECT_MODE_SINGLE = 1;
    final int SELECT_MODE_MULTI = 2;

}
