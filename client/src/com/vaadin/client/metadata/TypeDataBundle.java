/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.client.metadata;

import com.google.gwt.core.client.RunAsyncCallback;

public abstract class TypeDataBundle implements RunAsyncCallback {
    private final String name;

    public TypeDataBundle(String name) {
        this.name = name;
    }

    @Override
    public void onSuccess() {
        ConnectorBundleLoader loader = ConnectorBundleLoader.get();
        load();
        loader.setLoaded(getName());
    }

    @Override
    public void onFailure(Throwable reason) {
        ConnectorBundleLoader.get().setLoadFailure(
                getName(),
                new RuntimeException("Failed to load bundle " + getName()
                        + ": " + reason.getMessage(), reason));
    }

    public abstract void load();

    public String getName() {
        return name;
    }
}
