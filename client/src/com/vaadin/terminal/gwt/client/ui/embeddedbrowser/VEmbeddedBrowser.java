package com.vaadin.terminal.gwt.client.ui.embeddedbrowser;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.Widget;

public class VEmbeddedBrowser extends Widget {

    protected IFrameElement iframe;
    protected Element altElement;
    protected String altText;

    public VEmbeddedBrowser() {
        Element root = Document.get().createDivElement();
        setElement(root);

        setStylePrimaryName("v-embeddedbrowser");

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
}
