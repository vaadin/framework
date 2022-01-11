/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.shared.communication;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ApplicationConstants;

public class LegacyChangeVariablesInvocation extends MethodInvocation {
    private Map<String, Object> variableChanges = new HashMap<String, Object>();

    public LegacyChangeVariablesInvocation(String connectorId,
            String variableName, Object value) {
        super(connectorId, ApplicationConstants.UPDATE_VARIABLE_INTERFACE,
                ApplicationConstants.UPDATE_VARIABLE_METHOD,
                new Object[] { variableName, new UidlValue(value) });
        setVariableChange(variableName, value);
    }

    public static boolean isLegacyVariableChange(String interfaceName,
            String methodName) {
        return ApplicationConstants.UPDATE_VARIABLE_METHOD.equals(interfaceName)
                && ApplicationConstants.UPDATE_VARIABLE_METHOD
                        .equals(methodName);
    }

    public void setVariableChange(String name, Object value) {
        variableChanges.put(name, value);
    }

    public Map<String, Object> getVariableChanges() {
        return variableChanges;
    }

    @Override
    public String getLastOnlyTag() {
        assert variableChanges.size() == 1;
        return super.getLastOnlyTag()
                + variableChanges.keySet().iterator().next();
    }

}
