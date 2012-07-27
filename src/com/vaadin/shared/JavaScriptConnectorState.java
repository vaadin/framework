/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared;

import java.util.Map;
import java.util.Set;

public interface JavaScriptConnectorState {
    public Set<String> getCallbackNames();

    public Map<String, Set<String>> getRpcInterfaces();
}