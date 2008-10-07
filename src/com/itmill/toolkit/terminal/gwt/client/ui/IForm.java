/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ClientExceptionHandler;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.IErrorMessage;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IForm extends ComplexPanel implements Container,
        ContainerResizedListener {

    private String height;

    private String width;

    public static final String CLASSNAME = "i-form";

    private Container lo;
    private Element legend = DOM.createLegend();
    private Element caption = DOM.createSpan();
    private Element errorIndicatorElement = DOM.createDiv();
    private Element desc = DOM.createDiv();
    private Icon icon;
    private IErrorMessage errorMessage = new IErrorMessage();

    private Element fieldContainer = DOM.createDiv();

    private Element footerContainer = DOM.createDiv();

    private Container footer;

    private ApplicationConnection client;

    private RenderInformation renderInformation = new RenderInformation();

    private int borderPaddingHorizontal;

    private int borderPaddingVertical;

    public IForm() {
        setElement(DOM.createFieldSet());
        setStyleName(CLASSNAME);
        DOM.appendChild(getElement(), legend);
        DOM.appendChild(legend, caption);
        DOM.setElementProperty(errorIndicatorElement, "className",
                "i-errorindicator");
        DOM.setStyleAttribute(errorIndicatorElement, "display", "none");
        DOM.setInnerText(errorIndicatorElement, " "); // needed for IE
        DOM.setElementProperty(desc, "className", "i-form-description");
        DOM.appendChild(getElement(), desc);
        DOM.appendChild(getElement(), fieldContainer);
        errorMessage.setVisible(false);
        errorMessage.setStyleName(CLASSNAME + "-errormessage");
        DOM.appendChild(getElement(), errorMessage.getElement());
        DOM.appendChild(getElement(), footerContainer);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        boolean legendEmpty = true;
        if (uidl.hasAttribute("caption")) {
            DOM.setInnerText(caption, uidl.getStringAttribute("caption"));
            legendEmpty = false;
        } else {
            DOM.setInnerText(caption, "");
        }
        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(legend, icon.getElement(), 0);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
            legendEmpty = false;
        } else {
            if (icon != null) {
                DOM.removeChild(legend, icon.getElement());
            }
        }
        if (legendEmpty) {
            DOM.setStyleAttribute(legend, "display", "none");
        } else {
            DOM.setStyleAttribute(legend, "display", "");
        }

        if (uidl.hasAttribute("error")) {
            final UIDL errorUidl = uidl.getErrors();
            errorMessage.updateFromUIDL(errorUidl);
            errorMessage.setVisible(true);

        } else {
            errorMessage.setVisible(false);
        }

        if (uidl.hasAttribute("description")) {
            DOM.setInnerHTML(desc, uidl.getStringAttribute("description"));
        } else {
            DOM.setInnerHTML(desc, "");
        }

        iLayout();

        final UIDL layoutUidl = uidl.getChildUIDL(0);
        Container newLo = (Container) client.getPaintable(layoutUidl);
        if (lo == null) {
            lo = newLo;
            add((Widget) lo, fieldContainer);
        } else if (lo != newLo) {
            client.unregisterPaintable(lo);
            remove((Widget) lo);
            lo = newLo;
            add((Widget) lo, fieldContainer);
        }
        lo.updateFromUIDL(layoutUidl, client);

        if (uidl.getChildCount() > 1) {
            // render footer
            Container newFooter = (Container) client.getPaintable(uidl
                    .getChildUIDL(1));
            if (footer == null) {
                add((Widget) newFooter, footerContainer);
                footer = newFooter;
            } else if (newFooter != footer) {
                remove((Widget) footer);
                client.unregisterPaintable(footer);
                add((Widget) newFooter, footerContainer);
            }
            footer = newFooter;
            footer.updateFromUIDL(uidl.getChildUIDL(1), client);
        } else {
            if (footer != null) {
                remove((Widget) footer);
                client.unregisterPaintable(footer);
            }
        }
    }

    public void iLayout() {

        renderInformation.updateSize(getElement());

        renderInformation.setContentAreaHeight(renderInformation
                .getRenderedSize().getHeight()
                - borderPaddingVertical);
        renderInformation.setContentAreaWidth(renderInformation
                .getRenderedSize().getWidth()
                - borderPaddingHorizontal);

        client.runDescendentsLayout(this);
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        return renderInformation.getContentAreaSize();
    }

    public boolean hasChildComponent(Widget component) {
        return component != null && (component == lo || component == footer);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO Auto-generated method stub

    }

    public boolean requestLayout(Set<Paintable> child) {

        if (height != null && width != null) {
            /*
             * If the height and width has been specified the child components
             * cannot make the size of the layout change
             */

            return true;
        }

        if (renderInformation.updateSize(getElement())) {
            return false;
        } else {
            return true;
        }

    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHeight(String height) {
        this.height = height;

        super.setHeight(height);

        if (height.endsWith("px")) {
            try {
                borderPaddingVertical = getElement().getOffsetHeight()
                        - Integer.parseInt(height.substring(0,
                                height.length() - 2));
            } catch (Exception e) {
                ClientExceptionHandler.displayError(e);
            }
        }

    }

    @Override
    public void setWidth(String width) {
        this.width = width;

        super.setWidth(width);

        if (width.endsWith("px")) {
            try {
                borderPaddingHorizontal = getElement().getOffsetWidth()
                        - Integer.parseInt(width.substring(0,
                                width.length() - 2));
            } catch (Exception e) {
                ClientExceptionHandler.displayError(e);
            }
        }
    }
}
