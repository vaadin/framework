/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.v7.client.ui.form;

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
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.VForm;
import com.vaadin.v7.shared.form.FormState;
import com.vaadin.v7.ui.Form;

@Connect(Form.class)
public class FormConnector extends AbstractComponentContainerConnector
        implements Paintable, MayScrollChildren,
        com.vaadin.v7.client.ComponentConnector {

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
                footerHeight += lm
                        .getOuterHeight(form.errorMessage.getElement());
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
        VForm form = getWidget();
        form.client = client;
        form.id = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        boolean legendEmpty = true;
        if (getState().caption != null) {
            VCaption.setCaptionText(form.caption, getState());
            legendEmpty = false;
        } else {
            form.caption.setInnerText("");
        }
        if (form.icon != null) {
            form.legend.removeChild(form.icon.getElement());
        }
        if (getIconUri() != null) {
            form.icon = client.getIcon(getIconUri());
            form.legend.insertFirst(form.icon.getElement());

            legendEmpty = false;
        }
        if (legendEmpty) {
            form.addStyleDependentName("nocaption");
        } else {
            form.removeStyleDependentName("nocaption");
        }

        if (null != getState().errorMessage) {
            form.errorMessage.updateMessage(getState().errorMessage);
            form.errorMessage.updateErrorLevel(getState().errorLevel);
            form.errorMessage.setVisible(true);
        } else {
            form.errorMessage.setVisible(false);
        }

        if (ComponentStateUtil.hasDescription(getState())) {
            form.desc.setInnerHTML(getState().description);
            if (form.desc.getParentElement() == null) {
                form.fieldSet.insertAfter(form.desc,
                        form.legend);
            }
        } else {
            form.desc.setInnerHTML("");
            if (form.desc.getParentElement() != null) {
                form.fieldSet.removeChild(form.desc);
            }
        }

        // also recalculates size of the footer if undefined size form - see
        // #3710
        client.runDescendentsLayout(form);

        // We may have actions attached
        if (uidl.getChildCount() >= 1) {
            UIDL childUidl = uidl.getChildByTagName("actions");
            if (childUidl != null) {
                if (form.shortcutHandler == null) {
                    form.shortcutHandler = new ShortcutActionHandler(
                            getConnectorId(), client);
                    form.keyDownRegistration = form
                            .addDomHandler(form, KeyDownEvent.getType());
                }
                form.shortcutHandler.updateActionMap(childUidl);
            }
        } else if (form.shortcutHandler != null) {
            form.keyDownRegistration.removeHandler();
            form.shortcutHandler = null;
            form.keyDownRegistration = null;
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
        // Class hierarchy has changed for FormConnector
        return getState().readOnly || getState().propertyReadOnly;
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
