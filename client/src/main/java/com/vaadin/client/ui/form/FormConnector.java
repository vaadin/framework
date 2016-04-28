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
package com.vaadin.client.ui.form;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Paintable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.VCaption;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.VForm;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.form.FormState;
import com.vaadin.ui.Form;

@Connect(Form.class)
public class FormConnector extends AbstractComponentContainerConnector
        implements Paintable, MayScrollChildren {

    private final ElementResizeListener footerResizeListener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            VForm form = getWidget();

            LayoutManager lm = getLayoutManager();
            int footerHeight = 0;
            if (form.footer != null) {
                footerHeight += lm.getOuterHeight(form.footer.getElement());
            }

            if (form.errorMessage.isVisible()) {
                footerHeight += lm.getOuterHeight(form.errorMessage
                        .getElement());
                footerHeight -= lm.getMarginTop(form.errorMessage.getElement());
                form.errorMessage.getElement().getStyle()
                        .setMarginTop(-footerHeight, Unit.PX);
                form.footerContainer.getStyle().clearMarginTop();
            } else {
                form.footerContainer.getStyle().setMarginTop(-footerHeight,
                        Unit.PX);
            }

            form.fieldContainer.getStyle().setPaddingBottom(footerHeight,
                    Unit.PX);
        }
    };

    @Override
    protected void init() {
        getLayoutManager().addElementResizeListener(
                getWidget().errorMessage.getElement(), footerResizeListener);
    }

    @Override
    public void onUnregister() {
        VForm form = getWidget();
        getLayoutManager().removeElementResizeListener(
                form.errorMessage.getElement(), footerResizeListener);
        if (form.footer != null) {
            getLayoutManager().removeElementResizeListener(
                    form.footer.getElement(), footerResizeListener);
        }
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        boolean legendEmpty = true;
        if (getState().caption != null) {
            VCaption.setCaptionText(getWidget().caption, getState());
            legendEmpty = false;
        } else {
            getWidget().caption.setInnerText("");
        }
        if (getWidget().icon != null) {
            getWidget().legend.removeChild(getWidget().icon.getElement());
        }
        if (getIconUri() != null) {
            getWidget().icon = client.getIcon(getIconUri());
            getWidget().legend.insertFirst(getWidget().icon.getElement());

            legendEmpty = false;
        }
        if (legendEmpty) {
            getWidget().addStyleDependentName("nocaption");
        } else {
            getWidget().removeStyleDependentName("nocaption");
        }

        if (null != getState().errorMessage) {
            getWidget().errorMessage.updateMessage(getState().errorMessage);
            getWidget().errorMessage.setVisible(true);
        } else {
            getWidget().errorMessage.setVisible(false);
        }

        if (ComponentStateUtil.hasDescription(getState())) {
            getWidget().desc.setInnerHTML(getState().description);
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

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP form don't render caption for neither field layout nor footer
        // layout
    }

    @Override
    public VForm getWidget() {
        return (VForm) super.getWidget();
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().propertyReadOnly;
    }

    @Override
    public FormState getState() {
        return (FormState) super.getState();
    }

    private ComponentConnector getFooter() {
        return (ComponentConnector) getState().footer;
    }

    private ComponentConnector getLayout() {
        return (ComponentConnector) getState().layout;
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        Widget newFooterWidget = null;
        ComponentConnector footer = getFooter();

        if (footer != null) {
            newFooterWidget = footer.getWidget();
            Widget currentFooter = getWidget().footer;
            if (currentFooter != null) {
                // Remove old listener
                getLayoutManager().removeElementResizeListener(
                        currentFooter.getElement(), footerResizeListener);
            }
            getLayoutManager().addElementResizeListener(
                    newFooterWidget.getElement(), footerResizeListener);
        }
        getWidget().setFooterWidget(newFooterWidget);

        Widget newLayoutWidget = null;
        ComponentConnector newLayout = getLayout();
        if (newLayout != null) {
            newLayoutWidget = newLayout.getWidget();
        }
        getWidget().setLayoutWidget(newLayoutWidget);
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        // Form shows its description and error message
        // as a part of the actual layout
        return null;
    }

    @Override
    public boolean hasTooltip() {
        return false;
    }
}
