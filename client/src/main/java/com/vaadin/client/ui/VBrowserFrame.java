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
package com.vaadin.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;

public class VBrowserFrame extends Widget {

    protected IFrameElement iframe;
    protected Element altElement;
    protected String altText;

    public static final String CLASSNAME = "v-browserframe";

    public VBrowserFrame() {
        Element root = Document.get().createDivElement();
        setElement(root);

        setStyleName(CLASSNAME);

        createAltTextElement();
    }

    /**
     * Always creates new iframe inside widget. Will replace previous iframe.
     * 
     * @return
     */
    protected IFrameElement createIFrameElement(String src) {
        String name = null;

        // Remove alt text
        if (altElement != null) {
            getElement().removeChild(altElement);
            altElement = null;
        }

        // Remove old iframe
        if (iframe != null) {
            name = iframe.getAttribute("name");
            getElement().removeChild(iframe);
            iframe = null;
        }

        iframe = Document.get().createIFrameElement();
        iframe.setSrc(src);
        iframe.setFrameBorder(0);
        iframe.setAttribute("width", "100%");
        iframe.setAttribute("height", "100%");
        iframe.setAttribute("allowTransparency", "true");

        getElement().appendChild(iframe);

        // Reset old attributes (except src)
        if (name != null) {
            iframe.setName(name);
        }

        return iframe;
    }

    protected void createAltTextElement() {
        if (iframe != null) {
            return;
        }

        if (altElement == null) {
            altElement = Document.get().createSpanElement();
            getElement().appendChild(altElement);
        }

        if (altText != null) {
            altElement.setInnerText(altText);
        } else {
            altElement.setInnerText("");
        }
    }

    public void setAlternateText(String altText) {
        if (this.altText != altText) {
            this.altText = altText;
            if (altElement != null) {
                if (altText != null) {
                    altElement.setInnerText(altText);
                } else {
                    altElement.setInnerText("");
                }
            }
        }
    }

    /**
     * Set the source (the "src" attribute) of iframe. Will replace old iframe
     * with new.
     * 
     * @param source
     *            Source of iframe.
     */
    public void setSource(String source) {

        if (source == null) {
            if (iframe != null) {
                getElement().removeChild(iframe);
                iframe = null;
            }
            createAltTextElement();
            setAlternateText(altText);
            return;
        }

        if (iframe == null || iframe.getSrc() != source) {
            createIFrameElement(source);
        }
    }

    public void setName(String name) {
        if (iframe != null) {
            iframe.setName(name);
        }
    }

    @Override
    protected void onDetach() {
        if (BrowserInfo.get().isIE()) {
            // Force browser to fire unload event when component is detached
            // from the view (IE doesn't do this automatically)
            if (iframe != null) {
                /*
                 * src was previously set to javascript:false, but this was not
                 * enough to overcome a bug when detaching an iframe with a pdf
                 * loaded in IE9. about:blank seems to cause the adobe reader
                 * plugin to unload properly before the iframe is removed. See
                 * #7855
                 */
                iframe.setSrc("about:blank");
            }
        }
        super.onDetach();
    }
}
