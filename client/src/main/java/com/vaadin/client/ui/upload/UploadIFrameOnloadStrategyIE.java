/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui.upload;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ui.VUpload;

/**
 * IE does not have onload, detect onload via readystatechange
 * 
 */
public class UploadIFrameOnloadStrategyIE extends UploadIFrameOnloadStrategy {
    @Override
    public native void hookEvents(Element iframe, VUpload upload)
    /*-{
      iframe.onreadystatechange = $entry(function() {
        if (iframe.readyState == 'complete') {
          upload.@com.vaadin.client.ui.VUpload::onSubmitComplete()();
        }
      });
    }-*/;

    @Override
    public native void unHookEvents(Element iframe)
    /*-{
      iframe.onreadystatechange = null;
    }-*/;

}
