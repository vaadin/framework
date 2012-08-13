/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.extension.javascriptmanager;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.shared.communication.SharedState;

public class JavaScriptManagerState extends SharedState {
    private Set<String> names = new HashSet<String>();

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }
}
