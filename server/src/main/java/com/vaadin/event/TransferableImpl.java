/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Component;

/**
 * TODO Javadoc!
 *
 * @since 6.3
 */
public class TransferableImpl implements Transferable {
    private Map<String, Object> rawVariables = new HashMap<String, Object>();
    private Component sourceComponent;

    public TransferableImpl(Component sourceComponent,
            Map<String, Object> rawVariables) {
        this.sourceComponent = sourceComponent;
        this.rawVariables = rawVariables;
    }

    @Override
    public Component getSourceComponent() {
        return sourceComponent;
    }

    @Override
    public Object getData(String dataFlavor) {
        return rawVariables.get(dataFlavor);
    }

    @Override
    public void setData(String dataFlavor, Object value) {
        rawVariables.put(dataFlavor, value);
    }

    @Override
    public Collection<String> getDataFlavors() {
        return rawVariables.keySet();
    }

}
