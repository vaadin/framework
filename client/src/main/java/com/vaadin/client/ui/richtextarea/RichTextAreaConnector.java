/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.ui.richtextarea;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VRichTextArea;
import com.vaadin.client.ui.textfield.ValueChangeHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.richtextarea.RichTextAreaClientRpc;
import com.vaadin.shared.ui.richtextarea.RichTextAreaServerRpc;
import com.vaadin.shared.ui.richtextarea.RichTextAreaState;
import com.vaadin.ui.RichTextArea;

/**
 * Connector for RichTextArea.
 */
@Connect(value = RichTextArea.class, loadStyle = LoadStyle.LAZY)
public class RichTextAreaConnector extends AbstractFieldConnector
        implements SimpleManagedLayout, ValueChangeHandler.Owner {

    private class RichTextAreaClientRpcImpl implements RichTextAreaClientRpc {
        @Override
        public void selectAll() {
            getWidget().selectAll();
        }
    }

    private ValueChangeHandler valueChangeHandler;

    @Override
    protected void init() {
        getWidget().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                flush();
            }
        });
        getWidget().addInputHandler(() -> {
            valueChangeHandler.scheduleValueChange();
        });

        registerRpc(RichTextAreaClientRpc.class,
                new RichTextAreaClientRpcImpl());
        ConnectorFocusAndBlurHandler.addHandlers(this);

        valueChangeHandler = new ValueChangeHandler(this);

        getLayoutManager().registerDependency(this,
                getWidget().formatter.getElement());
    }

    @OnStateChange("valueChangeMode")
    private void updateValueChangeMode() {
        valueChangeHandler.setValueChangeMode(getState().valueChangeMode);
    }

    @OnStateChange("valueChangeTimeout")
    private void updateValueChangeTimeout() {
        valueChangeHandler.setValueChangeTimeout(getState().valueChangeTimeout);
    }

    @OnStateChange("readOnly")
    private void updateReadOnly() {
        getWidget().setReadOnly(getState().readOnly);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        getLayoutManager().unregisterDependency(this,
                getWidget().formatter.getElement());
    }

    @Override
    public VRichTextArea getWidget() {
        return (VRichTextArea) super.getWidget();
    }

    @Override
    public void layout() {
        if (!isUndefinedHeight()) {
            int rootElementInnerHeight = getLayoutManager()
                    .getInnerHeight(getWidget().getElement());
            int formatterHeight = getLayoutManager()
                    .getOuterHeight(getWidget().formatter.getElement());
            int editorHeight = rootElementInnerHeight - formatterHeight;
            if (editorHeight < 0) {
                editorHeight = 0;
            }
            getWidget().rta.setHeight(editorHeight + "px");
        }
    }

    @Override
    public RichTextAreaState getState() {
        return (RichTextAreaState) super.getState();
    }

    private boolean hasStateChanged(String widgetValue) {
        return !widgetValue.equals(getState().value);
    }

    @Override
    public void sendValueChange() {
        String widgetValue = getWidget().getSanitizedValue();
        if (!hasStateChanged(widgetValue)) {
            return;
        }

        getRpcProxy(RichTextAreaServerRpc.class).setText(widgetValue);
        getState().value = widgetValue;

    }

    @Override
    public void flush() {
        super.flush();
        sendValueChange();
    }
}
