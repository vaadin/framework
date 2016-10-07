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
package com.vaadin.v7.client.ui;

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.v7.shared.AbstractLegacyComponentState;

/**
 * Legacy connector for Vaadin 7 compatibility connectors. Needed because
 * <code>immediate</code> has been removed from {@link AbstractConnector} in
 * Vaadin 8.
 *
 * @author Vaadin Ltd
 * @since 8.0
 * @deprecated only used for Vaadin 7 compatiblity components
 */
@Deprecated
public class AbstractLegacyComponentConnector
        extends AbstractComponentConnector {

    // overridden to be visible to VUpload in the same package. Without making
    // it public in VUploadConnector
    @Override
    protected <T extends ServerRpc> T getRpcProxy(Class<T> rpcInterface) {
        return super.getRpcProxy(rpcInterface);
    }

    @Override
    public AbstractLegacyComponentState getState() {
        return (AbstractLegacyComponentState) super.getState();
    }
}
