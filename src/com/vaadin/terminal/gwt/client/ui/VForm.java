/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.VErrorMessage;

public class VForm extends ComplexPanel implements Container, KeyDownHandler {

    protected String id;

    private String height = "";

    private String width = "";

    public static final String CLASSNAME = "v-form";

    private Container lo;
    private Element legend = DOM.createLegend();
    private Element caption = DOM.createSpan();
    private Element errorIndicatorElement = DOM.createDiv();
    private Element desc = DOM.createDiv();
    private Icon icon;
    private VErrorMessage errorMessage = new VErrorMessage();

    private Element fieldContainer = DOM.createDiv();

    private Element footerContainer = DOM.createDiv();

    private Element fieldSet = DOM.createFieldSet();

    private Container footer;

    private ApplicationConnection client;

    private RenderInformation renderInformation = new RenderInformation();

    private int borderPaddingHorizontal = -1;

    private boolean rendering = false;

    ShortcutActionHandler shortcutHandler;

    private HandlerRegistration keyDownRegistration;

    public VForm() {
        setElement(DOM.createDiv());
        getElement().appendChild(fieldSet);
        setStyleName(CLASSNAME);
        fieldSet.appendChild(legend);
        legend.appendChild(caption);
        errorIndicatorElement.setClassName("v-errorindicator");
        errorIndicatorElement.getStyle().setDisplay(Display.NONE);
        errorIndicatorElement.setInnerText(" "); // needed for IE
        desc.setClassName("v-form-description");
        fieldSet.appendChild(desc); // Adding description for initial padding
                                    // measurements, removed later if no
                                    // description is set
        fieldSet.appendChild(fieldContainer);
        errorMessage.setVisible(false);
        errorMessage.setStyleName(CLASSNAME + "-errormessage");
        fieldSet.appendChild(errorMessage.getElement());
        fieldSet.appendChild(footerContainer);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;
        this.client = client;
        id = uidl.getId();

        if (client.updateComponent(this, uidl, false)) {
            rendering = false;
            return;
        }

        boolean legendEmpty = true;
        if (uidl.hasAttribute("caption")) {
            caption.setInnerText(uidl.getStringAttribute("caption"));
            legendEmpty = false;
        } else {
            caption.setInnerText("");
        }
        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                legend.insertFirst(icon.getElement());
            }
            icon.setUri(uidl.getStringAttribute("icon"));
            legendEmpty = false;
        } else {
            if (icon != null) {
                legend.removeChild(icon.getElement());
            }
        }
        if (legendEmpty) {
            addStyleDependentName("nocaption");
        } else {
            removeStyleDependentName("nocaption");
        }

        if (uidl.hasAttribute("error")) {
            final UIDL errorUidl = uidl.getErrors();
            errorMessage.updateFromUIDL(errorUidl);
            errorMessage.setVisible(true);

        } else {
            errorMessage.setVisible(false);
        }

        if (uidl.hasAttribute("description")) {
            desc.setInnerHTML(uidl.getStringAttribute("description"));
            if (desc.getParentElement() == null) {
                fieldSet.insertAfter(desc, legend);
            }
        } else {
            desc.setInnerHTML("");
            if (desc.getParentElement() != null) {
                fieldSet.removeChild(desc);
            }
        }

        updateSize();

        // first render footer so it will be easier to handle relative height of
        // main layout
        if (uidl.getChildCount() > 1
                && !uidl.getChildUIDL(1).getTag().equals("actions")) {
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
            // needed for the main layout to know the space it has available
            updateSize();
        } else {
            if (footer != null) {
                remove((Widget) footer);
                client.unregisterPaintable(footer);
                // needed for the main layout to know the space it has available
                updateSize();
            }
        }

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

        // also recalculates size of the footer if undefined size form - see
        // #3710
        updateSize();
        client.runDescendentsLayout(this);

        // We may have actions attached
        if (uidl.getChildCount() > 1) {
            UIDL childUidl = uidl.getChildByTagName("actions");
            if (childUidl != null) {
                if (shortcutHandler == null) {
                    shortcutHandler = new ShortcutActionHandler(id, client);
                    keyDownRegistration = addDomHandler(this,
                            KeyDownEvent.getType());
                }
                shortcutHandler.updateActionMap(childUidl);
            }
        } else if (shortcutHandler != null) {
            keyDownRegistration.removeHandler();
            shortcutHandler = null;
            keyDownRegistration = null;
        }

        rendering = false;
    }

    public void updateSize() {

        renderInformation.updateSize(getElement());

        renderInformation.setContentAreaHeight(renderInformation
                .getRenderedSize().getHeight() - getSpaceConsumedVertically());
        if (BrowserInfo.get().isIE6()) {
            getElement().getStyle().setProperty("overflow", "hidden");
        }
        renderInformation.setContentAreaWidth(renderInformation
                .getRenderedSize().getWidth() - borderPaddingHorizontal);
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        if (child == lo) {
            return renderInformation.getContentAreaSize();
        } else if (child == footer) {
            return new RenderSpace(renderInformation.getContentAreaSize()
                    .getWidth(), 0);
        } else {
            VConsole.error("Invalid child requested RenderSpace information");
            return null;
        }
    }

    public boolean hasChildComponent(Widget component) {
        return component != null && (component == lo || component == footer);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (!hasChildComponent(oldComponent)) {
            throw new IllegalArgumentException(
                    "Old component is not inside this Container");
        }
        remove(oldComponent);
        if (oldComponent == lo) {
            lo = (Container) newComponent;
            add((Widget) lo, fieldContainer);
        } else {
            footer = (Container) newComponent;
            add((Widget) footer, footerContainer);
        }

    }

    public boolean requestLayout(Set<Paintable> child) {

        if (height != null && !"".equals(height) && width != null
                && !"".equals(width)) {
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
        // NOP form don't render caption for neither field layout nor footer
        // layout
    }

    @Override
    public void setHeight(String height) {
        if (this.height.equals(height)) {
            return;
        }

        this.height = height;
        super.setHeight(height);

        updateSize();
    }

    /**
     * @return pixels consumed by decoration, captions, descrioptiosn etc.. In
     *         other words space, not used by the actual layout in form.
     */
    private int getSpaceConsumedVertically() {
        int offsetHeight2 = fieldSet.getOffsetHeight();
        int offsetHeight3 = fieldContainer.getOffsetHeight();
        int borderPadding = offsetHeight2 - offsetHeight3;
        return borderPadding;
    }

    @Override
    public void setWidth(String width) {
        if (borderPaddingHorizontal < 0) {
            // measure excess size lazily after stylename setting, but before
            // setting width
            int ow = getOffsetWidth();
            int dow = desc.getOffsetWidth();
            borderPaddingHorizontal = ow - dow;
        }
        if (Util.equals(this.width, width)) {
            return;
        }

        this.width = width;
        super.setWidth(width);

        updateSize();

        if (!rendering && height.equals("")) {
            // Width might affect height
            Util.updateRelativeChildrenAndSendSizeUpdateEvent(client, this);
        }
    }

    public void onKeyDown(KeyDownEvent event) {
        shortcutHandler.handleKeyboardEvent(Event.as(event.getNativeEvent()));
    }
}
