/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.ui.Form;

@Component(Form.class)
public class FormConnector extends AbstractComponentContainerConnector
        implements Paintable, SimpleManagedLayout {

    @Override
    public void init() {
        VForm form = getWidget();
        getLayoutManager().registerDependency(this, form.footerContainer);
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        boolean legendEmpty = true;
        if (getState().getCaption() != null) {
            getWidget().caption.setInnerText(getState().getCaption());
            legendEmpty = false;
        } else {
            getWidget().caption.setInnerText("");
        }
        if (getState().getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(client);
                getWidget().legend.insertFirst(getWidget().icon.getElement());
            }
            getWidget().icon.setUri(getState().getIcon().getURL());
            legendEmpty = false;
        } else {
            if (getWidget().icon != null) {
                getWidget().legend.removeChild(getWidget().icon.getElement());
            }
        }
        if (legendEmpty) {
            getWidget().addStyleDependentName("nocaption");
        } else {
            getWidget().removeStyleDependentName("nocaption");
        }

        if (null != getState().getErrorMessage()) {
            getWidget().errorMessage
                    .updateMessage(getState().getErrorMessage());
            getWidget().errorMessage.setVisible(true);
        } else {
            getWidget().errorMessage.setVisible(false);
        }

        if (getState().hasDescription()) {
            getWidget().desc.setInnerHTML(getState().getDescription());
            if (getWidget().desc.getParentElement() == null) {
                getWidget().fieldSet.insertAfter(getWidget().desc,
                        getWidget().legend);
            }
        } else {
            getWidget().desc.setInnerHTML("");
            if (getWidget().desc.getParentElement() != null) {
                getWidget().fieldSet.removeChild(getWidget().desc);
            }
        }

        // first render footer so it will be easier to handle relative height of
        // main layout
        if (getState().getFooter() != null) {
            // render footer
            ComponentConnector newFooter = (ComponentConnector) getState()
                    .getFooter();
            Widget newFooterWidget = newFooter.getWidget();
            if (getWidget().footer == null) {
                getWidget().add(newFooter.getWidget(),
                        getWidget().footerContainer);
                getWidget().footer = newFooterWidget;
            } else if (newFooter != getWidget().footer) {
                getWidget().remove(getWidget().footer);
                getWidget().add(newFooter.getWidget(),
                        getWidget().footerContainer);
            }
            getWidget().footer = newFooterWidget;
        } else {
            if (getWidget().footer != null) {
                getWidget().remove(getWidget().footer);
            }
        }

        ComponentConnector newLayout = (ComponentConnector) getState()
                .getLayout();
        Widget newLayoutWidget = newLayout.getWidget();
        if (getWidget().lo == null) {
            // Layout not rendered before
            getWidget().lo = newLayoutWidget;
            getWidget().add(newLayoutWidget, getWidget().fieldContainer);
        } else if (getWidget().lo != newLayoutWidget) {
            // Layout has changed
            getWidget().remove(getWidget().lo);
            getWidget().lo = newLayoutWidget;
            getWidget().add(newLayoutWidget, getWidget().fieldContainer);
        }

        // also recalculates size of the footer if undefined size form - see
        // #3710
        client.runDescendentsLayout(getWidget());

        // We may have actions attached
        if (uidl.getChildCount() >= 1) {
            UIDL childUidl = uidl.getChildByTagName("actions");
            if (childUidl != null) {
                if (getWidget().shortcutHandler == null) {
                    getWidget().shortcutHandler = new ShortcutActionHandler(
                            getConnectorId(), client);
                    getWidget().keyDownRegistration = getWidget()
                            .addDomHandler(getWidget(), KeyDownEvent.getType());
                }
                getWidget().shortcutHandler.updateActionMap(childUidl);
            }
        } else if (getWidget().shortcutHandler != null) {
            getWidget().keyDownRegistration.removeHandler();
            getWidget().shortcutHandler = null;
            getWidget().keyDownRegistration = null;
        }
    }

    public void updateCaption(ComponentConnector component) {
        // NOP form don't render caption for neither field layout nor footer
        // layout
    }

    @Override
    public VForm getWidget() {
        return (VForm) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VForm.class);
    }

    public void layout() {
        VForm form = getWidget();

        LayoutManager lm = getLayoutManager();
        int footerHeight = lm.getOuterHeight(form.footerContainer)
                - lm.getMarginTop(form.footerContainer);

        form.fieldContainer.getStyle().setPaddingBottom(footerHeight, Unit.PX);
        form.footerContainer.getStyle().setMarginTop(-footerHeight, Unit.PX);
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().isPropertyReadOnly();
    }

    @Override
    public FormState getState() {
        return (FormState) super.getState();
    }

}
