package com.vaadin.server;

import com.vaadin.ui.UI;

public class BrowserPopupUIProvider extends UIProvider {

    private final String path;
    private final Class<? extends UI> uiClass;

    public BrowserPopupUIProvider(Class<? extends UI> uiClass, String path) {
        this.path = ensureInitialSlash(path);
        this.uiClass = uiClass;
    }

    private static String ensureInitialSlash(String path) {
        if (path == null) {
            return null;
        } else if (!path.startsWith("/")) {
            return '/' + path;
        } else {
            return path;
        }
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String requestPathInfo = event.getRequest().getRequestPathInfo();
        if (path.equals(requestPathInfo)) {
            return uiClass;
        } else {
            return null;
        }
    }
}
