/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Element;

/**
 * IE does not have onload, detect onload via readystatechange
 * 
 */
public class UploadIFrameOnloadStrategyIE extends UploadIFrameOnloadStrategy {
    @Override
    native void hookEvents(Element iframe, VUpload upload)
    /*-{
      iframe.onreadystatechange = function() {
        if (iframe.readyState == 'complete') {
          upload.@com.vaadin.terminal.gwt.client.ui.VUpload::onSubmitComplete()();
        }
      };
    }-*/;

    @Override
    native void unHookEvents(Element iframe)
    /*-{
      iframe.onreadystatechange = null;
    }-*/;

}
